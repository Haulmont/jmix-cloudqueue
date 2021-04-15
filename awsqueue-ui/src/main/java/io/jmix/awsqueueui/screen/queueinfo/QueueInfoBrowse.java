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

package io.jmix.awsqueueui.screen.queueinfo;

import io.jmix.awsqueue.app.QueueInfoManager;
import io.jmix.awsqueue.app.QueueStatusCache;
import io.jmix.awsqueue.entity.QueueInfo;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.component.*;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.screen.LookupComponent;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@UiController("awsqueue_QueueInfo.browse")
@UiDescriptor("queue-info-browse.xml")
@LookupComponent("queueInfoDataGrid")
public class QueueInfoBrowse extends StandardLookup<QueueInfo> {

    @Autowired
    private CollectionContainer<QueueInfo> queueInfoDc;
    @Autowired
    private QueueInfoManager queueInfoManager;
    @Autowired
    private QueueStatusCache queueStatusCache;
    @Autowired
    private DataGrid<QueueInfo> queueInfoDataGrid;
    @Autowired
    protected UiComponents uiComponents;

    @Subscribe
    protected void onInit(InitEvent event) {
        queueInfoDataGrid.setItemClickAction(new BaseAction("itemClickAction")
                .withHandler(actionPerformedEvent ->
                        queueInfoDataGrid.setDetailsVisible(queueInfoDataGrid.getSingleSelected(), true)));
    }

    @Subscribe
    public void onAfterInit(AfterInitEvent event) {
        List<QueueInfo> states = queueInfoManager.loadAll();

        queueInfoDc.setItems(states);
        getScreenData().loadAll();
    }

    @Install(to = "queueInfoDataGrid.remove", subject = "enabledRule")
    private boolean queueInfoDataGridRemoveEnabledRule() {
        return queueInfoDataGrid.getSingleSelected() != null &&
                !queueStatusCache.isNotAvailable(queueInfoDataGrid.getSingleSelected());
    }

    @Subscribe("queueInfoDataGrid.remove")
    public void onQueueStatesTableRemove(Action.ActionPerformedEvent event) {
        queueInfoManager.deleteAsync(queueInfoDataGrid.getSingleSelected());
    }

    @Install(to = "queueInfoDataGrid", subject = "detailsGenerator")
    protected Component ordersDataGridDetailsGenerator(QueueInfo queueInfo) {
        VBoxLayout mainLayout = uiComponents.create(VBoxLayout.class);
        mainLayout.setWidth("100%");
        mainLayout.setMargin(true);

        HBoxLayout headerBox = uiComponents.create(HBoxLayout.class);
        headerBox.setWidth("100%");

        Label<String> infoLabel = uiComponents.create(Label.TYPE_STRING);
        infoLabel.setHtmlEnabled(true);
        infoLabel.setStyleName("h1");
        infoLabel.setValue("Queue details:");

        Component closeButton = createCloseButton(queueInfo);
        headerBox.add(infoLabel);
        headerBox.add(closeButton);
        headerBox.expand(infoLabel);

        Component content = getContent(queueInfo);

        mainLayout.add(headerBox);
        mainLayout.add(content);
        mainLayout.expand(content);

        return mainLayout;
    }

    @SuppressWarnings("DuplicatedCode")
    protected Component getContent(QueueInfo queueInfo) {
        String lastUpdatedStr = "-";
        if (queueInfo.getLastUpdateDateTime() != null) {
            lastUpdatedStr = queueInfo.getLastUpdateDateTime().toString();
        }

        Label<String> content = uiComponents.create(Label.TYPE_STRING);
        content.setHtmlEnabled(true);
        content.setId("contentLabel");

        StringBuilder sb = new StringBuilder();
        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>Created</th>")
                .append("<th>MaximumMessageSize</th>")
                .append("<th>LastUpdated</th>")
                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(queueInfo.getCreated()).append("</td>");
        sb.append("<td>").append(queueInfo.getMaximumMessageSize()).append(" KB ").append("</td>");
        sb.append("<td>").append(lastUpdatedStr).append("</td>");
        sb.append("</tr>").append("</table>");

        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>MessageRetentionPeriod</th>")
                .append("<th>DefaultVisibilityTimeout</th>")
                .append("<th>MessagesAvailable</th>")
                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(queueInfo.getMessageRetentionPeriod()).append(" Days ").append("</td>");
        sb.append("<td>").append(queueInfo.getVisibilityTimeout()).append(" Seconds ").append("</td>");
        sb.append("<td>").append(queueInfo.getMessagesAvailable()).append("</td>");
        sb.append("</tr>").append("</table>");

        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>DeliveryDelay</th>")
                .append("<th>MessagesInFlight</th>")
                .append("<th>ReceiveMessageWaitTime</th>")
                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(queueInfo.getDeliveryTime()).append(" Seconds ").append("</td>");
        sb.append("<td>").append(queueInfo.getMessagesInFlight()).append("</td>");
        sb.append("<td>").append(queueInfo.getReceiveMessageWaitTime()).append(" Seconds ").append("</td>");
        sb.append("</tr>").append("</table>");

        sb.append("<table spacing=3px padding=3px>")
                .append("<tr>")
                .append("<th>MessagesDelayed</th>")
                .append("<th>ContentBasedDeduplication</th>")
                .append("<th>CloudStatus</th>")
                .append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append(queueInfo.getMessageDelayed()).append("</td>");
        sb.append("<td>").append("-").append("</td>");
        sb.append("<td>").append(queueInfo.getStatus().name()).append("</td>");
        sb.append("</tr>").append("</table>");


        content.setValue(sb.toString());

        return content;
    }


    protected Component createCloseButton(QueueInfo queueInfo) {
        Button closeButton = uiComponents.create(Button.class);
        closeButton.setIcon("font-icon:TIMES");
        BaseAction closeAction = new BaseAction("closeAction")
                .withHandler(actionPerformedEvent ->
                        queueInfoDataGrid.setDetailsVisible(queueInfo, false))
                .withCaption("");
        closeButton.setAction(closeAction);
        return closeButton;
    }
}
