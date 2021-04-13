package io.jmix.autoconfigure.awsqueue;

import io.jmix.awsqueue.QueueConfiguration;
import io.jmix.awsqueue.QueueUiConfiguration;
import io.jmix.core.CoreConfiguration;
import io.jmix.data.DataConfiguration;
import io.jmix.uiexport.UiExportConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CoreConfiguration.class, DataConfiguration.class, QueueConfiguration.class, QueueUiConfiguration.class, UiExportConfiguration.class})
public class QueueUiAutoConfiguration {
}
