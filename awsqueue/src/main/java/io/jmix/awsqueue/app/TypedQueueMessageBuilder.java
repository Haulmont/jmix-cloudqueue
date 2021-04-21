package io.jmix.awsqueue.app;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class TypedQueueMessageBuilder<T> {
    private static final String MESSAGE_GROUP_ID_KEY = "message-group-id";
    private static final String MESSAGE_DEDUPLICATION_ID_KEY = "message-deduplication-id";

    private final MessageBuilder<T> messageBuilder;

    private TypedQueueMessageBuilder(Message<T> providedMessage) {
        messageBuilder = MessageBuilder.fromMessage(providedMessage);
    }

    private TypedQueueMessageBuilder(T payload) {
        messageBuilder = MessageBuilder.withPayload(payload);
    }

    public MessageBuilder<T> standard() {
        return messageBuilder;
    }

    public MessageBuilder<T> fifo(String messageGroupId, String messageDeduplicationId) {
        messageBuilder.setHeader(MESSAGE_GROUP_ID_KEY, messageGroupId);
        messageBuilder.setHeader(MESSAGE_DEDUPLICATION_ID_KEY, messageDeduplicationId);
        return messageBuilder;
    }

    public static <T> TypedQueueMessageBuilder<T> fromMessage(Message<T> message) {
        return new TypedQueueMessageBuilder<T>(message);
    }

    public static <T> TypedQueueMessageBuilder<T> fromPayload(T payload) {
        return new TypedQueueMessageBuilder<T>(payload);
    }
}
