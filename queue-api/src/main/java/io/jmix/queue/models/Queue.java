package io.jmix.queue.models;

import io.jmix.queue.entity.QueueType;


/**
 * The сlass that represents an abstraction over the provider queue
 */
public class Queue {
    private final String queueName;
    private final QueueType queueType;
    private final Long maximumMessageSize;
    private final Long messageRetentionPeriod;
    private final Long visibilityTimeout;
    private final Long deliveryTime;
    private final Long receiveMessageWaitTime;

    private Queue(String queueName, QueueType queueType, Long maximumMessageSize,
                  Long messageRetentionPeriod, Long visibilityTimeout, Long deliveryTime,
                  Long receiveMessageWaitTime) {
        this.queueName = queueName;
        this.queueType = queueType;
        this.maximumMessageSize = maximumMessageSize;
        this.messageRetentionPeriod = messageRetentionPeriod;
        this.visibilityTimeout = visibilityTimeout;
        this.deliveryTime = deliveryTime;
        this.receiveMessageWaitTime = receiveMessageWaitTime;
    }

    /**
     * Physical name of new queue in provider
     */
    public String getQueueName() {
        return queueName;
    }

    /**
     * Type of queue standard or fifo
     */
    public QueueType getQueueType() {
        return queueType;
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

        private final String queueName;
        private QueueType queueType;
        private Long maximumMessageSize;
        private Long messageRetentionPeriod;
        private Long visibilityTimeout;
        private Long deliveryTime;
        private Long receiveMessageWaitTime;

        public Builder(String queueName) {
            this.queueName = queueName;
        }

        public Builder withQueueType(QueueType queueType) {
            this.queueType = queueType;
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
            return new Queue(queueName, queueType, maximumMessageSize,
                    messageRetentionPeriod, visibilityTimeout, deliveryTime, receiveMessageWaitTime);
        }

    }
}
