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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.jmix.awsqueue.utils.LocalDateTimeDeserializer;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JmixEntity(annotatedPropertiesOnly = true)
public class QueueInfo {
    private static final String FIFO_POSTFIX = ".fifo";

    @JmixGeneratedValue
    @JmixProperty(mandatory = true)
    @JmixId
    private UUID id;

    @JsonUnwrapped
    @JmixProperty
    private QueueAttributes queueAttributes;

    @InstanceName
    @JmixProperty(mandatory = true)
    private String name;

    @JmixProperty(mandatory = true)
    private String url;

    @JmixProperty(mandatory = true)
    private Integer type;

    @JmixProperty
    private Integer status;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("CreatedTimestamp")
    @JmixProperty
    private LocalDateTime created;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("LastModifiedTimestamp")
    @JmixProperty
    private LocalDateTime lastUpdate;

    @JsonProperty("ApproximateNumberOfMessages")
    @JmixProperty
    private Long messagesAvailable;

    @JsonProperty("ApproximateNumberOfMessagesNotVisible")
    @JmixProperty
    private Long messagesInFlight;

    @JsonProperty("ApproximateNumberOfMessagesDelayed")
    @JmixProperty
    private Long messageDelayed;

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public LocalDateTime getLastUpdate() {
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

    public Long getMessagesAvailable() {
        return messagesAvailable;
    }

    public void setMessagesAvailable(Long messagesAvailable) {
        this.messagesAvailable = messagesAvailable;
    }


    public Long getMessagesInFlight() {
        return messagesInFlight;
    }

    public void setMessagesInFlight(Long messagesInFlight) {
        this.messagesInFlight = messagesInFlight;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public QueueAttributes getQueueAttributes() {
        return queueAttributes;
    }

    public void setQueueAttributes(QueueAttributes queueAttributes) {
        this.queueAttributes = queueAttributes;
    }

    public String generatePhysicalName(String previewName, String prefix){
        String realName = previewName;
        if(StringUtils.isNotBlank(previewName) && type != null){
            if(QueueType.fromId(type) == QueueType.FIFO){
                if (!previewName.endsWith(FIFO_POSTFIX)) {
                    realName = realName + FIFO_POSTFIX;
                }
            } else {
                if (previewName.endsWith(FIFO_POSTFIX)) {
                    realName = previewName.replace(FIFO_POSTFIX, StringUtils.EMPTY);
                }
            }
            if(StringUtils.isNotBlank(prefix) && !previewName.startsWith(prefix)){
                realName = prefix + "_" + realName;
            }
        }
        return realName;
    }
}
