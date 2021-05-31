package io.jmix.sqs.api;

import io.jmix.sqs.models.ReceiveMessageResult;

@FunctionalInterface
public interface MessageQueueHandler {
    void handle(ReceiveMessageResult receiveMessageResult);
}
