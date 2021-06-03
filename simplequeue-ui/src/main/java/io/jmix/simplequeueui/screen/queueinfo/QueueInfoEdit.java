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

package io.jmix.simplequeueui.screen.queueinfo;

import io.jmix.simplequeue.api.QueueManager;
import io.jmix.simplequeue.configuration.QueueProperties;
import io.jmix.simplequeue.entity.QueueAttributes;
import io.jmix.simplequeue.entity.QueueInfo;
import io.jmix.simplequeue.entity.QueueType;
import io.jmix.simplequeue.utils.CreateQueueRequestBuilder;
import io.jmix.simplequeue.utils.QueueInfoUtils;
import io.jmix.core.Metadata;
import io.jmix.ui.component.*;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@PrimaryEditorScreen(QueueInfo.class)
@UiController("simplequeue_QueueInfo.edit")
@UiDescriptor("queue-info-edit.xml")
@EditedEntityContainer("queueInfoDc")
public class QueueInfoEdit extends StandardEditor<QueueInfo> {
    @Autowired
    private TextField<String> nameField;
    @Autowired
    private TextField<String> physicalNameField;
    @Autowired
    private QueueManager queueManager;
    @Autowired
    private Metadata metadata;
    @Autowired
    private QueueProperties queueProperties;
    @Autowired
    private ComboBox<QueueType> typeField;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        if (getEditedEntity().getQueueAttributes() == null) {
            getEditedEntity().setQueueAttributes(metadata.create(QueueAttributes.class));
        }
    }

    @Subscribe("commitAndCloseBtn")
    public void onCommitAndCloseBtnClick(Button.ClickEvent event) {
        QueueInfo queueInfo = getEditedEntity();
        QueueAttributes attributes = queueInfo.getQueueAttributes();

        queueManager.createQueue(new CreateQueueRequestBuilder(queueInfo.getName())
                .fromQueueType(queueInfo.getType())
                .withDeliveryTime(attributes.getDeliveryTime())
                .withMaximumMessageSize(attributes.getMaximumMessageSize())
                .withMessageRetentionPeriod(attributes.getMessageRetentionPeriod())
                .withVisibilityTimeout(attributes.getVisibilityTimeout())
                .withReceiveMessageWaitTime(attributes.getReceiveMessageWaitTime())
                .build());
    }

    @Subscribe("nameField")
    public void onNameFieldTextChange(TextInputField.TextChangeEvent event) {
        physicalNameField.setValue(QueueInfoUtils.generatePhysicalName(
                nameField.getRawValue(),
                typeField.getValue(),
                queueProperties.getQueuePrefix()));
    }

    @Subscribe("typeField")
    public void onTypeFieldValueChange(HasValue.ValueChangeEvent<QueueType> event) {
        physicalNameField.setValue(QueueInfoUtils.generatePhysicalName(
                nameField.getRawValue(),
                typeField.getValue(),
                queueProperties.getQueuePrefix()));
    }


}