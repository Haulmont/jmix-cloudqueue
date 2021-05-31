/*
 * Copyright 2021 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jmix.awsqueue.impl;

import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jmix.awsqueue.QueueProperties;
import io.jmix.sqs.entity.QueueAttributes;
import io.jmix.sqs.entity.QueueInfo;
import io.jmix.sqs.entity.QueueStatus;
import io.jmix.sqs.entity.QueueType;
import io.jmix.awsqueue.utils.DTOMapper;
import io.jmix.sqs.utils.QueueInfoUtils;
import io.jmix.core.DataManager;
import io.jmix.core.impl.GeneratedIdEntityInitializer;
import io.jmix.sqs.api.MessageQueueHandler;
import io.jmix.sqs.api.QueueManager;
import io.jmix.sqs.utils.QueueStatusCache;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("sqs_QueueManagerImpl")
public class QueueManagerImpl implements QueueManager {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(QueueManagerImpl.class);
    @Autowired
    protected AmazonSQSAsyncClient amazonSQSAsyncClient;
    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;
    @Autowired
    protected QueueStatusCache queueStatusCache;
    @Autowired
    protected QueueProperties queueProperties;
    @Autowired
    protected GeneratedIdEntityInitializer generatedIdEntityInitializer;
    @Autowired
    protected DataManager dataManager;

    protected ObjectMapper mapper;

    private final Map<String, List<MessageQueueHandler>> queuesHandlers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService;
    private final ReceiveMessageRequest receiveRequest;

    public QueueManagerImpl(@Value("${jmix.awsqueue.listener.thread-pool-core-size:5}") int threadPoolCoreSize,
                            @Value("${jmix.awsqueue.listener.long-polling-timeout:10000}") int longPollingTimeout,
                            @Value("${jmix.awsqueue.listener.waiting-time-receive-request:5}") int waitingTimeReceiveRequest,
                            @Value("${jmix.awsqueue.listener.max-number-of-messages:10}") int maxNumberOfMessages) {
        mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        executorService = Executors.newScheduledThreadPool(threadPoolCoreSize);
        executorService.scheduleAtFixedRate(this::handleMessagesFromQueues, longPollingTimeout, longPollingTimeout, TimeUnit.MILLISECONDS);
        receiveRequest = new ReceiveMessageRequest()
                .withWaitTimeSeconds(waitingTimeReceiveRequest)
                .withMaxNumberOfMessages(maxNumberOfMessages);
    }

    @Override
    public Collection<QueueInfo> loadAll() {
        Map<String, QueueInfo> apiQueues = loadFromApi();
        queueStatusCache.invalidate(apiQueues);
        apiQueues.keySet().removeAll(queueStatusCache.getDeletedQueueUrls());
        Collection<QueueInfo> queueInfoWithCache = new ArrayList<>(apiQueues.values());
        queueInfoWithCache.addAll(queueStatusCache.getCreatingQueues());
        return queueInfoWithCache;
    }


    @Override
    public void subscribe(String queueName, MessageQueueHandler lambdaHandler) {
        queuesHandlers.computeIfPresent(queueName, (queue, messageQueueHandlers) -> {
            messageQueueHandlers.add(lambdaHandler);
            return messageQueueHandlers;
        });
        queuesHandlers.computeIfAbsent(queueName, queue -> {
            List<MessageQueueHandler> handlers = new ArrayList<>();
            handlers.add(lambdaHandler);
            return handlers;
        });
    }

    @Override
    public void sendMessage(String queueName, Message<?> message) {
        queueMessagingTemplate.send(queueName, message);
    }

    private void handleMessagesFromQueues() {
        try {
            if (!queuesHandlers.isEmpty())
                queuesHandlers.forEach((queue, handlers) -> {
                    if (!handlers.isEmpty()) {
                        receiveRequest.setQueueUrl(queue);
                        ReceiveMessageResult result = amazonSQSAsyncClient.receiveMessage(receiveRequest);

                        if (!result.getMessages().isEmpty()) {
                            io.jmix.sqs.models.ReceiveMessageResult res = DTOMapper.getModelFromAWSReceiveMessageResult(result);
                            handlers.forEach(handler -> handler.handle(res));

                            result.getMessages().forEach(message -> {
                                DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest(queue, message.getReceiptHandle());
                                amazonSQSAsyncClient.deleteMessage(deleteMessageRequest);
                            });
                        }
                    }
                });
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    private Map<String, QueueInfo> loadFromApi() {
        return amazonSQSAsyncClient
                .listQueues(queueProperties.getQueuePrefix())
                .getQueueUrls()
                .stream()
                .map(this::queueInfoFromUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(QueueInfo::getUrl, Function.identity()));
    }

    @Override
    @Nullable
    public QueueInfo queueInfoFromUrl(String queueUrl) {
        GetQueueAttributesRequest request = new GetQueueAttributesRequest()
                .withQueueUrl(queueUrl)
                .withAttributeNames("QueueArn",
                        "CreatedTimestamp",
                        "LastModifiedTimestamp",
                        "MaximumMessageSize",
                        "MessageRetentionPeriod",
                        "VisibilityTimeout",
                        "ApproximateNumberOfMessages",
                        "DelaySeconds",
                        "ApproximateNumberOfMessagesNotVisible",
                        "ReceiveMessageWaitTimeSeconds",
                        "ApproximateNumberOfMessagesDelayed");
        GetQueueAttributesResult attributesResult;
        try {
            attributesResult = amazonSQSAsyncClient.getQueueAttributes(request);
        } catch (QueueDoesNotExistException exception) {
            return null;
        }
        Map<String, String> attr = attributesResult.getAttributes();
        QueueInfo queueInfo = initQueueInfoFromAttributes(attr);
        String queueName = getNameFromAttributes(attr);

        queueInfo.setUrl(queueUrl);
        queueInfo.setName(queueName);
        queueInfo.setType(QueueInfoUtils.getTypeByName(queueName));
        queueInfo.setStatus(QueueStatus.RUNNING);

        return queueInfo;
    }

    private QueueInfo initQueueInfoFromAttributes(Map<String, String> attr) {
        QueueInfo queueInfo = mapper.convertValue(attr, QueueInfo.class);
        generatedIdEntityInitializer.initEntity(queueInfo);
        generatedIdEntityInitializer.initEntity(queueInfo.getQueueAttributes());
        return queueInfo;
    }

    private String getNameFromAttributes(Map<String, String> attr) {
        String[] arnValues = attr.get("QueueArn").split(":");
        return arnValues[arnValues.length - 1];
    }


    @Override
    public void createQueue(io.jmix.sqs.models.Queue queue) {
        createQueue(DTOMapper.getCreateQueueRequestFromModel(queue));
    }

    private void createQueue(CreateQueueRequest createQueueRequest) {
        setPhysicalNameIfNotValid(createQueueRequest);
        amazonSQSAsyncClient.createQueueAsync(createQueueRequest);
        queueStatusCache.setCreating(queueInfoFromRequest(createQueueRequest));
    }

    private void setPhysicalNameIfNotValid(CreateQueueRequest createQueueRequest) {
        String name = createQueueRequest.getQueueName();
        String prefix = queueProperties.getQueuePrefix();
        QueueType type = QueueInfoUtils.getTypeByName(name);
        createQueueRequest.setQueueName(QueueInfoUtils.generatePhysicalName(name, type, prefix));
    }

    protected QueueInfo queueInfoFromRequest(CreateQueueRequest createQueueRequest) {
        Map<String, String> mapAttrs = createQueueRequest.getAttributes();
        QueueAttributes queueAttributes = mapper.convertValue(mapAttrs, QueueAttributes.class);
        generatedIdEntityInitializer.initEntity(queueAttributes);

        QueueInfo queueInfo = dataManager.create(QueueInfo.class);
        queueInfo.setName(createQueueRequest.getQueueName());
        queueInfo.setType(QueueInfoUtils.getTypeByName(createQueueRequest.getQueueName()));
        queueInfo.setQueueAttributes(queueAttributes);
        queueInfo.setStatus(QueueStatus.CREATING);
        return queueInfo;
    }

    @Override
    public void deleteQueue(String queueUrl) {
        amazonSQSAsyncClient.deleteQueueAsync(queueUrl);
        queueStatusCache.setDeleting(queueUrl);
        queuesHandlers.remove(queueUrl);
    }
}
