package io.jmix.awsqueue.app;

import io.jmix.awsqueue.entity.QueueType;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;

import java.util.UUID;

public class TypedQueueMessageBuilder<T> {
    private static final String MESSAGE_GROUP_ID_KEY = "message-group-id";
    private static final String MESSAGE_DEDUPLICATION_ID_KEY = "message-deduplication-id";

    private String messageGroupId;
    private String messageDeduplicationId;
    private final MessageBuilder<T> messageBuilder;

    public TypedQueueMessageBuilder(Message<T> providedMessage) {
        messageBuilder = MessageBuilder.fromMessage(providedMessage);
    }

    public TypedQueueMessageBuilder(T payload) {
        messageBuilder = MessageBuilder.withPayload(payload);
    }

    public TypedQueueMessageBuilder<T> withMessageGroupId(String messageGroupId) {
        this.messageGroupId = messageGroupId;
        return this;
    }

    public TypedQueueMessageBuilder<T> withMessageDeduplicationId(String messageDeduplicationId) {
        this.messageDeduplicationId = messageDeduplicationId;
        return this;
    }

    public static <T> TypedQueueMessageBuilder<T> fromMessage(Message<T> message) {
        return new TypedQueueMessageBuilder<T>(message);
    }

    public static <T> TypedQueueMessageBuilder<T> fromPayload(T payload) {
        return new TypedQueueMessageBuilder<T>(payload);
    }

    public MessageBuilder<T> toMessageBuilder(QueueType queueType) {
        if (queueType.equals(QueueType.FIFO)) {
            Assert.notNull(messageGroupId, "Message group id must be specified and not null for FIFO");
            Assert.notNull(messageDeduplicationId, "Message deduplication id must be specified and not null for FIFO");
            messageBuilder.setHeader(MESSAGE_GROUP_ID_KEY, messageGroupId);
            messageBuilder.setHeader(MESSAGE_DEDUPLICATION_ID_KEY, UUID.randomUUID().toString());
        }
        return messageBuilder;
    }
}
