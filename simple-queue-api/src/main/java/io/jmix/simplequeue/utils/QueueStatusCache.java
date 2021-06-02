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

package io.jmix.simplequeue.utils;

import io.jmix.simplequeue.entity.QueueInfo;
import io.jmix.simplequeue.entity.QueueStatus;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component("sqs_QueueStatusCache")
public class QueueStatusCache {
    protected Map<String, QueueInfo> creatingQueues = new ConcurrentHashMap<>();
    protected Set<String> deletedQueueUrls = ConcurrentHashMap.newKeySet();

    public void invalidate(Map<String, QueueInfo> actualData) {
        actualData.values().stream()
                .map(QueueInfo::getName)
                .forEach(creatingQueues::remove);
        deletedQueueUrls.retainAll(actualData.keySet());
    }

    public void setCreating(QueueInfo queue) {
        queue.setStatus(QueueStatus.CREATING);
        creatingQueues.put(queue.getName(), queue);
    }

    public Collection<QueueInfo> getCreatingQueues() {
        return creatingQueues.values();
    }

    public void setDeleting(String queueUrl) {
        deletedQueueUrls.add(queueUrl);
    }

    public Set<String> getDeletedQueueUrls() {
        return deletedQueueUrls;
    }

    public boolean isNotAvailable(QueueInfo queue) {
        return creatingQueues.containsKey(queue.getName()) &&
                deletedQueueUrls.contains(queue.getName());
    }
}
