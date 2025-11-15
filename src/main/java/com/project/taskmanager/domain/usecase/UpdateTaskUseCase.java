package com.project.taskmanager.domain.usecase;

import com.project.taskmanager.domain.entity.Task;
import com.project.taskmanager.domain.exceptions.TaskNotFoundException;
import com.project.taskmanager.domain.exceptions.UnauthorizedAccessException;
import com.project.taskmanager.domain.repository.TaskRepository;
import com.project.taskmanager.domain.valueobject.Category;
import com.project.taskmanager.domain.valueobject.Priority;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class UpdateTaskUseCase {
    private final TaskRepository taskRepository;

    public Task execute(String taskId, String userId, String title, Priority priority,
                        LocalDateTime dueDate, Category category) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (!task.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("User not authorized to update this task");
        }

        task.update(title, priority, dueDate, category);
        taskRepository.update(task);

        return task;
    }
}