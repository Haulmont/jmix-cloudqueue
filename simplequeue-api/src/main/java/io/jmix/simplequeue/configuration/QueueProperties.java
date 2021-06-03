package io.jmix.simplequeue.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "jmix.simplequeue.queue")
@ConstructorBinding
public class QueueProperties {
    protected String prefix;

    public QueueProperties(@DefaultValue("") String prefix) {
        this.prefix = prefix;
    }

    public String getQueuePrefix() {
        return prefix;
    }

    public void setQueuePrefix(String queuePrefix) {
        this.prefix = prefix;
    }
}
