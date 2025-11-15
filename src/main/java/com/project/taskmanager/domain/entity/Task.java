package com.project.taskmanager.domain.entity;

import com.project.taskmanager.domain.valueobject.Category;
import com.project.taskmanager.domain.valueobject.Priority;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Task {
    private final String id;
    private String title;
    private Priority priority;
    private LocalDateTime dueDate;
    private Category category;
    private String userId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean reminderSent;

    public Task(String title, Priority priority, LocalDateTime dueDate, Category category, String userId) {
        this.id = UUID.randomUUID().toString();
        this.title = validateTitle(title);
        this.priority = priority;
        this.dueDate = dueDate;
        this.category = category;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.reminderSent = false;
    }

    // Constructor for reconstruction from persistence
    public Task(String id, String title, Priority priority, LocalDateTime dueDate,
                Category category, String userId, LocalDateTime createdAt,
                LocalDateTime updatedAt, boolean reminderSent) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.dueDate = dueDate;
        this.category = category;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reminderSent = reminderSent;
    }

    private String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (title.length() > 200) {
            throw new IllegalArgumentException("Title cannot exceed 200 characters");
        }
        return title.trim();
    }

    public void update(String title, Priority priority, LocalDateTime dueDate, Category category) {
        if (title != null) {
            this.title = validateTitle(title);
        }
        if (priority != null) {
            this.priority = priority;
        }
        if (dueDate != null) {
            this.dueDate = dueDate;
        }
        if (category != null) {
            this.category = category;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void markReminderSent() {
        this.reminderSent = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDue() {
        return dueDate != null && LocalDateTime.now().isAfter(dueDate) && !reminderSent;
    }
}