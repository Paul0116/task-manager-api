package com.project.taskmanager.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private String id;
    private String title;
    private int priority;
    private LocalDateTime dueDate;
    private String category;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}