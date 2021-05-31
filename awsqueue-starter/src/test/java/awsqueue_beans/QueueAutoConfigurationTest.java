/*
 * Copyright 2021 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package awsqueue_beans;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import io.jmix.autoconfigure.sqs.QueueAutoConfiguration;
import io.jmix.awsqueue.QueueProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class QueueAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(QueueAutoConfiguration.class, CacheAutoConfiguration.class))
            .withPropertyValues(
                    "jmix.awsqueue.region=eu-central-1",
                    "jmix.awsqueue.access-key=mockAccessKey",
                    "jmix.awsqueue.secret-key=mockSecretKey",
                    "jmix.awsqueue.queue-prefix=jmixTestPrefix"
            );

    @Test
    public void testQueueBeansDefinedByClassOk() {
        this.contextRunner.run(context -> {
            assertThat(context).getBean(AmazonSQSAsyncClient.class).isExactlyInstanceOf(AmazonSQSAsyncClient.class);
            assertThat(context).getBeans(AmazonSQS.class).size().isPositive();
        });
    }

    @Test
    public void testQueuePropertiesDefined() {
        this.contextRunner.run(context -> {
            assertThat(context).getBean(QueueProperties.class).isExactlyInstanceOf(QueueProperties.class);
        });
    }

    @Test
    public void testQueueHasProperties() {
        this.contextRunner.run(context -> {
            QueueProperties properties = context.getBean(QueueProperties.class);
            assertThat(properties).hasFieldOrPropertyWithValue("accessKey", "mockAccessKey");
            assertThat(properties).hasFieldOrPropertyWithValue("secretKey", "mockSecretKey");
            assertThat(properties).hasFieldOrPropertyWithValue("region", "eu-central-1");
            assertThat(properties).hasFieldOrPropertyWithValue("queuePrefix", "jmixTestPrefix");
        });
    }
}
