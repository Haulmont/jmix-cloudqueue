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
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jmix.awsqueue.QueueManager;
import io.jmix.awsqueue.QueueProperties;
import io.jmix.awsqueue.entity.QueueAttributes;
import io.jmix.awsqueue.entity.QueueInfo;
import io.jmix.awsqueue.entity.QueueStatus;
import io.jmix.awsqueue.entity.QueueType;
import io.jmix.awsqueue.utils.QueueInfoUtils;
import io.jmix.core.DataManager;
import io.jmix.core.impl.GeneratedIdEntityInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("awsqueue_QueueManagerImpl")
public class QueueManagerImpl implements QueueManager {
    @Autowired
    protected AmazonSQSAsyncClient amazonSQSAsyncClient;
    @Autowired
    protected QueueStatusCache queueStatusCache;
    @Autowired
    protected QueueProperties queueProperties;
    @Autowired
    protected GeneratedIdEntityInitializer generatedIdEntityInitializer;
    @Autowired
    protected DataManager dataManager;

    protected ObjectMapper mapper;

    public QueueManagerImpl() {
        mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
    public void createQueue(CreateQueueRequest createQueueRequest) {
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
    }
}
