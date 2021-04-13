package io.jmix.queue.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.UUID;

@JmixEntity
public class QueueInfo {
    @JmixGeneratedValue
    @JmixProperty(mandatory = true)
    @JmixId
    private UUID id;

    @InstanceName
    @JmixProperty(mandatory = true)
    private String name;

    @JmixProperty(mandatory = true)
    private String url;

    @JmixProperty(mandatory = true)
    private Integer type;

    private Integer status;

    private String created = "-";

    private String lastUpdate = "-";

    private Long messagesAvailable = 0L;

    private Long messageDelayed = 0L;

    private Long messagesInFlight = 0L;

    @Max(message = "Should be between 0 seconds and 12 hours.", value = 43200)
    @Min(message = "Should be between 0 seconds and 12 hours.", value = 0)
    private Long visibilityTimeout;

    @Max(message = "Should be between 1 minute and 14 days.", value = 1209600)
    @Min(message = "Should be between 1 minute and 14 days.", value = 60)
    private Long messageRetentionPeriod;

    @Max(message = "Should be between 0 seconds and 15 minutes.", value = 900)
    @Min(message = "Should be between 0 seconds and 15 minutes.", value = 0)
    private Long deliveryTime;

    @Max(message = "Should be between 1 KB and 256 KB.", value = 256)
    @Min(message = "Should be between 1 KB and 256 KB.", value = 1)
    private Long maximumMessageSize;

    @Max(message = "Should be between 0 and 20 seconds.", value = 20)
    @Min(message = "Should be between 0 and 20 seconds.", value = 0)
    private Long receiveMessageWaitTime;

    public Long getMessageDelayed() {
        return messageDelayed;
    }

    public void setMessageDelayed(Long messageDelayed) {
        this.messageDelayed = messageDelayed;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public QueueStatus getStatus() {
        return status == null ? null : QueueStatus.fromId(status);
    }

    @JmixProperty
    public String getStatusName() {
        return status == null ? null : QueueStatus.fromId(status).name();
    }

    public void setStatus(QueueStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public Long getReceiveMessageWaitTime() {
        return receiveMessageWaitTime;
    }

    public void setReceiveMessageWaitTime(Long recieveMessageWaitTime) {
        this.receiveMessageWaitTime = recieveMessageWaitTime;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public Long getVisibilityTimeout() {
        return visibilityTimeout;
    }

    public void setVisibilityTimeout(Long visibilityTimeout) {
        this.visibilityTimeout = visibilityTimeout;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreated() {
        return created;
    }

    public Long getMessagesInFlight() {
        return messagesInFlight;
    }

    public void setMessagesInFlight(Long messagesInFlight) {
        this.messagesInFlight = messagesInFlight;
    }

    public Long getMessagesAvailable() {
        return messagesAvailable;
    }

    public void setMessagesAvailable(Long messagesAvailable) {
        this.messagesAvailable = messagesAvailable;
    }

    public QueueType getType() {
        return type == null ? null : QueueType.fromId(type);
    }

    public void setType(QueueType type) {
        this.type = type == null ? null : type.getId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
