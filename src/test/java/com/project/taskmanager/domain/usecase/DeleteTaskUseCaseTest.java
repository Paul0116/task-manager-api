package com.project.taskmanager.domain.usecase;

import com.project.taskmanager.domain.entity.Task;
import com.project.taskmanager.domain.exceptions.TaskNotFoundException;
import com.project.taskmanager.domain.exceptions.UnauthorizedAccessException;
import com.project.taskmanager.domain.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteTaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    private DeleteTaskUseCase deleteTaskUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        deleteTaskUseCase = new DeleteTaskUseCase(taskRepository);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        String taskId = "task123";
        String userId = "user123";
        Task task = Task.builder()
                .id(taskId)
                .userId(userId)
                .title("Test Task")
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(taskId);

        // Act
        deleteTaskUseCase.execute(taskId, userId);

        // Assert
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).delete(taskId);
    }

    @Test
    void testExecute_TaskNotFound() {
        // Arrange
        String taskId = "nonexistent123";
        String userId = "user123";

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            deleteTaskUseCase.execute(taskId, userId);
        });

        assertEquals("Task not found with id: " + taskId, exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).delete(anyString());
    }

    @Test
    void testExecute_UnauthorizedAccess() {
        // Arrange
        String taskId = "task123";
        String userId = "user123";
        String differentUserId = "user456";
        Task task = Task.builder()
                .id(taskId)
                .userId(differentUserId)
                .title("Test Task")
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () -> {
            deleteTaskUseCase.execute(taskId, userId);
        });

        assertEquals("User not authorized to delete this task", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).delete(anyString());
    }
}