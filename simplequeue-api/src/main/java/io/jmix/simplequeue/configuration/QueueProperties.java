package io.jmix.simplequeue.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.Nullable;

@ConfigurationProperties(prefix = "jmix.simplequeue")
@ConstructorBinding
public class QueueProperties {
    protected String queuePrefix;
    protected Listener listener;

    public QueueProperties(
            @Nullable Listener listener,
            @DefaultValue("") String queuePrefix) {
        this.queuePrefix = queuePrefix;
        this.listener = listener;
    }

    public String getQueuePrefix() {
        return queuePrefix;
    }

    public void setQueuePrefix(String queuePrefix) {
        this.queuePrefix = queuePrefix;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public static class Listener {
        protected int threadPoolCoreSize;
        protected int longPollingTimeout;
        protected int waitingTimeReceiveRequest;
        protected int maxNumberOfMessages;

        public Listener(@DefaultValue("5") int threadPoolCoreSize,
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
}
