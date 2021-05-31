package io.jmix.queue.utils;

import com.google.common.base.Strings;
import io.jmix.queue.entity.QueueType;

public final class QueueInfoUtils {
    private static final String FIFO_POSTFIX = ".fifo";

    public static String generatePhysicalName(String providedName, QueueType type, String prefix) {
        String realName = providedName;
        if (!Strings.isNullOrEmpty(providedName) && type != null) {
            if (type == QueueType.FIFO) {
                if (!providedName.endsWith(FIFO_POSTFIX)) {
                    realName = realName + FIFO_POSTFIX;
                }
            } else {
                if (providedName.endsWith(FIFO_POSTFIX)) {
                    realName = providedName.replace(FIFO_POSTFIX, "");
                }
            }
            if (!Strings.isNullOrEmpty(prefix) && !providedName.startsWith(prefix)) {
                realName = prefix + "_" + realName;
            }
        }
        return realName;
    }

    public static QueueType getTypeByName(String name) {
        if (name.endsWith(FIFO_POSTFIX)) {
            return QueueType.FIFO;
        }
        return QueueType.STANDARD;
    }
}
