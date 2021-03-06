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

package io.jmix.cloudqueueui.app;

import io.jmix.core.Messages;
import io.jmix.cloudqueue.entity.QueueInfo;
import io.jmix.ui.screen.MessageBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("cloudqueue_QueueInfoDetailsHtmlGenerator")
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
                .append("<th>").append(messages.getMessage("io.jmix.cloudqueueui.app/tableDetails.Created")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.cloudqueueui.app/tableDetails.MaximumMessageSize")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.cloudqueueui.app/tableDetails.LastUpdated")).append("</th>")
                .append("<th></th>")
                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(awsFormat(queueInfo.getCreated(), LocalDateTime.class)).append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getQueueAttributes().getMaximumMessageSize(), Long.class))
                .append(" ").append(messages.getMessage("io.jmix.cloudqueueui.app/Bytes")).append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getLastUpdate(), LocalDateTime.class)).append("</td>");
        sb.append("</tr>").append("</table>");
        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>").append(messages.getMessage("io.jmix.cloudqueueui.app/tableDetails.MessageRetentionPeriod")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.cloudqueueui.app/tableDetails.DefaultVisibilityTimeout")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.cloudqueueui.app/tableDetails.MessagesAvailable")).append("</th>")
                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(awsFormat(queueInfo.getQueueAttributes().getMessageRetentionPeriod(), Long.class))
                .append(" ").append(messages.getMessage("io.jmix.cloudqueueui.app/TimeSecs")).append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getQueueAttributes().getVisibilityTimeout(), Long.class))
                .append(" ").append(messages.getMessage("io.jmix.cloudqueueui.app/TimeSecs")).append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getMessagesAvailable(), Long.class)).append("</td>");
        sb.append("</tr>").append("</table>");
        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>").append(messages.getMessage("io.jmix.cloudqueueui.app/tableDetails.DeliveryDelay")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.cloudqueueui.app/tableDetails.MessagesInFlight")).append("</th>")
                .append("<th>").append(messages.getMessage("io.jmix.cloudqueueui.app/tableDetails.ReceiveMessageWaitTime")).append("</th>")
                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(awsFormat(queueInfo.getQueueAttributes().getDeliveryTime(), Long.class))
                .append(" ").append(messages.getMessage("io.jmix.cloudqueueui.app/TimeSecs")).append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getMessagesInFlight(), Long.class)).append("</td>");
        sb.append("<td>").append(awsFormat(queueInfo.getQueueAttributes().getReceiveMessageWaitTime(), Long.class))
                .append(" ").append(messages.getMessage("io.jmix.cloudqueueui.app/TimeSecs")).append("</td>");
        sb.append("</tr>").append("</table>");
        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>").append(messages.getMessage("io.jmix.cloudqueueui.app/tableDetails.MessagesDelayed")).append("</th>")
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
        return "";
    }
}
