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

package io.jmix.awsqueueui.app;

import io.jmix.awsqueueui.entity.QueueInfo;
import io.jmix.awsqueueui.entity.QueueStatus;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("QueueStatusNavigator")
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class QueueStatusCache {
    protected Map<String, QueueInfo> pendingQueues;

    @PostConstruct
    protected void init() {
        pendingQueues = new ConcurrentHashMap<>();
    }

    public Collection<String> getPendingNames(){
        return pendingQueues.keySet();
    }

    public Collection<QueueInfo> getPendingQueues(){
        return pendingQueues.values();
    }

    public void removeFromCache(String name){
        pendingQueues.remove(name);
    }

    public boolean isNotAvailable(QueueInfo queue) {
        return pendingQueues.containsKey(queue.getName());
    }

    public boolean isNotAvailable(String queueName) {
        return pendingQueues.containsKey(queueName);
    }

    public void setPendingStatus(QueueInfo queue, QueueStatus status) {
        queue.setStatus(status);
        pendingQueues.put(queue.getName(), queue);
    }

    public QueueStatus getPendingStatus(QueueInfo queue) {
        if (isNotAvailable(queue)) {
            pendingQueues.get(queue.getName());
        }
        return QueueStatus.RUNNING;
    }

    public QueueInfo getPendingQueue(String name){
        return pendingQueues.get(name);
    }
}
