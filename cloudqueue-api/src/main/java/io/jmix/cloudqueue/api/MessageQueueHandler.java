package io.jmix.cloudqueue.api;


import com.amazonaws.services.sqs.model.ReceiveMessageResult;

@FunctionalInterface
public interface MessageQueueHandler {
    void handle(ReceiveMessageResult receiveMessageResult);
}
