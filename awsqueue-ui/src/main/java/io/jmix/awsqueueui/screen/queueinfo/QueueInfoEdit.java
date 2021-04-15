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
import io.jmix.awsqueue.entity.QueueInfo;
import io.jmix.awsqueue.entity.QueueType;
import io.jmix.ui.component.*;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("QueueInfo.edit")
@UiDescriptor("queue-info-edit.xml")
@EditedEntityContainer("queueInfoDc")
public class QueueInfoEdit extends StandardEditor<QueueInfo> {

    @Autowired
    private TextField<String> nameField;
    @Autowired
    private ComboBox<QueueType> typeField;
    @Autowired
    private QueueInfoManager queueInfoManager;

    @Subscribe("commitAndCloseBtn")
    public void onCommitAndCloseBtnClick(Button.ClickEvent event) {
        queueInfoManager.createAsync(getEditedEntity());
    }

    @Subscribe("typeField")
    public void onTypeFieldValueChange(HasValue.ValueChangeEvent<QueueType> event) {
        if(!nameField.isEmpty())
        nameField.setValue(getQueueName());
    }

    @Install(to = "nameField", subject = "validator")
    private void nameFieldValidator(String value) {
        if (!value.endsWith(".fifo") && !typeField.isEmpty() && typeField.getValue() == (QueueType.FIFO)) {
            throw new ValidationException("Name must ends with .fifo postfix");
        }
    }

    protected String getQueueName() {
        if (typeField.getValue().equals(QueueType.FIFO)) {
            if (!nameField.getRawValue().endsWith(".fifo")) {
                return nameField.getRawValue() + ".fifo";
            }
        } else {
            if (nameField.getRawValue().endsWith(".fifo")) {
                return nameField.getRawValue().replace(".fifo", "");
            }
        }
        return nameField.getRawValue();
    }
}
