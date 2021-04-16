package io.jmix.awsqueue.app;

import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.jmix.awsqueue.entity.QueueAttributes;
import io.jmix.awsqueue.entity.QueueType;
import io.micrometer.core.lang.Nullable;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class CreateQueueRequestBuilder {
    protected static final String APPLICATION_TAG_KEY = "ApplicationTag";

    protected ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
    protected CreateQueueRequest createQueueRequest;


    public CreateQueueRequestBuilder(String queueName) {
        createQueueRequest = new CreateQueueRequest().withQueueName(queueName);
    }

    public CreateQueueRequestBuilder fromQueueType(@Nullable QueueType queueType) {
        if (queueType == QueueType.FIFO) {
            createQueueRequest.addAttributesEntry("FifoQueue", Boolean.TRUE.toString());
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public CreateQueueRequestBuilder withQueueAttributes(QueueAttributes attributes) {
        Map<String, Object> attr = mapper.convertValue(attributes, Map.class);
        for (Map.Entry<String, Object> kv : attr.entrySet()) {
            createQueueRequest.addAttributesEntry(kv.getKey(), kv.getValue().toString());
        }
        return this;
    }

    public CreateQueueRequest build() {
        return createQueueRequest;
    }
}
