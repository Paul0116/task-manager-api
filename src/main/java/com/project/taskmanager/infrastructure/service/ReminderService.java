package com.project.taskmanager.infrastructure.service;

import com.project.taskmanager.domain.entity.Task;
import com.project.taskmanager.domain.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ReminderService {

    private final TaskRepository taskRepository;
    private final ScheduledExecutorService scheduledExecutor;
    private final ExecutorService reminderExecutor;
    private final ConcurrentHashMap<String, ReentrantLock> taskLocks;
    private final long checkIntervalSeconds;

    public ReminderService(TaskRepository taskRepository, int threadPoolSize, long checkIntervalSeconds) {
        this.taskRepository = taskRepository;
        this.scheduledExecutor = Executors.newScheduledThreadPool(1);
        this.reminderExecutor = Executors.newFixedThreadPool(threadPoolSize);
        this.taskLocks = new ConcurrentHashMap<>();
        this.checkIntervalSeconds = checkIntervalSeconds;
    }

    public void start() {
        log.info("Starting Reminder Service with check interval of {} seconds", checkIntervalSeconds);

        scheduledExecutor.scheduleAtFixedRate(
                this::checkAndSendReminders,
                0,
                checkIntervalSeconds,
                TimeUnit.SECONDS
        );
    }

    private void checkAndSendReminders() {
        try {
            List<Task> dueTasks = taskRepository.findDueTasks();
            log.debug("Found {} due tasks to process", dueTasks.size());

            for (Task task : dueTasks) {
                reminderExecutor.submit(() -> processReminder(task));
            }
        } catch (Exception e) {
            log.error("Error checking for due tasks", e);
        }
    }

    private void processReminder(Task task) {
        ReentrantLock lock = taskLocks.computeIfAbsent(task.getId(), k -> new ReentrantLock());

        try {
            if (lock.tryLock(5, TimeUnit.SECONDS)) {
                try {
                    Task currentTask = taskRepository.findById(task.getId()).orElse(null);

                    if (currentTask != null && currentTask.isDue()) {
                        sendReminder(currentTask);
                        currentTask.markReminderSent();
                        taskRepository.update(currentTask);

                        log.info("Reminder sent for task: {} - '{}'",
                                currentTask.getId(), currentTask.getTitle());
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                log.warn("Could not acquire lock for task {} within timeout", task.getId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for lock on task {}", task.getId(), e);
        } catch (Exception e) {
            log.error("Error processing reminder for task {}", task.getId(), e);
        } finally {
            taskLocks.remove(task.getId());
        }
    }

    private void sendReminder(Task task) {
        log.info("REMINDER: Task '{}' (ID: {}) is due! Priority: {}, Category: {}",
                task.getTitle(),
                task.getId(),
                task.getPriority().getValue(),
                task.getCategory());
    }

    public void shutdown() {
        log.info("Shutting down Reminder Service");

        scheduledExecutor.shutdown();
        reminderExecutor.shutdown();

        try {
            if (!scheduledExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
            if (!reminderExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                reminderExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduledExecutor.shutdownNow();
            reminderExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        log.info("Reminder Service shut down complete");
    }
}