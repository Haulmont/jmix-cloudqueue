package io.jmix.simplequeue.api;

public interface QueueProperties {

    String getAccessKey();

    void setAccessKey(String accessKey);

    String getSecretKey();

    void setSecretKey(String secretKey);

    String getRegion();

    void setRegion(String region);

    default String getEndpointConfiguration() {
        return "";
    }

    default void setEndpointConfiguration(String endpointConfiguration) {
    }

    String getQueuePrefix();

    void setQueuePrefix(String queueFamilyTag);

}
