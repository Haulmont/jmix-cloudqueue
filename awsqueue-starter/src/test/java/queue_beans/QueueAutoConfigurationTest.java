package queue_beans;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import io.jmix.autoconfigure.awsqueue.QueueAutoConfiguration;
import io.jmix.awsqueue.QueueProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

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
