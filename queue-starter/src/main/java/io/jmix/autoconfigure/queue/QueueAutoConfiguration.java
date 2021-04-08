package io.jmix.autoconfigure.queue;


import io.jmix.core.CoreConfiguration;
import io.jmix.queue.QueueConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CoreConfiguration.class, QueueConfiguration.class})
public class QueueAutoConfiguration {

}
