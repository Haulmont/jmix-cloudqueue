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
import io.jmix.awsqueue.app.CreateQueueRequestBuilder;
import io.jmix.awsqueue.entity.QueueAttributes;
import io.jmix.awsqueue.entity.QueueInfo;
import io.jmix.awsqueue.entity.QueueStatus;
import io.jmix.awsqueue.entity.QueueType;
import io.jmix.core.DataManager;
import io.jmix.core.EntityInitializer;
import io.jmix.core.impl.GeneratedIdEntityInitializer;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    protected ObjectMapper mapper;
    @Autowired
    private DataManager dataManager;

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
                .collect(Collectors.toMap(QueueInfo::getName, queueInfo -> queueInfo));

    }

    public Collection<QueueInfo> loadAll() {
        Map<String, QueueInfo> resultQueues = loadFromApi();
        syncCacheWithApi(resultQueues);
        Collection<QueueInfo> queueInfoList = new ArrayList<>();
        queueInfoList.addAll(queueStatusCache.getPendingQueues());
        queueInfoList.addAll(resultQueues.values());
        return queueInfoList;
    }

    protected void syncCacheWithApi(Map<String, QueueInfo> apiQueues) {
        for (String queueName : queueStatusCache.getPendingNames()) {
            QueueInfo queueInfo = queueStatusCache.getPendingQueue(queueName);
            if (apiQueues.containsKey(queueName)) {
                if (queueInfo.getStatus().equals(QueueStatus.ON_CREATE)) {
                    queueStatusCache.removeFromCache(queueName);
                }
            } else {
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
        String queueName = getNameFromAttributes(attr);

        queueInfo.setUrl(queueUrl);
        queueInfo.setName(queueName);
        queueInfo.setType(getTypeByName(queueName));
        setStatusFromCache(queueInfo);

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

    private void setStatusFromCache(QueueInfo queueInfo) {
        if (queueStatusCache.isNotAvailable(queueInfo)) {
            queueInfo.setStatus(queueStatusCache.getPendingStatus(queueInfo));
        } else {
            queueInfo.setStatus(QueueStatus.RUNNING);
        }
    }

    public void deleteQueue(QueueInfo queueInfo) {
        amazonSQSAsyncClient.deleteQueueAsync(queueInfo.getUrl());
        queueStatusCache.setPendingStatus(queueInfo, QueueStatus.ON_DELETE);
    }

    public void createQueue(CreateQueueRequest createQueueRequest) {
        String prefix = queueProperties.getQueuePrefix();

        if (StringUtils.isNotBlank(prefix) && !createQueueRequest.getQueueName().startsWith(prefix)) {
            String prefixedName = queueProperties.getQueuePrefix() + "_" + createQueueRequest.getQueueName();
            createQueueRequest.setQueueName(prefixedName);
        }

        amazonSQSAsyncClient.createQueueAsync(createQueueRequest);
        queueStatusCache.setPendingStatus(queueInfoFromRequest(createQueueRequest), QueueStatus.ON_CREATE);
    }

    protected QueueInfo queueInfoFromRequest(CreateQueueRequest createQueueRequest){
        Map<String, String> mapAttrs = createQueueRequest.getAttributes();
        QueueAttributes queueAttributes = mapper.convertValue(mapAttrs, QueueAttributes.class);
        generatedIdEntityInitializer.initEntity(queueAttributes);

        QueueInfo queueInfo = dataManager.create(QueueInfo.class);
        queueInfo.setName(createQueueRequest.getQueueName());
        queueInfo.setType(getTypeByName(createQueueRequest.getQueueName()));
        queueInfo.setQueueAttributes(queueAttributes);

        return queueInfo;
    }
}
