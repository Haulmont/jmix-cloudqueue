package io.jmix.awsqueueui.app;

import io.jmix.awsqueue.entity.QueueInfo;
import io.jmix.ui.screen.MessageBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("awsqueue_QueueInfoDetailsHtmlGenerator") //todo mb prefix awsqueueui
public class QueueInfoDetailsHtmlGenerator {
    @Autowired
    protected MessageBundle messageBundle;

    @SuppressWarnings({"DuplicatedCode", "StringBufferReplaceableByString"})
    public String generateTableHtml(QueueInfo queueInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>").append(messageBundle.getMessage("tableDetails.Created")).append("</th>")
                .append("<th>").append(messageBundle.getMessage("tableDetails.MaximumMessageSize")).append("</th>")
                .append("<th>").append(messageBundle.getMessage("tableDetails.LastUpdated")).append("</th>")
                .append("<th></th>")
                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(awsFormat(queueInfo.getCreated())).append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getMaximumMessageSize())).append(" KB ").append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getLastUpdate())).append("</td>");
        sb.append("</tr>").append("</table>");

        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>").append(messageBundle.getMessage("tableDetails.MessageRetentionPeriod")).append("</th>")
                .append("<th>").append(messageBundle.getMessage("tableDetails.DefaultVisibilityTimeout")).append("</th>")
                .append("<th>").append(messageBundle.getMessage("tableDetails.MessagesAvailable")).append("</th>")
                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(awsFormat(queueInfo.getMessageRetentionPeriod())).append(" Days ").append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getVisibilityTimeout())).append(" Seconds ").append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getMessagesAvailable())).append("</td>");
        sb.append("</tr>").append("</table>");

        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>").append(messageBundle.getMessage("tableDetails.DeliveryDelay")).append("</th>")
                .append("<th>").append(messageBundle.getMessage("tableDetails.MessagesInFlight")).append("</th>")
                .append("<th>").append(messageBundle.getMessage("tableDetails.ReceiveMessageWaitTime")).append("</th>")

                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(awsFormat(queueInfo.getDeliveryTime())).append(" Seconds ").append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getMessagesInFlight())).append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getReceiveMessageWaitTime())).append(" Seconds ").append("</td>");
        sb.append("</tr>").append("</table>");

        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>").append(messageBundle.getMessage("tableDetails.MessagesDelayed")).append("</th>")
                .append("<th>").append(messageBundle.getMessage("tableDetails.ContentBasedDeduplication")).append("</th>")
                .append("<th>").append(messageBundle.getMessage("tableDetails.CloudStatus")).append("</th>")
                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(awsFormat(queueInfo.getMessageDelayed())).append("</td>");
        //todo content based
        sb.append("<td>").append("NotImplemented").append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getStatus().name())).append("</td>");
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
