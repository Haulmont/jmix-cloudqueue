package io.jmix.queue.api;

import io.jmix.queue.models.ReceiveMessageResult;

@FunctionalInterface
public interface MessageQueueHandler {
    void handle(ReceiveMessageResult receiveMessageResult);
}
