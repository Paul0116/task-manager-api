package com.project.taskmanager.infrastructure.persistence;

import com.project.taskmanager.domain.entity.Task;
import com.project.taskmanager.domain.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryTaskRepository implements TaskRepository {
    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    @Override
    public synchronized Task save(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<Task> findByUserId(String userId) {
        return tasks.values().stream()
                .filter(task -> task.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByUserIdAndDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return tasks.values().stream()
                .filter(task -> task.getUserId().equals(userId))
                .filter(task -> task.getDueDate() != null)
                .filter(task -> !task.getDueDate().isBefore(startDate) &&
                        !task.getDueDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findDueTasks() {
        return tasks.values().stream()
                .filter(Task::isDue)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized void update(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public synchronized void delete(String id) {
        tasks.remove(id);
    }
}