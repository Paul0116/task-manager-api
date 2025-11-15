package com.project.taskmanager.domain.valueobject;

import lombok.Getter;

@Getter
public enum Priority {
    LOWEST(1),
    LOW(2),
    MEDIUM(3),
    HIGH(4),
    HIGHEST(5);

    private final int value;

    Priority(int value) {
        this.value = value;
    }

    public static Priority fromValue(int value) {
        for (Priority priority : Priority.values()) {
            if (priority.value == value) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid priority value: " + value + ". Must be between 1 and 5.");
    }
}
