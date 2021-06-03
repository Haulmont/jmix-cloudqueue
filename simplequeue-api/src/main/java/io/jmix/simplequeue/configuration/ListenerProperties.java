package io.jmix.simplequeue.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "jmix.simplequeue.listener")
@ConstructorBinding
public class ListenerProperties {
    protected int threadPoolCoreSize;
    protected int longPollingTimeout;
    protected int waitingTimeReceiveRequest;
    protected int maxNumberOfMessages;

    public ListenerProperties(@DefaultValue("5") int threadPoolCoreSize,
                              @DefaultValue("10000") int longPollingTimeout,
                              @DefaultValue("5") int waitingTimeReceiveRequest,
                              @DefaultValue("10") int maxNumberOfMessages) {
        this.threadPoolCoreSize = threadPoolCoreSize;
        this.longPollingTimeout = longPollingTimeout;
        this.waitingTimeReceiveRequest = waitingTimeReceiveRequest;
        this.maxNumberOfMessages = maxNumberOfMessages;
    }

    public int getThreadPoolCoreSize() {
        return threadPoolCoreSize;
    }

    public void setThreadPoolCoreSize(int threadPoolCoreSize) {
        this.threadPoolCoreSize = threadPoolCoreSize;
    }

    public int getLongPollingTimeout() {
        return longPollingTimeout;
    }

    public void setLongPollingTimeout(int longPollingTimeout) {
        this.longPollingTimeout = longPollingTimeout;
    }

    public int getWaitingTimeReceiveRequest() {
        return waitingTimeReceiveRequest;
    }

    public void setWaitingTimeReceiveRequest(int waitingTimeReceiveRequest) {
        this.waitingTimeReceiveRequest = waitingTimeReceiveRequest;
    }

    public int getMaxNumberOfMessages() {
        return maxNumberOfMessages;
    }

    public void setMaxNumberOfMessages(int maxNumberOfMessages) {
        this.maxNumberOfMessages = maxNumberOfMessages;
    }
}
