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
import io.jmix.awsqueue.entity.QueueInfo;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * AWS Queues manager for loading information about existing queues, creating and deleting queues.
 */
public interface QueueManager {
    /**
     * @return all existing for application queues
     */
    Collection<QueueInfo> loadAll();

    /**
     * Gets information about queue in AWS from specified url.
     * @param queueUrl url to physical queue in AWS
     * @return {@link io.jmix.awsqueue.entity.QueueInfo} with status attributes,
     * returns null if queue does not exist or if it in the process of deleting
     * @see <a href="https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/API_SetQueueAttributes.html">Queue attributes</a>
     */
    @Nullable
    QueueInfo queueInfoFromUrl(String queueUrl);

    /**
     * Create queue on AWS with request specified properties.
     * @param createQueueRequest request with information about new queue,
     * must contains queueName parameter
     * @apiNote use {@link CreateQueueRequestBuilder} to create the request properly
     * @see <a href="https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/sqs/model/CreateQueueRequest.html">CreateQueueRequest</a>
     */
    void createQueue(CreateQueueRequest createQueueRequest);

    /**
     * Delete queue from AWS.
     * @param queueUrl url to physical queue in cloud
     */
    void deleteQueue(String queueUrl);
}
