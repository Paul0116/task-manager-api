package com.project.taskmanager.infrastructure.service;

import com.project.taskmanager.domain.entity.Task;
import com.project.taskmanager.domain.repository.TaskRepository;
import com.project.taskmanager.domain.valueobject.Category;
import com.project.taskmanager.domain.valueobject.Priority;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReminderServiceTest {

    @Mock
    private TaskRepository taskRepository;

    private ReminderService reminderService;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        reminderService = new ReminderService(taskRepository, 2, 1);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (reminderService != null) {
            reminderService.shutdown();
        }
        mocks.close();
    }

    @Test
    void testReminderProcessing() throws InterruptedException {
        // Arrange
        Task dueTask = new Task("Due Task", Priority.HIGH,
                LocalDateTime.now().minusMinutes(5),
                Category.WORK, "user1");

        when(taskRepository.findDueTasks()).thenReturn(Arrays.asList(dueTask));
        when(taskRepository.findById(dueTask.getId())).thenReturn(Optional.of(dueTask));

        // Act
        reminderService.start();
        TimeUnit.SECONDS.sleep(2);

        // Assert
        verify(taskRepository, atLeastOnce()).findDueTasks();
        verify(taskRepository, atLeastOnce()).update(any(Task.class));
    }
}