package io.jmix.awsqueue;

import com.amazonaws.services.sqs.model.CreateQueueRequest;
import io.jmix.awsqueue.entity.QueueInfo;

import javax.annotation.Nullable;
import java.util.List;

public interface QueueManager {
    List<QueueInfo> loadFromApi();
    List<QueueInfo> loadAll();
    @Nullable QueueInfo queueInfoFromUrl(String queueUrl);
    void createQueue(QueueInfo queueInfo);
    void deleteQueue(QueueInfo queueInfo);
}
