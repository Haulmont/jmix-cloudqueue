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

import com.amazonaws.services.sqs.model.CreateQueueRequest;
import io.jmix.sqs.entity.QueueType;


/**
 * {@link com.amazonaws.services.sqs.model.CreateQueueRequest} builder that can provide any of SQS queue attributes.
 * @see <a href="https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/API_SetQueueAttributes.html">Queue attributes</a>
 */
public class CreateQueueRequestBuilder {
    protected final CreateQueueRequest createQueueRequest;

    /**
     * @param queueName physical name of new queue in AWS
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
     * @param maximumMessageSize the limit of how many bytes a message can contain before Amazon SQS rejects it.
     * Valid values: An integer from 1,024 bytes (1 KiB) up to 262,144 bytes (256 KiB).
     * Default: 262,144 (256 KiB)
     */
    public CreateQueueRequestBuilder withMaximumMessageSize(Long maximumMessageSize) {
        putIfNotNull("MaximumMessageSize", maximumMessageSize);
        return this;
    }

    /**
     * @param messageRetentionPeriod the length of time, in seconds, for which Amazon SQS retains a message.
     * Valid values: An integer representing seconds, from 60 (1 minute) to 1,209,600 (14 days).
     * Default: 345,600 (4 days)
     */
    public CreateQueueRequestBuilder withMessageRetentionPeriod(Long messageRetentionPeriod) {
        putIfNotNull("MessageRetentionPeriod", messageRetentionPeriod);
        return this;
    }

    /**
     * @param visibilityTimeout the visibility timeout for the queue, in seconds.
     * Valid values: An integer from 0 to 43,200 (12 hours). Default: 30.
     * For more information about the visibility timeout,
     * see <a href="https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-visibility-timeout.html">Visibility Timeout</a>
     * in the Amazon Simple Queue Service Developer Guide
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
