package com.project.taskmanager.domain.repository;

import com.project.taskmanager.domain.entity.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);
    Optional<Task> findById(String id);
    List<Task> findByUserId(String userId);
    List<Task> findByUserIdAndDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate);
    List<Task> findDueTasks();
    void update(Task task);
    void delete(String id);
}