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

import org.springframework.messaging.Message;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @param <T> - the message payload type
 */
public class QueueMessageBuilder<T> {
    private static final String MESSAGE_GROUP_ID_KEY = "message-group-id";
    private static final String MESSAGE_DEDUPLICATION_ID_KEY = "message-deduplication-id";

    private final MessageBuilder<T> messageBuilder;

    private QueueMessageBuilder(Message<T> providedMessage) {
        messageBuilder = MessageBuilder.fromMessage(providedMessage);
    }

    private QueueMessageBuilder(T payload) {
        messageBuilder = MessageBuilder.withPayload(payload);
    }

    /**
     * @return Spring message builder for payload that was specified
     */
    public MessageBuilder<T> standard() {
        return messageBuilder;
    }

    /**
     * Special message builder for FIFO queue with required attributes
     *
     * @param messageGroupId         The tag that specifies that a message belongs to a specific message group.
     *                               Messages that belong to the same message group are always processed one by one,
     *                               in a strict order relative to the message group (however, messages that belong
     *                               to different message groups might be processed out of order).
     * @param messageDeduplicationId The token used for deduplication of sent messages.
     *                               If a message with a particular message deduplication ID is sent successfully,
     *                               any messages sent with the same message deduplication ID are accepted successfully
     *                               but aren't delivered during the 5-minute deduplication interval.
     * @return Spring message builder for payload that was specified with arguments for FIFO queue
     * @see <a href="https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/FIFO-queues.html">
     * AWS fifo queue
     * </a>
     */
    public MessageBuilder<T> fifo(String messageGroupId, String messageDeduplicationId) {
        messageBuilder.setHeader(MESSAGE_GROUP_ID_KEY, messageGroupId);
        messageBuilder.setHeader(MESSAGE_DEDUPLICATION_ID_KEY, messageDeduplicationId);
        return messageBuilder;
    }

    /**
     * Create a builder for a new {@link Message} instance pre-populated with all of the
     * headers copied from the provided message. The payload of the provided Message will
     * also be used as the payload for the new message.
     * <p>If the provided message is an {@link ErrorMessage}, the
     * {@link ErrorMessage#getOriginalMessage() originalMessage} it contains, will be
     * passed on to new instance.
     *
     * @param message the Message from which the payload and all headers will be copied
     */
    public static <T> QueueMessageBuilder<T> fromMessage(Message<T> message) {
        return new QueueMessageBuilder<T>(message);
    }

    /**
     * Create a new builder for a message with the given payload.
     *
     * @param payload the payload
     */
    public static <T> QueueMessageBuilder<T> fromPayload(T payload) {
        return new QueueMessageBuilder<T>(payload);
    }
}
