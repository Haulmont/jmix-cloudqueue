package io.jmix.awsqueue;

import io.micrometer.core.lang.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "jmix.awsqueue")
@ConstructorBinding
public class QueueProperties {

    protected String accessKey;
    protected String secretKey;
    protected String region;

    protected String queueFamilyTag;

    public QueueProperties(String accessKey, String secretKey, String region,
                           @Nullable String queueFamilyTag) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.queueFamilyTag = queueFamilyTag;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getQueueFamilyTag() {
        return queueFamilyTag;
    }

    public void setQueueFamilyTag(String queueFamilyTag) {
        this.queueFamilyTag = queueFamilyTag;
    }
}
