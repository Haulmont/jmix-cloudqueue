package io.jmix.awsqueueui.app;

import io.jmix.awsqueue.entity.QueueInfo;
import io.jmix.core.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("awsqueue_QueueInfoDetailsHtmlGenerator") //todo mb prefix awsqueueui
public class QueueInfoDetailsHtmlGenerator {
    @Autowired
    protected Messages messages;

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
        sb.append("<td>").append(awsFormat(queueInfo.getCreated())).append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getQueueAttributes().getMaximumMessageSize())).append(" Bytes ").append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getLastUpdate())).append("</td>");
        sb.append("</tr>").append("</table>");

        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.MessageRetentionPeriod")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.DefaultVisibilityTimeout")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.MessagesAvailable")).append("</th>")
                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(awsFormat(queueInfo.getQueueAttributes().getMessageRetentionPeriod())).append(" Seconds ").append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getQueueAttributes().getVisibilityTimeout())).append(" Seconds ").append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getMessagesAvailable())).append("</td>");
        sb.append("</tr>").append("</table>");

        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.DeliveryDelay")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.MessagesInFlight")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.ReceiveMessageWaitTime")).append("</th>")

                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(awsFormat(queueInfo.getQueueAttributes().getDeliveryTime())).append(" Seconds ").append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getMessagesInFlight())).append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getQueueAttributes().getReceiveMessageWaitTime())).append(" Seconds ").append("</td>");
        sb.append("</tr>").append("</table>");

        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.MessagesDelayed")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.ContentBasedDeduplication")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.awsqueueui.app/tableDetails.CloudStatus")).append("</th>")
                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(awsFormat(queueInfo.getMessageDelayed())).append("</td>");
        //todo content based
        sb.append("<td>").append("NotImplemented").append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getStatus())).append("</td>");
        sb.append("</tr>").append("</table>");

        return sb.toString();
    }

    protected String awsFormat(Object o) {
        if (o != null) {
            return o.toString();
        }
        return "-";
    }
}
