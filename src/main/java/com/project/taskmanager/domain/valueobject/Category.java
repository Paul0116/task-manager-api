package com.project.taskmanager.domain.valueobject;

public enum Category {
    WORK,
    PERSONAL,
    SHOPPING,
    HEALTH,
    EDUCATION,
    FINANCE,
    OTHER;

    public static Category fromString(String category) {
        try {
            return Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }
    }
}