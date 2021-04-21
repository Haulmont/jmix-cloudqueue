package queue_beans;

import io.jmix.autoconfigure.awsqueue.QueueAutoConfiguration;
import io.jmix.awsqueue.QueueStatusCache;
import io.jmix.awsqueue.entity.QueueInfo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import queue_beans.data.QueueInfoGenerator;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class QueueStatusCacheTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class,
                    QueueAutoConfiguration.class,
                    CacheAutoConfiguration.class))
            .withPropertyValues("spring.datasource.url=jdbc:hsqldb:mem:testdb", "spring.datasource.username=sa")
            .withPropertyValues(
                    "jmix.awsqueue.region=eu-central-1",
                    "jmix.awsqueue.access-key=mockAccessKey",
                    "jmix.awsqueue.secret-key=mockSecretKey",
                    "jmix.awsqueue.queue-prefix=jmixTestPrefix"
            )
            .withAllowBeanDefinitionOverriding(true);
    private final QueueInfoGenerator queueInfoGenerator = new QueueInfoGenerator();

    @Test
    public void emptyCacheValidation() {
        this.contextRunner.run(context -> {
            QueueStatusCache cacheBean = context.getBean(QueueStatusCache.class);
            Map<String, QueueInfo> testData = queueInfoGenerator.generate(0, 100);

            cacheBean.invalidate(testData);

            assertThat(cacheBean.getDeletedQueueUrls()).isEmpty();
            assertThat(cacheBean.getCreatingQueues()).isEmpty();
        });
    }

    @Test
    public void allCreatedCacheQueuesLoadedFromApi() {
        this.contextRunner.run(context -> {
            QueueStatusCache cacheBean = context.getBean(QueueStatusCache.class);
            Map<String, QueueInfo> testData = queueInfoGenerator.generate(0, 100);
            testData.values().stream().limit(10L).forEach(cacheBean::setCreating);

            cacheBean.invalidate(testData);

            assertThat(cacheBean.getCreatingQueues()).isEmpty();
        });
    }

    @Test
    public void oneNotCreated() {
        this.contextRunner.run(context -> {
            QueueStatusCache cacheBean = context.getBean(QueueStatusCache.class);
            Map<String, QueueInfo> testData = queueInfoGenerator.generate(0, 100);
            testData.values().stream().limit(10L).forEach(cacheBean::setCreating);
            cacheBean.setCreating(queueInfoGenerator.createOne("NotCreated", "NotCreatedURL"));

            cacheBean.invalidate(testData);

            assertThat(cacheBean.getCreatingQueues()).hasSize(1);
        });
    }

    @Test
    public void manyNotCreated() {
        this.contextRunner.run(context -> {
            QueueStatusCache cacheBean = context.getBean(QueueStatusCache.class);
            Map<String, QueueInfo> testData = queueInfoGenerator.generate(0, 100);
            Map<String, QueueInfo> notCreated = queueInfoGenerator.generate(105, 115);
            testData.values().stream().limit(10L).forEach(cacheBean::setCreating);
            notCreated.values().forEach(cacheBean::setCreating);

            cacheBean.invalidate(testData);

            assertThat(cacheBean.getCreatingQueues()).hasSize(notCreated.size());
            assertThat(cacheBean.getCreatingQueues()).containsAll(notCreated.values());
        });
    }

    @Test
    public void allDeletedQueuesLoadedFromApi() {
        this.contextRunner.run(context -> {
            QueueStatusCache cacheBean = context.getBean(QueueStatusCache.class);
            Map<String, QueueInfo> testData = queueInfoGenerator.generate(0, 10);
            testData.keySet().forEach(cacheBean::setDeleting);

            cacheBean.invalidate(testData);

            assertThat(cacheBean.getDeletedQueueUrls()).hasSize(testData.size());
        });
    }

    @Test
    public void allDeletedQueuesNotLoaded() {
        this.contextRunner.run(context -> {
            QueueStatusCache cacheBean = context.getBean(QueueStatusCache.class);
            Map<String, QueueInfo> testData = queueInfoGenerator.generate(0, 100);
            Map<String, QueueInfo> deleting = queueInfoGenerator.generate(100, 200);
            deleting.keySet().forEach(cacheBean::setDeleting);

            cacheBean.invalidate(testData);

            assertThat(cacheBean.getDeletedQueueUrls()).isEmpty();
        });
    }

    @Test
    public void allDeletedQueuesNotLoadedFromEmptyApi() {
        this.contextRunner.run(context -> {
            QueueStatusCache cacheBean = context.getBean(QueueStatusCache.class);
            Map<String, QueueInfo> testData = queueInfoGenerator.generate(0, 0);
            Map<String, QueueInfo> deleting = queueInfoGenerator.generate(0, 100);
            deleting.keySet().forEach(cacheBean::setDeleting);

            cacheBean.invalidate(testData);

            assertThat(cacheBean.getDeletedQueueUrls()).isEmpty();
        });
    }

    @Test
    public void oneQueueDeletedFromApi() {
        this.contextRunner.run(context -> {
            QueueStatusCache cacheBean = context.getBean(QueueStatusCache.class);
            Map<String, QueueInfo> testData = queueInfoGenerator.generate(0, 100);
            testData.keySet().forEach(cacheBean::setDeleting);
            cacheBean.setDeleting("deletingQueueThatNotAppearsInApi");

            cacheBean.invalidate(testData);

            assertThat(cacheBean.getDeletedQueueUrls()).hasSize(testData.size());
        });
    }

    @Test
    public void oneQueueNotDeletedFromApi() {
        this.contextRunner.run(context -> {
            QueueStatusCache cacheBean = context.getBean(QueueStatusCache.class);
            Map<String, QueueInfo> testData = queueInfoGenerator.generate(0, 100);
            testData.keySet().forEach(cacheBean::setDeleting);
            cacheBean.setDeleting("NotLoadedQueue");
            assertThat(cacheBean.getDeletedQueueUrls()).hasSize(testData.size() + 1);

            cacheBean.invalidate(testData);

            assertThat(cacheBean.getDeletedQueueUrls()).hasSize(testData.size());
        });
    }

    @Test
    public void halfQueueNotDeletedFromApi() {
        this.contextRunner.run(context -> {
            int fullSizeOfApi = 100;

            QueueStatusCache cacheBean = context.getBean(QueueStatusCache.class);
            Map<String, QueueInfo> testData = queueInfoGenerator.generate(0, fullSizeOfApi);
            testData.keySet().stream().limit(50).forEach(cacheBean::setDeleting);

            cacheBean.invalidate(testData);

            assertThat(cacheBean.getDeletedQueueUrls()).hasSize(fullSizeOfApi - 50);
        });
    }

    @Test
    public void ApiNoMatchDeletedOrCreated() {
        this.contextRunner.run(context -> {
            QueueStatusCache cacheBean = context.getBean(QueueStatusCache.class);
            Map<String, QueueInfo> testData = queueInfoGenerator.generate(200, 1337);
            Map<String, QueueInfo> created = queueInfoGenerator.generate(0, 100);
            Map<String, QueueInfo> deleted = queueInfoGenerator.generate(5555, 5655);
            testData.putAll(deleted);

            created.values().forEach(cacheBean::setCreating);
            deleted.keySet().forEach(cacheBean::setDeleting);

            cacheBean.invalidate(testData);

            assertThat(cacheBean.getDeletedQueueUrls()).hasSize(deleted.size());
            assertThat(cacheBean.getCreatingQueues()).hasSize(created.size());
        });
    }

    @Test
    public void deletedQueuesNotContainsInApi() {
        this.contextRunner.run(context -> {
            QueueStatusCache cacheBean = context.getBean(QueueStatusCache.class);
            Map<String, QueueInfo> testData = queueInfoGenerator.generate(200, 1337);
            Map<String, QueueInfo> created = queueInfoGenerator.generate(0, 100);
            Map<String, QueueInfo> deleted = queueInfoGenerator.generate(5555, 5655);

            created.values().forEach(cacheBean::setCreating);
            deleted.keySet().forEach(cacheBean::setDeleting);

            cacheBean.invalidate(testData);

            assertThat(cacheBean.getDeletedQueueUrls()).isEmpty();
            assertThat(cacheBean.getCreatingQueues()).hasSize(created.size());
        });
    }

    @Test
    public void createdQueuesExistsInApi() {
        this.contextRunner.run(context -> {
            QueueStatusCache cacheBean = context.getBean(QueueStatusCache.class);
            Map<String, QueueInfo> testData = queueInfoGenerator.generate(200, 1337);
            Map<String, QueueInfo> created = queueInfoGenerator.generate(0, 100);
            Map<String, QueueInfo> deleted = queueInfoGenerator.generate(5555, 5655);
            testData.putAll(deleted);
            testData.putAll(created);

            created.values().forEach(cacheBean::setCreating);
            deleted.keySet().forEach(cacheBean::setDeleting);

            cacheBean.invalidate(testData);

            assertThat(cacheBean.getDeletedQueueUrls()).hasSize(deleted.size());
            assertThat(cacheBean.getCreatingQueues()).isEmpty();
        });
    }

    @Test
    public void allCacheRemovedFromDeletedAndCreated() {
        this.contextRunner.run(context -> {
            QueueStatusCache cacheBean = context.getBean(QueueStatusCache.class);
            Map<String, QueueInfo> testData = queueInfoGenerator.generate(200, 1337);
            Map<String, QueueInfo> created = queueInfoGenerator.generate(0, 100);
            Map<String, QueueInfo> deleted = queueInfoGenerator.generate(5555, 5655);
            testData.putAll(created);

            created.values().forEach(cacheBean::setCreating);
            deleted.keySet().forEach(cacheBean::setDeleting);

            cacheBean.invalidate(testData);

            assertThat(cacheBean.getDeletedQueueUrls()).isEmpty();
            assertThat(cacheBean.getCreatingQueues()).isEmpty();
        });
    }
}
