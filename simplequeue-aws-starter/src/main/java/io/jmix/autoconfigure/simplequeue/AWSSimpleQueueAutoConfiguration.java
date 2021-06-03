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

package io.jmix.autoconfigure.simplequeue;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import io.jmix.core.CoreConfiguration;
import io.jmix.core.annotation.JmixModule;
import io.jmix.simplequeue.SimpleQueueAPIConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.aws.messaging.config.annotation.SqsConfiguration;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@ConfigurationPropertiesScan
@JmixModule(dependsOn = {CoreConfiguration.class})
@Import({CoreConfiguration.class, SqsConfiguration.class, SimpleQueueAPIConfiguration.class})
public class AWSSimpleQueueAutoConfiguration {

    @Autowired
    protected AWSQueueProperties awsQueueProperties;

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate() {
        return new QueueMessagingTemplate(amazonSQSAsyncClient());
    }

    @Bean
    public AmazonSQSAsyncClient amazonSQSAsyncClient() {
        if (awsQueueProperties.getAccessKey() != null && awsQueueProperties.getSecretKey() != null) {
            return (AmazonSQSAsyncClient) AmazonSQSAsyncClientBuilder
                    .standard()
                    .withRegion(awsQueueProperties.getRegion())
                    .withCredentials(new AWSStaticCredentialsProvider(getBasicAwsCredentials()))
                    .build();
        } else {
            return (AmazonSQSAsyncClient) AmazonSQSAsyncClientBuilder.defaultClient();
        }
    }

    private AWSCredentials getBasicAwsCredentials() {
        return new BasicAWSCredentials(awsQueueProperties.getAccessKey(), awsQueueProperties.getSecretKey());
    }

}
