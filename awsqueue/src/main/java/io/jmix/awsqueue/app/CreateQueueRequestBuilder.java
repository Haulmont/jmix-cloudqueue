package io.jmix.awsqueue.app;

import com.amazonaws.services.sqs.model.CreateQueueRequest;
import io.jmix.awsqueue.entity.QueueType;
import io.micrometer.core.lang.Nullable;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CreateQueueRequestBuilder {

    protected final CreateQueueRequest createQueueRequest;
    private final Map<String, String> attributes = new HashMap<>();
    public CreateQueueRequestBuilder(String queueName) {
        createQueueRequest = new CreateQueueRequest().withQueueName(queueName);
    }

    public CreateQueueRequestBuilder fromQueueType(QueueType queueType) {
        if (queueType == QueueType.FIFO) {
            attributes.put("FifoQueue", Boolean.TRUE.toString());
        }
        return this;
    }

    public CreateQueueRequestBuilder withMaximumMessageSize(long maximumMessageSize) {
        attributes.put("MaximumMessageSize", String.valueOf(maximumMessageSize));
        return this;
    }

    public CreateQueueRequestBuilder withMessageRetentionPeriod(long messageRetentionPeriod) {
        attributes.put("MessageRetentionPeriod", String.valueOf(messageRetentionPeriod));
        return this;
    }

    public CreateQueueRequestBuilder withVisibilityTimeout(long visibilityTimeout) {
        attributes.put("VisibilityTimeout", String.valueOf(visibilityTimeout));
        return this;
    }

    public CreateQueueRequestBuilder withDeliveryTime(long deliveryTime) {
        attributes.put("DeliveryTime", String.valueOf(deliveryTime));
        return this;
    }

    public CreateQueueRequestBuilder withReceiveMessageWaitTime(long receiveMessageWaitTime) {
        attributes.put("ReceiveMessageWaitTime", String.valueOf(receiveMessageWaitTime));
        return this;
    }

    public CreateQueueRequest build() {
        Map<String, String> notNullAttributes = excludeNullFields(attributes);
        return createQueueRequest.withAttributes(notNullAttributes);
    }

    protected Map<String, String> excludeNullFields(Map<String, String> attributes) {
        Map<String, String> resultMap = new HashMap<>(attributes);
        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            if (StringUtils.isBlank(entry.getValue())) {
                attributes.remove(entry.getKey());
            }
        }
        return resultMap;
    }
}
