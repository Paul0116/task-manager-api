package com.project.taskmanager.domain.usecase;

import com.project.taskmanager.domain.entity.Task;
import com.project.taskmanager.domain.exceptions.TaskNotFoundException;
import com.project.taskmanager.domain.exceptions.UnauthorizedAccessException;
import com.project.taskmanager.domain.repository.TaskRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteTaskUseCase {
    private final TaskRepository taskRepository;

    public void execute(String taskId, String userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (!task.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("User not authorized to delete this task");
        }

        taskRepository.delete(taskId);
    }

}