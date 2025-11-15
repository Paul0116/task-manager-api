package com.project.taskmanager.domain.usecase;

import com.project.taskmanager.domain.entity.Task;
import com.project.taskmanager.domain.repository.TaskRepository;
import com.project.taskmanager.domain.valueobject.Category;
import com.project.taskmanager.domain.valueobject.Priority;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CreateTaskUseCase {
    private final TaskRepository taskRepository;
    public Task execute(String title, Priority priority, LocalDateTime dueDate,
                        Category category, String userId) {
        Task task = new Task(title, priority, dueDate, category, userId);
        return taskRepository.save(task);
    }
}