package io.jmix.queue.app;

import io.jmix.queue.entity.QueueInfo;
import io.jmix.queue.entity.QueueStatus;
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
