package io.jmix.queue.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;

public enum QueueStatus implements EnumClass<Integer> {

    RUNNING(10),
    ON_DELETE(20),
    ON_CREATE(30);

    private Integer id;

    QueueStatus(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static QueueStatus fromId(Integer id) {
        for (QueueStatus at : QueueStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
