package io.jmix.awsqueueui.app;

import io.jmix.awsqueue.entity.QueueInfo;
import io.jmix.core.Messages;
import io.jmix.ui.screen.MessageBundle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("awsqueue_QueueInfoDetailsHtmlGenerator")
public class QueueInfoDetailsHtmlGenerator {
    @Autowired
    protected Messages messages;
    @Autowired
    protected MessageBundle bundleMessage;

    @SuppressWarnings({"DuplicatedCode", "StringBufferReplaceableByString"})
    public String generateTableHtml(QueueInfo queueInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.Created")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.MaximumMessageSize")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.LastUpdated")).append("</th>")
                .append("<th></th>")
                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(awsFormat(queueInfo.getCreated(), LocalDateTime.class)).append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getQueueAttributes().getMaximumMessageSize(), Long.class))
                .append(messages.getMessage("io.jmix.awsqueueui.app/Bytes")).append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getLastUpdate(), LocalDateTime.class)).append("</td>");
        sb.append("</tr>").append("</table>");
        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.MessageRetentionPeriod")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.DefaultVisibilityTimeout")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.MessagesAvailable")).append("</th>")
                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(awsFormat(queueInfo.getQueueAttributes().getMessageRetentionPeriod(), Long.class))
                .append(messages.getMessage("io.jmix.awsqueueui.app/TimeSecs")).append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getQueueAttributes().getVisibilityTimeout(), Long.class))
                .append(messages.getMessage("io.jmix.awsqueueui.app/TimeSecs")).append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getMessagesAvailable(), Long.class)).append("</td>");
        sb.append("</tr>").append("</table>");
        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.DeliveryDelay")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.MessagesInFlight")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.ReceiveMessageWaitTime")).append("</th>")
                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(awsFormat(queueInfo.getQueueAttributes().getDeliveryTime(), Long.class))
                .append(messages.getMessage("io.jmix.awsqueueui.app/TimeSecs")).append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getMessagesInFlight(), Long.class)).append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getQueueAttributes().getReceiveMessageWaitTime(), Long.class))
                .append(messages.getMessage("io.jmix.awsqueueui.app/TimeSecs")).append("</td>");
        sb.append("</tr>").append("</table>");
        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.MessagesDelayed")).append("</th>")
                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(awsFormat(queueInfo.getMessageDelayed(), Long.class)).append("</td>");
        sb.append("</tr>").append("</table>");
        return sb.toString();
    }

    protected String awsFormat(Object o, Class<?> clazz) {
        if (o != null) {
            return o.toString();
        } else if (clazz.equals(String.class)) {
            return "-";
        } else if (clazz.equals(Long.class)) {
            return "0";
        } else if (clazz.equals(LocalDateTime.class)) {
            return "N/A";
        }

        return StringUtils.EMPTY;
    }
}
