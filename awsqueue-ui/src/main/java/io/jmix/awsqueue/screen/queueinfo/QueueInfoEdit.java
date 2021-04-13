package io.jmix.awsqueue.screen.queueinfo;

import io.jmix.awsqueue.app.QueueInfoManager;
import io.jmix.awsqueue.entity.QueueInfo;
import io.jmix.awsqueue.entity.QueueType;
import io.jmix.ui.component.*;
import io.jmix.ui.screen.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@UiController("QueueInfo.edit")
@UiDescriptor("queue-info-edit.xml")
@EditedEntityContainer("queueInfoDc")
public class QueueInfoEdit extends StandardEditor<QueueInfo> {

    @Autowired
    private TextField<Long> maximumMessageSizeField;
    @Autowired
    private TextField<Long> deliveryTimeField;
    @Autowired
    private TextField<Long> messageRetentionPeriodField;
    @Autowired
    private TextField<String> nameField;
    @Autowired
    private TextField<Long> receiveMessageWaitTimeField;
    @Autowired
    private ComboBox<QueueType> typeField;
    @Autowired
    private TextField<Long> visibilityTimeoutField;
    @Autowired
    private QueueInfoManager queueInfoManager;

    @Subscribe("commitAndCloseBtn")
    public void onCommitAndCloseBtnClick(Button.ClickEvent event) throws ExecutionException, InterruptedException {
        queueInfoManager.createAsync(getEditedEntity(), collectAttributesFromFields());
    }

    @Subscribe("typeField")
    public void onTypeFieldValueChange(HasValue.ValueChangeEvent<QueueType> event) {
        nameField.setValue(getQueueName());
    }

    @Install(to = "nameField", subject = "validator")
    private void nameFieldValidator(String value) {
        if(!value.endsWith(".fifo")){
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

    protected Map<String, String> collectAttributesFromFields() {
        Map<String, String> map = new HashMap<>();
        putIfNotBlank(map, "MaximumMessageSize", maximumMessageSizeField.getRawValue());
        putIfNotBlank(map, "MessageRetentionPeriod", messageRetentionPeriodField.getRawValue());
        putIfNotBlank(map, "VisibilityTimeout", visibilityTimeoutField.getRawValue());
        putIfNotBlank(map, "DelaySeconds", deliveryTimeField.getRawValue());
        putIfNotBlank(map, "ReceiveMessageWaitTimeSeconds", receiveMessageWaitTimeField.getRawValue());
        boolean isFifo = typeField.getValue() == QueueType.FIFO;
        if (isFifo) {
            putIfNotBlank(map, "FifoQueue", Boolean.TRUE.toString());
        }

        return map;
    }

    private void putIfNotBlank(Map<String, String> map, String key, @Nullable String value) {
        if (StringUtils.isNotBlank(value)) {
            map.put(key, value);
        }
    }
}
