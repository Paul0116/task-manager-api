package com.project.taskmanager.infrastructure.config;

import com.project.taskmanager.domain.repository.TaskRepository;
import com.project.taskmanager.domain.usecase.CreateTaskUseCase;
import com.project.taskmanager.domain.usecase.DeleteTaskUseCase;
import com.project.taskmanager.domain.usecase.GetTasksUseCase;
import com.project.taskmanager.domain.usecase.UpdateTaskUseCase;
import com.project.taskmanager.infrastructure.persistence.InMemoryTaskRepository;
import com.project.taskmanager.infrastructure.service.ReminderService;
import com.project.taskmanager.infrastructure.service.TaskSortingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ApplicationConfig {

    @Value("${reminder.thread-pool-size:5}")
    private int reminderThreadPoolSize;

    @Value("${reminder.check-interval-seconds:60}")
    private long reminderCheckIntervalSeconds;

    @Bean
    public TaskRepository taskRepository() {
        log.info("Initializing TaskRepository");
        return new InMemoryTaskRepository();
    }

    @Bean
    public CreateTaskUseCase createTaskUseCase(TaskRepository taskRepository) {
        return new CreateTaskUseCase(taskRepository);
    }

    @Bean
    public GetTasksUseCase getTasksUseCase(TaskRepository taskRepository) {
        return new GetTasksUseCase(taskRepository);
    }

    @Bean
    public UpdateTaskUseCase updateTaskUseCase(TaskRepository taskRepository) {
        return new UpdateTaskUseCase(taskRepository);
    }

    @Bean
    public DeleteTaskUseCase deleteTaskUseCase(TaskRepository taskRepository) {
        return new DeleteTaskUseCase(taskRepository);
    }

    @Bean
    public TaskSortingService taskSortingService() {
        return new TaskSortingService();
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ReminderService reminderService(TaskRepository taskRepository) {
        log.info("Initializing ReminderService with thread pool size: {} and check interval: {}s",
                reminderThreadPoolSize, reminderCheckIntervalSeconds);
        return new ReminderService(taskRepository, reminderThreadPoolSize, reminderCheckIntervalSeconds);
    }
}