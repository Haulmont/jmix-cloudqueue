package io.jmix.cloudqueue.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@ConfigurationProperties(prefix = "jmix.cloudqueue")
@ConstructorBinding
public class QueueProperties {
    protected String queuePrefix;
    protected Listener listener;

    public QueueProperties(
            Listener listener,
            @DefaultValue("") String queuePrefix) {
        this.queuePrefix = queuePrefix;
        this.listener = listener == null ? new Listener() : listener;
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
        private static final int THREAD_POOL_CORE_SIZE_DEFAULT = 5;
        private static final int LONG_POLLING_TIMEOUT_DEFAULT = 10000;
        private static final int WAITING_TIME_RECEIVE_REQUEST_DEFAULT = 5;
        private static final int MAX_NUMBER_OF_MESSAGES_DEFAULT = 10;

        protected int threadPoolCoreSize;
        protected int longPollingTimeout;
        protected int waitingTimeReceiveRequest;
        protected int maxNumberOfMessages;

        public Listener() {
            this.threadPoolCoreSize = THREAD_POOL_CORE_SIZE_DEFAULT;
            this.longPollingTimeout = LONG_POLLING_TIMEOUT_DEFAULT;
            this.waitingTimeReceiveRequest = WAITING_TIME_RECEIVE_REQUEST_DEFAULT;
            this.maxNumberOfMessages = MAX_NUMBER_OF_MESSAGES_DEFAULT;
        }

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
