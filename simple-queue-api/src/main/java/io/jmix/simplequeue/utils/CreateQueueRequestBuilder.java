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

package io.jmix.simplequeue.utils;

import com.amazonaws.services.sqs.model.CreateQueueRequest;
import io.jmix.simplequeue.entity.QueueType;


/**
 * The builder that can provide any of SQS queue attributes.
 */
public class CreateQueueRequestBuilder {
    protected final CreateQueueRequest createQueueRequest;

    /**
     * @param queueName physical name of new queue in provider
     */
    public CreateQueueRequestBuilder(String queueName) {
        createQueueRequest = new CreateQueueRequest().withQueueName(queueName);
    }

    /**
     * @param queueType type of queue standard/fifo
     */
    public CreateQueueRequestBuilder fromQueueType(QueueType queueType) {
        if (queueType == QueueType.FIFO) {
            createQueueRequest.addAttributesEntry("FifoQueue", Boolean.TRUE.toString());
        }
        return this;
    }

    /**
     * @param maximumMessageSize the limit of how many bytes a message can contain before provider rejects it.
     */
    public CreateQueueRequestBuilder withMaximumMessageSize(Long maximumMessageSize) {
        putIfNotNull("MaximumMessageSize", maximumMessageSize);
        return this;
    }

    /**
     * @param messageRetentionPeriod the length of time, in seconds, for which provider retains a message.
     */
    public CreateQueueRequestBuilder withMessageRetentionPeriod(Long messageRetentionPeriod) {
        putIfNotNull("MessageRetentionPeriod", messageRetentionPeriod);
        return this;
    }

    /**
     * @param visibilityTimeout the visibility timeout for the queue, in seconds.
     * Valid values: An integer from 0 to 43,200 (12 hours). Default: 30.
     */
    public CreateQueueRequestBuilder withVisibilityTimeout(Long visibilityTimeout) {
        putIfNotNull("VisibilityTimeout", visibilityTimeout);
        return this;
    }

    /**
     * @param deliveryTime the length of time, in seconds, for which the delivery of all messages in the queue is delayed.
     * Valid values: An integer from 0 to 900 (15 minutes). Default: 0
     */
    public CreateQueueRequestBuilder withDeliveryTime(Long deliveryTime) {
        putIfNotNull("DelaySeconds", deliveryTime);
        return this;
    }


    /**
     * @param receiveMessageWaitTime the length of time, in seconds,
     * for which a ReceiveMessage action waits for a message to arrive.
     * Valid values: An integer from 0 to 20 (seconds). Default: 0
     */
    public CreateQueueRequestBuilder withReceiveMessageWaitTime(Long receiveMessageWaitTime) {
        putIfNotNull("ReceiveMessageWaitTimeSeconds", receiveMessageWaitTime);
        return this;
    }

    /**
     * @return request to create specified in queue with name and attributes
     */
    public CreateQueueRequest build() {
        return createQueueRequest;
    }

    private void putIfNotNull(String attrName, Object obj) {
        if (obj != null) {
            createQueueRequest.addAttributesEntry(attrName, String.valueOf(obj));
        }
    }
}
