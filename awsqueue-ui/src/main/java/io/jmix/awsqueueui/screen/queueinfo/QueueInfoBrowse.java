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

import io.jmix.awsqueue.QueueManagerImpl;
import io.jmix.awsqueue.QueueStatusCache;
import io.jmix.awsqueue.entity.QueueInfo;
import io.jmix.awsqueueui.app.QueueInfoDetailsHtmlGenerator;
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
    private QueueManagerImpl queueInfoManager;
    @Autowired
    private QueueStatusCache queueStatusCache;
    @Autowired
    private DataGrid<QueueInfo> queueInfoDataGrid;
    @Autowired
    protected UiComponents uiComponents;
    @Autowired
    private QueueInfoDetailsHtmlGenerator queueInfoDetailsHtmlGenerator;

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
        queueInfoManager.deleteQueue(queueInfoDataGrid.getSingleSelected());
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

    protected Component getContent(QueueInfo queueInfo) {
        Label<String> content = uiComponents.create(Label.TYPE_STRING);
        content.setHtmlEnabled(true);
        content.setId("contentLabel");
        content.setValue(queueInfoDetailsHtmlGenerator.generateTableHtml(queueInfo));
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
