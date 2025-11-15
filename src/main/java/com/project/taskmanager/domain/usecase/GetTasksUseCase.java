package com.project.taskmanager.domain.usecase;


import com.project.taskmanager.domain.entity.Task;
import com.project.taskmanager.domain.repository.TaskRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GetTasksUseCase {
    private final TaskRepository taskRepository;

    public List<Task> execute(String userId, LocalDateTime startDate,
                              LocalDateTime endDate, Comparator<Task> comparator) {
        List<Task> tasks;

        if (startDate != null && endDate != null) {
            tasks = taskRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        } else {
            tasks = taskRepository.findByUserId(userId);
        }

        if (comparator != null) {
            return tasks.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }

        return tasks;
    }
}