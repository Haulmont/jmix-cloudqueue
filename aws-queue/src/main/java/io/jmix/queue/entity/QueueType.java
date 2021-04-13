package io.jmix.queue.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;

public enum QueueType implements EnumClass<Integer> {

    STANDARD(10),
    FIFO(20);

    private final Integer id;

    QueueType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static QueueType fromId(Integer id) {
        for (QueueType at : QueueType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
