package io.jmix.awsqueue.app;

import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.jmix.awsqueue.entity.QueueInfo;
import io.jmix.awsqueue.entity.QueueType;
import io.micrometer.core.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CreateQueueRequestBuilder {
    protected static final String APPLICATION_TAG_KEY = "ApplicationTag";

    protected ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
    protected CreateQueueRequest createQueueRequest;
    private final QueueInfo liteCopyInfo;


    public CreateQueueRequestBuilder(QueueInfo info) {
        liteCopyInfo = copyNecessaryFields(info);
        createQueueRequest = new CreateQueueRequest().withQueueName(info.getName());
    }

    public CreateQueueRequestBuilder withInnerQueueType(){
        if (liteCopyInfo.getType() == QueueType.FIFO) {
            createQueueRequest.addAttributesEntry("FifoQueue", Boolean.TRUE.toString());
        }
        return this;
    }

    public CreateQueueRequestBuilder withPrefixIfNotNull(@Nullable String prefix){
        if (prefix != null) {
            createQueueRequest.addTagsEntry(APPLICATION_TAG_KEY, prefix);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public CreateQueueRequestBuilder withInnerQueueAttributes(){
        Map<String, Object> attr = mapper.convertValue(liteCopyInfo, Map.class);
        for (Map.Entry<String, Object> kv : attr.entrySet()) {
            createQueueRequest.addAttributesEntry(kv.getKey(), kv.getValue().toString());
        }
        return this;
    }

    protected QueueInfo copyNecessaryFields(QueueInfo info){
        QueueInfo liteCopy = new QueueInfo();
        liteCopy.setDeliveryTime(info.getDeliveryTime());
        liteCopy.setMessageDelayed(info.getMessageDelayed());
        liteCopy.setMaximumMessageSize(info.getMaximumMessageSize());
        liteCopy.setMessageRetentionPeriod(info.getMessageRetentionPeriod());
        liteCopy.setReceiveMessageWaitTime(info.getReceiveMessageWaitTime());
        return liteCopy;
    }

    public CreateQueueRequest build() {
        return createQueueRequest;
    }
}
