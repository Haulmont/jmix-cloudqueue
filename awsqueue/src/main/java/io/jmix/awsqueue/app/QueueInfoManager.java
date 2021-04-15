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

package io.jmix.awsqueue.app;

import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jmix.awsqueue.QueueProperties;
import io.jmix.awsqueue.entity.QueueInfo;
import io.jmix.awsqueue.entity.QueueStatus;
import io.jmix.awsqueue.entity.QueueType;
import io.jmix.core.DataManager;
import io.jmix.core.Metadata;
import io.jmix.core.impl.GeneratedIdEntityInitializer;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("QueueInfoManager")
public class QueueInfoManager {
    private static final Logger log = LoggerFactory.getLogger(QueueInfoManager.class);
    private static final String APPLICATION_TAG_KEY = "ApplicationTag";

    @Autowired
    protected AmazonSQSAsyncClient amazonSQSAsyncClient;
    @Autowired
    protected QueueStatusCache queueStatusCache;
    @Autowired
    protected QueueProperties queueProperties;
    @Autowired
    protected GeneratedIdEntityInitializer generatedIdEntityInitializer;

    protected ObjectMapper mapper;

    @PostConstruct
    protected void init() {
        mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<QueueInfo> loadFromApi() {
        return amazonSQSAsyncClient
                .listQueues()
                .getQueueUrls()
                .stream()
                .filter(this::isInternalExistingQueue)
                .map(this::queueInfoFromUrl)
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toList());
    }

    protected boolean isInternalExistingQueue(String queueUrl) {
        if (prefixExists()) {
            try {
                ListQueueTagsResult result = amazonSQSAsyncClient.listQueueTags(queueUrl);
                return result.getTags().containsKey(APPLICATION_TAG_KEY) &&
                        result.getTags().get(APPLICATION_TAG_KEY).equals(getApplicationPrefix());
            } catch (QueueDoesNotExistException exception) {
                return false;
            }
        }
        return true;
    }

    public List<QueueInfo> loadAll() {
        List<QueueInfo> resultQueues = loadFromApi();
        syncCacheWithApi(resultQueues);
        resultQueues.addAll(queueStatusCache.getPendingQueues());
        return resultQueues;
    }

    protected void syncCacheWithApi(List<QueueInfo> apiQueues) {
        for (String queueName : queueStatusCache.getPendingNames()) {
            if (apiQueues.stream()
                    .anyMatch(t -> t.getName().equals(queueName))) {
                QueueInfo queueInfo = queueStatusCache.getPendingQueue(queueName);
                if (queueInfo.getStatus().equals(QueueStatus.ON_CREATE)) {
                    queueStatusCache.removeFromCache(queueName);
                }
            } else if (apiQueues.stream()
                    .noneMatch(t -> t.getName().equals(queueName))) {
                QueueInfo queueInfo = queueStatusCache.getPendingQueue(queueName);
                if (queueInfo.getStatus().equals(QueueStatus.ON_DELETE)) {
                    queueStatusCache.removeFromCache(queueName);
                }
            }
        }
    }

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

        queueInfo.setUrl(queueUrl);
        queueInfo.setName(getNameFromAttributes(attr));
        queueInfo.setType(getTypeByName(attr));
        setStatusFromCache(queueInfo);

        return queueInfo;
    }

    private QueueInfo initQueueInfoFromAttributes(Map<String, String> attr) {
        QueueInfo queueInfo = mapper.convertValue(attr, QueueInfo.class);
        generatedIdEntityInitializer.initEntity(queueInfo);
        return queueInfo;
    }

    private String getNameFromAttributes(Map<String, String> attr) {
        String[] arnValues = attr.get("QueueArn").split(":");
        return arnValues[arnValues.length - 1];
    }

    private QueueType getTypeByName(Map<String, String> attr) {
        String name = getNameFromAttributes(attr);
        if (name.endsWith(".fifo")) {
            return QueueType.FIFO;
        }
        return QueueType.STANDARD;
    }

    private void setStatusFromCache(QueueInfo queueInfo) {
        if (queueStatusCache.isNotAvailable(queueInfo)) {
            queueInfo.setStatus(queueStatusCache.getPendingStatus(queueInfo));
        } else {
            queueInfo.setStatus(QueueStatus.RUNNING);
        }
    }

    public void deleteAsync(QueueInfo queueInfo) {
        amazonSQSAsyncClient.deleteQueueAsync(queueInfo.getUrl());
        queueStatusCache.setPendingStatus(queueInfo, QueueStatus.ON_DELETE);
    }

    public void createAsync(QueueInfo queueInfo) {
        Map<String, Object> attr = mapper.convertValue(queueInfo, Map.class);

        CreateQueueRequest createQueueRequest = new CreateQueueRequest()
                .withQueueName(queueInfo.getName());

        for (Map.Entry<String, Object> kv : attr.entrySet()) {
            createQueueRequest.addAttributesEntry(kv.getKey(), kv.getValue().toString());
        }

        if (queueInfo.getType() == QueueType.FIFO) {
            createQueueRequest.addAttributesEntry("FifoQueue", Boolean.TRUE.toString());
        }
        if (prefixExists()) {
            createQueueRequest.addTagsEntry(APPLICATION_TAG_KEY, getApplicationPrefix());
        }

        amazonSQSAsyncClient.createQueueAsync(createQueueRequest);
        queueStatusCache.setPendingStatus(queueInfo, QueueStatus.ON_CREATE);
    }

    private void putIfNotBlank(Map<String, String> map, String key, @Nullable String value) {
        if (StringUtils.isNotBlank(value)) {
            map.put(key, value);
        }
    }

    protected boolean prefixExists() {
        return getApplicationPrefix() != null;
    }

    protected String getApplicationPrefix() {
        return queueProperties.getQueueFamilyTag();
    }
}
