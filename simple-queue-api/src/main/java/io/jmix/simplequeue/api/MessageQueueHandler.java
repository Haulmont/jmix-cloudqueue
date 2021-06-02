package io.jmix.simplequeue.api;


import com.amazonaws.services.sqs.model.ReceiveMessageResult;

@FunctionalInterface
public interface MessageQueueHandler {
    void handle(ReceiveMessageResult receiveMessageResult);
}
