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

package io.jmix.cloudqueue.impl;

import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jmix.cloudqueue.api.MessageQueueHandler;
import io.jmix.cloudqueue.api.QueueManager;
import io.jmix.cloudqueue.configuration.QueueProperties;
import io.jmix.cloudqueue.entity.QueueAttributes;
import io.jmix.cloudqueue.entity.QueueInfo;
import io.jmix.cloudqueue.entity.QueueStatus;
import io.jmix.cloudqueue.entity.QueueType;
import io.jmix.cloudqueue.utils.QueueStatusCache;
import io.jmix.cloudqueue.utils.QueueInfoUtils;
import io.jmix.core.DataManager;
import io.jmix.core.impl.GeneratedIdEntityInitializer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("cloudqueue_QueueManagerImpl")
public class QueueManagerImpl implements QueueManager {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(QueueManagerImpl.class);

    private static final String QUEUE_PARAMETER_ALL = "All";
    private static final String QUEUE_PARAMETER_QUEUE_ARN = "QueueArn";
    private static final String QUEUE_PARAMETER_SPLIT_EXP = ":";

    @Autowired
    protected AmazonSQSAsyncClient amazonSQSAsyncClient;
    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;
    @Autowired
    protected QueueStatusCache queueStatusCache;
    @Autowired
    protected GeneratedIdEntityInitializer generatedIdEntityInitializer;
    @Autowired
    protected DataManager dataManager;

    protected ObjectMapper mapper;

    private final QueueProperties queueProperties;
    private final Map<String, List<MessageQueueHandler>> queueHandlers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService;
    private final ReceiveMessageRequest receiveRequest;

    public QueueManagerImpl(QueueProperties queueProperties) {
        this.queueProperties = queueProperties;
        mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        executorService = Executors.newScheduledThreadPool(queueProperties.getListener().getThreadPoolCoreSize());
        executorService.scheduleAtFixedRate(this::handleMessagesFromQueues,
                queueProperties.getListener().getLongPollingTimeout(),
                queueProperties.getListener().getLongPollingTimeout(),
                TimeUnit.MILLISECONDS);
        receiveRequest = new ReceiveMessageRequest()
                .withWaitTimeSeconds(queueProperties.getListener().getWaitingTimeReceiveRequest())
                .withMaxNumberOfMessages(queueProperties.getListener().getMaxNumberOfMessages());
    }

    @EventListener
    public void onApplicationContextClosed(ContextClosedEvent event) {
        executorService.shutdownNow();
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
    @Nullable
    public QueueInfo queueInfoFromUrl(String queueUrl) {
        GetQueueAttributesRequest request = new GetQueueAttributesRequest()
                .withQueueUrl(queueUrl)
                .withAttributeNames(QUEUE_PARAMETER_ALL);
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

    @Override
    public void createQueue(CreateQueueRequest createQueueRequest) {
        setPhysicalNameIfNotValid(createQueueRequest);
        amazonSQSAsyncClient.createQueueAsync(createQueueRequest);
        queueStatusCache.setCreating(queueInfoFromRequest(createQueueRequest));
    }

    @Override
    public void deleteQueue(String queueUrl) {
        amazonSQSAsyncClient.deleteQueueAsync(queueUrl);
        queueStatusCache.setDeleting(queueUrl);
        queueHandlers.remove(queueUrl);
    }

    @Override
    public void subscribe(String queueName, MessageQueueHandler lambdaHandler) {
        String queueUrl = getQueueUrlByName(queueName);
        queueHandlers.computeIfAbsent(queueUrl, queue -> new ArrayList<>());
        queueHandlers.get(queueUrl).add(lambdaHandler);
    }

    @Override
    public void sendMessage(String queueName, Message<?> message) {
        queueMessagingTemplate.send(queueName, message);
    }

    private String getQueueUrlByName(String name) {
        String queueUrl = name;
        Optional<QueueInfo> optionalQueueInfo = loadFromApi().values()
                .stream()
                .filter(queueInfo -> queueInfo.getName().equals(name))
                .findFirst();
        if (optionalQueueInfo.isPresent()) queueUrl = optionalQueueInfo.get().getUrl();
        return queueUrl;
    }

    private void handleMessagesFromQueues() {
        try {
            if (!queueHandlers.isEmpty())
                queueHandlers.forEach((queue, handlers) -> {
                    if (!handlers.isEmpty()) {
                        receiveRequest.setQueueUrl(queue);
                        ReceiveMessageResult result = amazonSQSAsyncClient.receiveMessage(receiveRequest);

                        if (!result.getMessages().isEmpty()) {
                            handlers.forEach(handler ->
                                    handler.handle(result));
                            result.getMessages().forEach(message ->
                                    amazonSQSAsyncClient.deleteMessage(new DeleteMessageRequest(queue, message.getReceiptHandle())));
                        }
                    }
                });
        } catch (Exception e) {
            log.error("In during handling message from queue occurred exception: {}", e.getMessage());
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

    private QueueInfo initQueueInfoFromAttributes(Map<String, String> attr) {
        QueueInfo queueInfo = mapper.convertValue(attr, QueueInfo.class);
        generatedIdEntityInitializer.initEntity(queueInfo);
        generatedIdEntityInitializer.initEntity(queueInfo.getQueueAttributes());
        return queueInfo;
    }

    private String getNameFromAttributes(Map<String, String> attr) {
        String[] arnValues = attr.get(QUEUE_PARAMETER_QUEUE_ARN).split(QUEUE_PARAMETER_SPLIT_EXP);
        return arnValues[arnValues.length - 1];
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
}
