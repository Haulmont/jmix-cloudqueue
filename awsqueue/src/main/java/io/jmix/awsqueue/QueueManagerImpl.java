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

package io.jmix.awsqueue;

import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.jmix.awsqueue.entity.QueueAttributes;
import io.jmix.awsqueue.entity.QueueInfo;
import io.jmix.awsqueue.entity.QueueStatus;
import io.jmix.awsqueue.entity.QueueType;
import io.jmix.core.DataManager;
import io.jmix.core.impl.GeneratedIdEntityInitializer;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.util.*;
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
    private DataManager dataManager;

    protected ObjectMapper mapper;

    @PostConstruct
    protected void init() {
        mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);

    }

    public Map<String, QueueInfo> loadFromApi() {
        return amazonSQSAsyncClient
                .listQueues(queueProperties.getQueuePrefix())
                .getQueueUrls()
                .stream()
                .map(this::queueInfoFromUrl)
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toMap(QueueInfo::getUrl, queueInfo -> queueInfo));
    }

    @Override
    public Collection<QueueInfo> loadAll() {
        Map<String, QueueInfo> apiQueues = loadFromApi();
        invalidateCreatedQueues(apiQueues);
        invalidateDeletedQueues(apiQueues);

        Collection<QueueInfo> apiNotDeletedQueues = apiQueues.values()
                .stream()
                .filter(t -> !queueStatusCache.isOnDeletion(t.getUrl()))
                .collect(Collectors.toList());
        apiNotDeletedQueues.addAll(queueStatusCache.getCreatedQueues());

        return apiNotDeletedQueues;
    }

    protected void invalidateCreatedQueues(Map<String, QueueInfo> apiQueues) {
        for (String queueName : apiQueues.values().stream().map(QueueInfo::getName).collect(Collectors.toList())) {
            if (queueStatusCache.isOnCreation(queueName)) {
                queueStatusCache.unassignCreated(queueName);
            }
        }
    }

    protected void invalidateDeletedQueues(Map<String, QueueInfo> apiQueues) {
        for (String removedQueueUrl : queueStatusCache.getDeletedQueueUrls()) {
            if (!apiQueues.containsKey(removedQueueUrl)) {
                queueStatusCache.setTotallyDeleted(removedQueueUrl);
            }
        }
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
        queueInfo.setType(getTypeByName(queueName));
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

    private QueueType getTypeByName(String name) {
        if (name.endsWith(".fifo")) {
            return QueueType.FIFO;
        }
        return QueueType.STANDARD;
    }

    @Override
    public void createQueue(CreateQueueRequest createQueueRequest) {
        amazonSQSAsyncClient.createQueueAsync(createQueueRequest);
        queueStatusCache.setOnCreation(queueInfoFromRequest(createQueueRequest));
    }

    protected QueueInfo queueInfoFromRequest(CreateQueueRequest createQueueRequest) {
        Map<String, String> mapAttrs = createQueueRequest.getAttributes();
        QueueAttributes queueAttributes = mapper.convertValue(mapAttrs, QueueAttributes.class);
        generatedIdEntityInitializer.initEntity(queueAttributes);

        QueueInfo queueInfo = dataManager.create(QueueInfo.class);

        queueInfo.setName(queueInfo.generatePhysicalName(
                createQueueRequest.getQueueName(), queueProperties.getQueuePrefix()));
        queueInfo.setType(getTypeByName(createQueueRequest.getQueueName()));
        queueInfo.setQueueAttributes(queueAttributes);
        queueInfo.setStatus(QueueStatus.CREATING);

        return queueInfo;
    }

    @Override
    public void deleteQueue(String queueUrl) {
        amazonSQSAsyncClient.deleteQueueAsync(queueUrl);
        queueStatusCache.setOnDeletion(queueUrl);
    }
}
