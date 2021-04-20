package io.jmix.awsqueue.utils;

import io.jmix.awsqueue.entity.QueueType;
import org.apache.commons.lang3.StringUtils;

public final class QueueInfoUtils {
    private static final String FIFO_POSTFIX = ".fifo";

    public static String generatePhysicalName(String providedName, QueueType type, String prefix) {
        String realName = providedName;
        if (StringUtils.isNotBlank(providedName) && type != null) {
            if (type == QueueType.FIFO) {
                if (!providedName.endsWith(FIFO_POSTFIX)) {
                    realName = realName + FIFO_POSTFIX;
                }
            } else {
                if (providedName.endsWith(FIFO_POSTFIX)) {
                    realName = providedName.replace(FIFO_POSTFIX, StringUtils.EMPTY);
                }
            }
            if (StringUtils.isNotBlank(prefix) && !providedName.startsWith(prefix)) {
                realName = prefix + "_" + realName;
            }
        }
        return realName;
    }

    public static QueueType getTypeByName(String name) {
        if (name.endsWith(".fifo")) {
            return QueueType.FIFO;
        }
        return QueueType.STANDARD;
    }
}
