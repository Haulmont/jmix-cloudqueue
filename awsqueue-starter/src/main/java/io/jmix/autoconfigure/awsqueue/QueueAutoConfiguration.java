package io.jmix.autoconfigure.awsqueue;


import io.jmix.core.CoreConfiguration;
import io.jmix.awsqueue.QueueConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CoreConfiguration.class, QueueConfiguration.class})
public class QueueAutoConfiguration {

}
