package simplequeue_components.data;

import io.jmix.simplequeue.entity.QueueInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QueueInfoGenerator {

    public Map<String, QueueInfo> generate(int from, int to){
        Map<String, QueueInfo> queueInfoMap = new HashMap<>();

        for (int i = from; i < to; i++) {
            String queueUrl = "QueueUrl#" + i;
            queueInfoMap.put(queueUrl, createOne("Queue#" + i, queueUrl));
        }

        return queueInfoMap;
    }

    public QueueInfo createOne(String queueName, String queueUrl){
        QueueInfo queueInfo = new QueueInfo();
        queueInfo.setName(queueName);
        queueInfo.setId(UUID.randomUUID());
        queueInfo.setUrl(queueUrl);
        return queueInfo;
    }
}
