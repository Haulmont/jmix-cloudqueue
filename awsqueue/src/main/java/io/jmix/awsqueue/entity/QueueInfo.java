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

package io.jmix.awsqueue.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import io.micrometer.core.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JmixEntity
public class QueueInfo {

    @JsonIgnore
    @JmixGeneratedValue
    @JmixProperty(mandatory = true)
    @JmixId
    private UUID id;

    @JsonIgnore
    @InstanceName
    @JmixProperty(mandatory = true)
    private String name;

    @JsonIgnore
    @JmixProperty(mandatory = true)
    private String url;

    @JsonIgnore
    @JmixProperty(mandatory = true)
    private Integer type;

    @JsonIgnore
    private Integer status;

    @JsonProperty("CreatedTimestamp")
    private Long created;

    @JsonProperty("LastModifiedTimestamp")
    private Long lastUpdate;

    @JsonProperty("MaximumMessageSize")
    @Max(message = "Should be between 1 KB and 256 KB.", value = 262144)
    @Min(message = "Should be between 1 KB and 256 KB.", value = 1024)
    private Long maximumMessageSize;

    @JsonProperty("MessageRetentionPeriod")
    @Max(message = "Should be between 1 minute and 14 days.", value = 1209600)
    @Min(message = "Should be between 1 minute and 14 days.", value = 60)
    private Long messageRetentionPeriod;

    @JsonProperty("VisibilityTimeout")
    @Max(message = "Should be between 0 seconds and 12 hours.", value = 43200)
    @Min(message = "Should be between 0 seconds and 12 hours.", value = 0)
    private Long visibilityTimeout;

    @JsonProperty("ApproximateNumberOfMessages")
    private Long messagesAvailable;

    @JsonProperty("DelaySeconds")
    @Max(message = "Should be between 0 seconds and 15 minutes.", value = 900)
    @Min(message = "Should be between 0 seconds and 15 minutes.", value = 0)
    private Long deliveryTime;

    @JsonProperty("ApproximateNumberOfMessagesNotVisible")
    private Long messagesInFlight;

    @JsonProperty("ReceiveMessageWaitTimeSeconds")
    @Max(message = "Should be between 0 and 20 seconds.", value = 20)
    @Min(message = "Should be between 0 and 20 seconds.", value = 0)
    private Long receiveMessageWaitTime;

    @JsonProperty("ApproximateNumberOfMessagesDelayed")
    private Long messageDelayed;

    @JsonIgnore
    @JmixProperty
    public String getStatusName() {
        return status == null ? "-" : QueueStatus.fromId(status).name();
    }

    @JsonIgnore
    @Nullable
    @JmixProperty
    public LocalDateTime getCreatedDateTime() {
        if (created == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(created * 1000), ZoneId.systemDefault());
    }

    @JsonIgnore
    @Nullable
    @JmixProperty
    public LocalDateTime getLastUpdateDateTime() {
        if (lastUpdate == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(lastUpdate * 1000), ZoneId.systemDefault());
    }


    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getCreated() {
        return created;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public Long getMessageDelayed() {
        return messageDelayed;
    }

    public void setMessageDelayed(Long messageDelayed) {
        this.messageDelayed = messageDelayed;
    }

    public QueueStatus getStatus() {
        return status == null ? null : QueueStatus.fromId(status);
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
