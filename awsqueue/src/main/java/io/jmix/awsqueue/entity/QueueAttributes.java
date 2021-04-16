package io.jmix.awsqueue.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.UUID;

@JmixEntity(annotatedPropertiesOnly = true)
public class QueueAttributes {

    @JsonIgnore
    @JmixGeneratedValue
    @JmixProperty(mandatory = true)
    @JmixId
    private UUID id;

    @JsonProperty("MaximumMessageSize")
    @Max(message = "{msg://QueueInfo.maximumMessageSize.validation}", value = 262144)
    @Min(message = "{msg://QueueInfo.maximumMessageSize.validation}", value = 1024)
    @JmixProperty
    private Long maximumMessageSize;

    @JsonProperty("MessageRetentionPeriod")
    @Max(message = "{msg://QueueInfo.messageRetentionPeriod.validation}", value = 1209600)
    @Min(message = "{msg://QueueInfo.messageRetentionPeriod.validation}", value = 60)
    @JmixProperty
    private Long messageRetentionPeriod;

    @JsonProperty("VisibilityTimeout")
    @Max(message = "{msg://QueueInfo.visibilityTimeout.validation}", value = 43200)
    @Min(message = "{msg://QueueInfo.visibilityTimeout.validation}", value = 0)
    @JmixProperty
    private Long visibilityTimeout;

    @JsonProperty("DelaySeconds")
    @Max(message = "{msg://QueueInfo.deliveryTime.validation}", value = 900)
    @Min(message = "{msg://QueueInfo.deliveryTime.validation}", value = 0)
    @JmixProperty
    private Long deliveryTime;

    @JsonProperty("ReceiveMessageWaitTimeSeconds")
    @Max(message = "{msg://QueueInfo.receiveMessageWaitTime.validation}", value = 20)
    @Min(message = "{msg://QueueInfo.receiveMessageWaitTime.validation}", value = 0)
    @JmixProperty
    private Long receiveMessageWaitTime;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getMaximumMessageSize() {
        return maximumMessageSize;
    }

    public void setMaximumMessageSize(Long maximumMessageSize) {
        this.maximumMessageSize = maximumMessageSize;
    }

    public Long getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Long getMessageRetentionPeriod() {
        return messageRetentionPeriod;
    }

    public void setMessageRetentionPeriod(Long messageRetentionPeriod) {
        this.messageRetentionPeriod = messageRetentionPeriod;
    }

    public Long getVisibilityTimeout() {
        return visibilityTimeout;
    }

    public void setVisibilityTimeout(Long visibilityTimeout) {
        this.visibilityTimeout = visibilityTimeout;
    }
    public Long getReceiveMessageWaitTime() {
        return receiveMessageWaitTime;
    }

    public void setReceiveMessageWaitTime(Long receiveMessageWaitTime) {
        this.receiveMessageWaitTime = receiveMessageWaitTime;
    }
}