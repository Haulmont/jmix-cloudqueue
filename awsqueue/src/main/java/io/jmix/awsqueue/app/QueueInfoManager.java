package io.jmix.awsqueue.app;

import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.*;
import io.jmix.core.DataManager;
import io.jmix.awsqueue.QueueProperties;
import io.jmix.awsqueue.entity.QueueInfo;
import io.jmix.awsqueue.entity.QueueStatus;
import io.jmix.awsqueue.entity.QueueType;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private DataManager dataManager;

    @Autowired
    private QueueStatusCache queueStatusCache;


    @Autowired
    private QueueProperties queueProperties;

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

    public List<QueueInfo> loadAll() {
        List<QueueInfo> resultQueues = loadFromApi();
        syncCacheWithApi(resultQueues);
        resultQueues.addAll(queueStatusCache.getPendingQueues());
        return resultQueues;
    }

    protected void syncCacheWithApi(List<QueueInfo> apiQueues){
        for (String queueName : queueStatusCache.getPendingNames()) {
            if(apiQueues.stream()
                    .anyMatch(t -> t.getName().equals(queueName))){
                QueueInfo queueInfo = queueStatusCache.getPendingQueue(queueName);
                if(queueInfo.getStatus().equals(QueueStatus.ON_CREATE)){
                    queueStatusCache.removeFromCache(queueName);
                }
            }
            else if(apiQueues.stream()
                    .noneMatch(t -> t.getName().equals(queueName))){
                QueueInfo queueInfo = queueStatusCache.getPendingQueue(queueName);
                if(queueInfo.getStatus().equals(QueueStatus.ON_DELETE)){
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
        } catch (QueueDoesNotExistException exception){
            return null;
        }

        QueueInfo queueInfo = dataManager.create(QueueInfo.class);
        Map<String, String> attr = attributesResult.getAttributes();

        String[] arn = attr.get("QueueArn").split(":");
        String name = arn[arn.length - 1];
        queueInfo.setUrl(queueUrl);
        queueInfo.setName(name);
        queueInfo.setType(getTypeByName(name));
        setStatusFromCache(queueInfo);
        queueInfo.setCreated(getLocalTime(attr.get("CreatedTimestamp")));
        queueInfo.setLastUpdate(getLocalTime(attr.get("LastModifiedTimestamp")));
        queueInfo.setMaximumMessageSize(getSizeFromStr(attr.get("MaximumMessageSize")));
        queueInfo.setMessageRetentionPeriod(getSizeFromStr(attr.get("MessageRetentionPeriod")));
        queueInfo.setVisibilityTimeout(getSizeFromStr(attr.get("VisibilityTimeout")));
        queueInfo.setMessagesAvailable(getSizeFromStr(attr.get("ApproximateNumberOfMessages")));
        queueInfo.setDeliveryTime(getSizeFromStr(attr.get("DelaySeconds")));
        queueInfo.setMessagesInFlight(getSizeFromStr(attr.get("ApproximateNumberOfMessagesNotVisible")));
        queueInfo.setReceiveMessageWaitTime(getSizeFromStr(attr.get("ReceiveMessageWaitTimeSeconds")));
        queueInfo.setMessageDelayed(getSizeFromStr(attr.get("ApproximateNumberOfMessagesDelayed")));
        return queueInfo;
    }

    protected boolean isInternalExistingQueue(String queueUrl){
        if(prefixExists()){
            try {
                ListQueueTagsResult result = amazonSQSAsyncClient.listQueueTags(queueUrl);
                return result.getTags().containsKey(APPLICATION_TAG_KEY) &&
                        result.getTags().get(APPLICATION_TAG_KEY).equals(getApplicationPrefix());
            } catch (QueueDoesNotExistException exception){
                return false;
            }
        }
        return true;
    }

    protected void setStatusFromCache(QueueInfo queueInfo) {
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

    public void createAsync(QueueInfo queueInfo, Map<String, String> attr) {
        CreateQueueRequest createQueueRequest = new CreateQueueRequest()
                .withQueueName(queueInfo.getName())
                .withAttributes(attr);

        if(prefixExists()){
            Map<String, String> tags = new HashMap<>();
            tags.put(APPLICATION_TAG_KEY, getApplicationPrefix());
            createQueueRequest.withTags(tags);
        }

        amazonSQSAsyncClient.createQueueAsync(createQueueRequest);
        queueStatusCache.setPendingStatus(queueInfo, QueueStatus.ON_CREATE);
    }

    protected boolean prefixExists(){
        return getApplicationPrefix() != null;
    }

    protected String getApplicationPrefix(){
        return queueProperties.getQueueFamilyTag();
    }

    protected String getLocalTime(String timestampStr) {
        //todo use Client's local time
        long seconds = Long.valueOf(timestampStr).longValue();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss z");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(String.valueOf(seconds * 1000)));

        return simpleDateFormat.format(calendar.getTime());
    }

    protected QueueType getTypeByName(String name) {
        if (name.endsWith(".fifo")) {
            return QueueType.FIFO;
        }
        return QueueType.STANDARD;
    }

    protected Long getSizeFromStr(String value) {
        if (StringUtils.isBlank(value)) {
            return 0L;
        }
        return (Long.valueOf(value));
    }
}
