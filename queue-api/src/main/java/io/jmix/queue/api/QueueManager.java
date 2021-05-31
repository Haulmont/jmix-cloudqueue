package io.jmix.queue.api;

import io.jmix.queue.entity.QueueInfo;
import io.jmix.queue.models.Queue;

import java.util.Collection;

import javax.annotation.Nullable;

/**
 * Queues manager for loading information about existing queues, creating and deleting queues.
 */
public interface QueueManager {

    /**
     * @return all existing for application queues
     */
    Collection<QueueInfo> loadAll();

    /**
     * Gets information about queue in provider from specified url.
     *
     * @param queueUrl url to physical queue in provider
     * @return {@link io.jmix.queue.entity.QueueInfo} with status attributes,
     * returns null if queue does not exist or if it in the process of deleting
     */
    @Nullable
    QueueInfo queueInfoFromUrl(String queueUrl);

    /**
     * Create queue on provider with request specified properties.
     *
     * @param queue object with information about new queue,
     *              must contains queueName parameter, after this method has to call method from provider API
     */
    void createQueue(Queue queue);

    /**
     * Delete queue from Queue provider.
     *
     * @param queueUrl url to physical queue in cloud
     */
    void deleteQueue(String queueUrl);

    /**
     * Allows subscribe on provider Queue by its name and handle message by handler that according MessageQueueHandler.
     * Also allows create several different handlers for same queue.
     *
     * @param queueName     for subscribing
     * @param lambdaHandler handler that will handle message from queue
     */
    void subscribe(String queueName, MessageQueueHandler lambdaHandler);

    /**
     *  Delivers a message to the specified queue.
     *  The method uses for standard type of queue.
     *
     * @param queueName for sending message
     * @param payload   body of message
     */
    void sendMessageToStandardQueue(String queueName, String payload);

    /**
     *  Delivers a message to the specified queue.
     *  The method uses for FIFO type of queue.
     *
     * @param queueName              for sending message
     * @param payload                body of message
     * @param messageGroupId         еhe tag that specifies that a message belongs to a specific message group. Messages that belong to the same message group are processed in a FIFO manner (however, messages in different message groups might be processed out of order). To interleave multiple ordered streams within a single queue, use MessageGroupId values (for example, session data for multiple users). In this scenario, multiple consumers can process the queue, but the session data of each user is processed in a FIFO fashion.
     * @param messageDeduplicationId еhe token used for deduplication of sent messages. If a message with a particular MessageDeduplicationId is sent successfully, any messages sent with the same MessageDeduplicationId are accepted successfully but aren't delivered during the 5-minute deduplication interval.
     */
    void sendMessageToFIFOQueue(String queueName, String payload, String messageGroupId, String messageDeduplicationId);
}
