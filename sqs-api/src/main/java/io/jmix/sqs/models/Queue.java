package io.jmix.sqs.models;

import io.jmix.sqs.entity.QueueType;


/**
 * The сlass that represents an abstraction over the provider queue
 */
public class Queue {
    private final String name;
    private final QueueType type;
    private final Long maximumMessageSize;
    private final Long messageRetentionPeriod;
    private final Long visibilityTimeout;
    private final Long deliveryTime;
    private final Long receiveMessageWaitTime;

    private Queue(String name, QueueType type, Long maximumMessageSize,
                  Long messageRetentionPeriod, Long visibilityTimeout, Long deliveryTime,
                  Long receiveMessageWaitTime) {
        this.name = name;
        this.type = type;
        this.maximumMessageSize = maximumMessageSize;
        this.messageRetentionPeriod = messageRetentionPeriod;
        this.visibilityTimeout = visibilityTimeout;
        this.deliveryTime = deliveryTime;
        this.receiveMessageWaitTime = receiveMessageWaitTime;
    }

    /**
     * Physical name of new queue in provider
     */
    public String getName() {
        return name;
    }

    /**
     * Type of queue standard or fifo
     */
    public QueueType getType() {
        return type;
    }

    /**
     * The limit of how many bytes a message can contain before provider queue rejects it.
     */
    public Long getMaximumMessageSize() {
        return maximumMessageSize;
    }

    /**
     * The length of time, in seconds, for which queue retains a message.
     */
    public Long getMessageRetentionPeriod() {
        return messageRetentionPeriod;
    }

    /**
     * The visibility timeout for the queue, in seconds.
     */
    public Long getVisibilityTimeout() {
        return visibilityTimeout;
    }

    /**
     * The length of time, in seconds, for which the delivery of all messages in the queue is delayed.
     */
    public Long getDeliveryTime() {
        return deliveryTime;
    }

    /**
     * The length of time, in seconds,for which a action waits for a message to arrive.
     */
    public Long getReceiveMessageWaitTime() {
        return receiveMessageWaitTime;
    }

    public static class Builder {

        private final String name;
        private QueueType type;
        private Long maximumMessageSize;
        private Long messageRetentionPeriod;
        private Long visibilityTimeout;
        private Long deliveryTime;
        private Long receiveMessageWaitTime;

        public Builder(String name) {
            this.name = name;
        }

        public Builder withType(QueueType type) {
            this.type = type;
            return this;
        }

        public Builder withMaximumMessageSize(Long maximumMessageSize) {
            this.maximumMessageSize = maximumMessageSize;
            return this;
        }

        public Builder withMessageRetentionPeriod(Long messageRetentionPeriod) {
            this.messageRetentionPeriod = messageRetentionPeriod;
            return this;
        }

        public Builder withVisibilityTimeout(Long visibilityTimeout) {
            this.visibilityTimeout = visibilityTimeout;
            return this;
        }

        public Builder withDeliveryTime(Long deliveryTime) {
            this.deliveryTime = deliveryTime;
            return this;
        }

        public Builder withReceiveMessageWaitTime(Long receiveMessageWaitTime) {
            this.receiveMessageWaitTime = receiveMessageWaitTime;
            return this;
        }

        public Queue build() {
            return new Queue(name, type, maximumMessageSize,
                    messageRetentionPeriod, visibilityTimeout, deliveryTime, receiveMessageWaitTime);
        }

    }
}
