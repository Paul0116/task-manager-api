package com.project.taskmanager.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TaskListResponse {
    private List<TaskResponse> tasks;
    private int total;

    public TaskListResponse(List<TaskResponse> tasks) {
        this.tasks = tasks;
        this.total = tasks.size();
    }
}