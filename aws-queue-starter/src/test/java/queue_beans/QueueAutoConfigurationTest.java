package queue_beans;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.regions.Region;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.*;
import io.jmix.autoconfigure.queue.QueueAutoConfiguration;
import io.jmix.core.Stores;
import io.jmix.queue.QueueProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

public class QueueAutoConfigurationTest {
    //https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.3-Release-Notes#applicationcontextrunner-disables-bean-overriding-by-default
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class,
                    QueueAutoConfiguration.class,
                    CacheAutoConfiguration.class))
            .withPropertyValues("spring.datasource.url=jdbc:hsqldb:mem:testdb", "spring.datasource.username=sa")
            .withPropertyValues("jmix.cloud.region=eu-central-1",
                    "jmix.cloud.access-key=mockAccessKey",
                    "jmix.cloud.secret-key=mockSecretKey")
            .withAllowBeanDefinitionOverriding(true);

    @Test
    public void testQueueBeansDefinedOk() {
        this.contextRunner.run((context) -> {
            assertThat(context).hasBean("amazonSQSAsync");
            assertThat(context).getBean("amazonSQSAsync").isInstanceOf(AmazonSQSAsync.class);
            assertThat(context).getBean("amazonSQSAsync");
        });
    }

    @Test
    public void testQueueDefined() {
        this.contextRunner.run((context) -> {
            assertThat(context).hasBean("queueProperties");
            assertThat(context).getBean("queueProperties").isInstanceOf(QueueProperties.class);
        });
    }

    @Test
    public void testQueueHasProperties() {
        this.contextRunner.run((context) -> {
            QueueProperties properties = context.getBean(QueueProperties.class);
            assertThat(properties).hasFieldOrPropertyWithValue("accessKey", "mockAccessKey");
            assertThat(properties).hasFieldOrPropertyWithValue("secretKey", "mockSecretKey");
            assertThat(properties).hasFieldOrPropertyWithValue("region", "eu-central-1");
        });
    }
}
