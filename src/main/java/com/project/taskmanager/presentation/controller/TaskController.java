package com.project.taskmanager.presentation.controller;

import com.project.taskmanager.application.dto.CreateTaskRequest;
import com.project.taskmanager.application.dto.TaskListResponse;
import com.project.taskmanager.application.dto.TaskResponse;
import com.project.taskmanager.application.dto.UpdateTaskRequest;
import com.project.taskmanager.application.mapper.TaskMapper;
import com.project.taskmanager.domain.entity.Task;
import com.project.taskmanager.domain.exceptions.TaskNotFoundException;
import com.project.taskmanager.domain.usecase.CreateTaskUseCase;
import com.project.taskmanager.domain.usecase.DeleteTaskUseCase;
import com.project.taskmanager.domain.usecase.GetTasksUseCase;
import com.project.taskmanager.domain.usecase.UpdateTaskUseCase;
import com.project.taskmanager.domain.valueobject.Category;
import com.project.taskmanager.domain.valueobject.Priority;
import com.project.taskmanager.infrastructure.service.TaskSortingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final GetTasksUseCase getTasksUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final TaskSortingService taskSortingService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @RequestHeader("X-User-Id") String userId) {

        log.info("Creating task for user: {}", userId);

        Priority priority = Priority.fromValue(request.getPriority());
        Category category = Category.fromString(request.getCategory());

        Task task = createTaskUseCase.execute(
                request.getTitle(),
                priority,
                request.getDueDate(),
                category,
                userId
        );

        log.info("Task created successfully: {}", task.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TaskMapper.toResponse(task));
    }

    @GetMapping
    public ResponseEntity<TaskListResponse> getTasks(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "CREATED_AT") String sortBy) {

        log.info("Getting tasks for user: {} with sortBy: {}", userId, sortBy);

        Comparator<Task> comparator = taskSortingService.getComparator(sortBy);

        List<Task> tasks = getTasksUseCase.execute(userId, startDate, endDate, comparator);

        TaskListResponse response = new TaskListResponse(TaskMapper.toResponseList(tasks));

        log.info("Retrieved {} tasks for user: {}", response.getTotal(), userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {

        log.info("Getting task {} for user: {}", id, userId);

        List<Task> userTasks = getTasksUseCase.execute(userId, null, null, null);
        Task task = userTasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        return ResponseEntity.ok(TaskMapper.toResponse(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdateTaskRequest request) {

        log.info("Updating task {} for user: {}", id, userId);

        Priority priority = request.getPriority() != null ?
                Priority.fromValue(request.getPriority()) : null;
        Category category = request.getCategory() != null ?
                Category.fromString(request.getCategory()) : null;

        Task task = updateTaskUseCase.execute(
                id,
                userId,
                request.getTitle(),
                priority,
                request.getDueDate(),
                category
        );

        log.info("Task updated successfully: {}", id);

        return ResponseEntity.ok(TaskMapper.toResponse(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {

        log.info("Deleting task {} for user: {}", id, userId);

        deleteTaskUseCase.execute(id, userId);

        log.info("Task deleted successfully: {}", id);

        return ResponseEntity.noContent().build();
    }


}