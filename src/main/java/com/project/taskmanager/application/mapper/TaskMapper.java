package com.project.taskmanager.application.mapper;


import com.project.taskmanager.application.dto.TaskResponse;
import com.project.taskmanager.domain.entity.Task;

import java.util.List;
import java.util.stream.Collectors;

public class TaskMapper {

    public static TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .priority(task.getPriority().getValue())
                .dueDate(task.getDueDate())
                .category(task.getCategory().name())
                .userId(task.getUserId())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public static List<TaskResponse> toResponseList(List<Task> tasks) {
        return tasks.stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());
    }
}
