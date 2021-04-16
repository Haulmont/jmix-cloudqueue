package io.jmix.awsqueue.app;

import com.amazonaws.services.sqs.model.CreateQueueRequest;
import io.jmix.awsqueue.entity.QueueType;

public class CreateQueueRequestBuilder {
    protected final CreateQueueRequest createQueueRequest;

    public CreateQueueRequestBuilder(String queueName) {
        createQueueRequest = new CreateQueueRequest().withQueueName(queueName);
    }

    public CreateQueueRequestBuilder fromQueueType(QueueType queueType) {
        if (queueType == QueueType.FIFO) {
            createQueueRequest.addAttributesEntry("FifoQueue", Boolean.TRUE.toString());
        }
        return this;
    }

    public CreateQueueRequestBuilder withMaximumMessageSize(Long maximumMessageSize) {
        putIfNotNull("MaximumMessageSize", maximumMessageSize);
        return this;
    }

    public CreateQueueRequestBuilder withMessageRetentionPeriod(Long messageRetentionPeriod) {
        putIfNotNull("MessageRetentionPeriod", messageRetentionPeriod);
        return this;
    }

    public CreateQueueRequestBuilder withVisibilityTimeout(Long visibilityTimeout) {
        putIfNotNull("VisibilityTimeout", visibilityTimeout);
        return this;
    }

    public CreateQueueRequestBuilder withDeliveryTime(Long deliveryTime) {
        putIfNotNull("DelaySeconds", deliveryTime);
        return this;
    }

    public CreateQueueRequestBuilder withReceiveMessageWaitTime(Long receiveMessageWaitTime) {
        putIfNotNull("ReceiveMessageWaitTimeSeconds", receiveMessageWaitTime);
        return this;
    }

    public CreateQueueRequest build() {
        return createQueueRequest;
    }

    private void putIfNotNull(String attrName, Object obj) {
        if (obj != null) {
            createQueueRequest.addAttributesEntry(attrName, String.valueOf(obj));
        }
    }
}
