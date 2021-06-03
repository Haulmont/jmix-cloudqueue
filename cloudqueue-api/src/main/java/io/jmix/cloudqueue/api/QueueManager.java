package io.jmix.cloudqueue.api;

import com.amazonaws.services.sqs.model.CreateQueueRequest;
import io.jmix.cloudqueue.entity.QueueInfo;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;

import java.util.Collection;

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
     * @return {@link QueueInfo} with status attributes,
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
    void createQueue(CreateQueueRequest queue);

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
     *
     * @param queueName where sending message
     * @param message   message
     */
    void sendMessage(String queueName, Message<?> message);
}
