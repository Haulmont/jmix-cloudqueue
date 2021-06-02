package io.jmix.sqs.utils;

import com.amazonaws.services.sqs.model.CreateQueueRequest;
import io.jmix.sqs.models.Message;
import io.jmix.sqs.models.Queue;
import io.jmix.sqs.models.ReceiveMessageResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTOMapper {

    private static final String METADATA_REQUEST_ID = "RequestId";

    public static ReceiveMessageResult getModelFromAWSReceiveMessageResult(com.amazonaws.services.sqs.model.ReceiveMessageResult from) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put(METADATA_REQUEST_ID, from.getSdkResponseMetadata().getRequestId());

        ReceiveMessageResult result = new ReceiveMessageResult(metadata);

        List<Message> messages = new ArrayList<>();
        if (!from.getMessages().isEmpty())
            from.getMessages().forEach(message -> messages.add(getModelFromAWSMessage(message)));
        result.setMessages(messages);

        return result;
    }

    public static Message getModelFromAWSMessage(com.amazonaws.services.sqs.model.Message from) {
        Message message = new Message();
        message.setMessageId(from.getMessageId());
        message.setReceiptHandle(from.getReceiptHandle());
        message.setMD5OfBody(from.getMD5OfBody());
        message.setBody(from.getBody());
        message.setMD5OfMessageAttributes(from.getMD5OfMessageAttributes());

        if (!from.getAttributes().isEmpty()) {
            Map<String, String> attributes = new HashMap<>();
            from.getAttributes().forEach(attributes::put);
            message.setAttributes(attributes);
        }

        return message;
    }

    public static CreateQueueRequest getCreateQueueRequestFromModel(Queue from) {
        return new CreateQueueRequestBuilder(from.getName())
                .fromQueueType(from.getType())
                .withDeliveryTime(from.getDeliveryTime())
                .withMaximumMessageSize(from.getMaximumMessageSize())
                .withMessageRetentionPeriod(from.getMessageRetentionPeriod())
                .withVisibilityTimeout(from.getVisibilityTimeout())
                .withReceiveMessageWaitTime(from.getReceiveMessageWaitTime())
                .build();
    }
}
