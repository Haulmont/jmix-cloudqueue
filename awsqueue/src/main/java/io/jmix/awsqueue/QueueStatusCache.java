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

import io.jmix.awsqueue.entity.QueueStatus;
import io.jmix.awsqueue.entity.QueueInfo;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("awsqueue_QueueStatusCache")
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class QueueStatusCache {
    protected Map<String, QueueInfo> pendingCreationQueues;
    protected Collection<String> deletedQueueUrls;

    @PostConstruct
    protected void init() {
        pendingCreationQueues = new ConcurrentHashMap<>();
        deletedQueueUrls = ConcurrentHashMap.newKeySet();
    }

    public boolean isOnCreation(String queueName) {
        return pendingCreationQueues.containsKey(queueName);
    }

    public void setOnCreation(QueueInfo queue) {
        queue.setStatus(QueueStatus.CREATING);
        pendingCreationQueues.put(queue.getName(), queue);
    }

    public void unassignCreated(String queueName) {
        pendingCreationQueues.remove(queueName);
    }

    public Collection<QueueInfo> getCreatedQueues() {
        return pendingCreationQueues.values();
    }

    public boolean isOnDeletion(String queueUrl) {
        return deletedQueueUrls.contains(queueUrl);
    }

    public void setOnDeletion(String queueUrl) {
        deletedQueueUrls.add(queueUrl);
    }

    public Collection<String> getDeletedQueueUrls() {
        return deletedQueueUrls;
    }

    public void setTotallyDeleted(String queueUrl) {
        if (isOnDeletion(queueUrl)) {
            deletedQueueUrls.add(queueUrl);
        }
    }

    public boolean isNotAvailable(QueueInfo queue) {
        return pendingCreationQueues.containsKey(queue.getName()) &&
                deletedQueueUrls.contains(queue.getName());
    }
}
