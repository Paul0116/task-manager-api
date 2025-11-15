package com.project.taskmanager.domain.usecase;

import com.project.taskmanager.domain.entity.Task;
import com.project.taskmanager.domain.exceptions.TaskNotFoundException;
import com.project.taskmanager.domain.exceptions.UnauthorizedAccessException;
import com.project.taskmanager.domain.repository.TaskRepository;
import com.project.taskmanager.domain.valueobject.Category;
import com.project.taskmanager.domain.valueobject.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateTaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    private UpdateTaskUseCase updateTaskUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        updateTaskUseCase = new UpdateTaskUseCase(taskRepository);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        String taskId = "task123";
        String userId = "user123";
        String newTitle = "Updated Task";
        Priority newPriority = Priority.HIGH;
        LocalDateTime newDueDate = LocalDateTime.now().plusDays(5);
        Category newCategory = Category.PERSONAL;

        Task existingTask = Task.builder()
                .id(taskId)
                .userId(userId)
                .title("Old Task")
                .priority(Priority.LOW)
                .dueDate(LocalDateTime.now().plusDays(1))
                .category(Category.WORK)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        doNothing().when(taskRepository).update(any(Task.class));

        // Act
        Task result = updateTaskUseCase.execute(taskId, userId, newTitle, newPriority, newDueDate, newCategory);

        // Assert
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals(userId, result.getUserId());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).update(existingTask);
    }

    @Test
    void testExecute_TaskNotFound() {
        // Arrange
        String taskId = "nonexistent123";
        String userId = "user123";
        String title = "Updated Task";
        Priority priority = Priority.HIGH;
        LocalDateTime dueDate = LocalDateTime.now().plusDays(5);
        Category category = Category.PERSONAL;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            updateTaskUseCase.execute(taskId, userId, title, priority, dueDate, category);
        });

        assertEquals("Task not found with id: " + taskId, exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).update(any(Task.class));
    }

    @Test
    void testExecute_UnauthorizedAccess() {
        // Arrange
        String taskId = "task123";
        String userId = "user123";
        String differentUserId = "user456";
        String title = "Updated Task";
        Priority priority = Priority.HIGH;
        LocalDateTime dueDate = LocalDateTime.now().plusDays(5);
        Category category = Category.PERSONAL;

        Task existingTask = Task.builder()
                .id(taskId)
                .userId(differentUserId)
                .title("Old Task")
                .priority(Priority.LOW)
                .dueDate(LocalDateTime.now().plusDays(1))
                .category(Category.WORK)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () -> {
            updateTaskUseCase.execute(taskId, userId, title, priority, dueDate, category);
        });

        assertEquals("User not authorized to update this task", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).update(any(Task.class));
    }

    @Test
    void testExecute_PartialUpdate() {
        // Arrange
        String taskId = "task123";
        String userId = "user123";
        String newTitle = "Partially Updated Task";
        Priority originalPriority = Priority.MEDIUM;
        LocalDateTime originalDueDate = LocalDateTime.now().plusDays(3);
        Category originalCategory = Category.WORK;

        Task existingTask = Task.builder()
                .id(taskId)
                .userId(userId)
                .title("Old Task")
                .priority(originalPriority)
                .dueDate(originalDueDate)
                .category(originalCategory)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        doNothing().when(taskRepository).update(any(Task.class));

        // Act
        Task result = updateTaskUseCase.execute(taskId, userId, newTitle, originalPriority,
                originalDueDate, originalCategory);

        // Assert
        assertNotNull(result);
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).update(existingTask);
    }

    @Test
    void testExecute_UpdateAllFields() {
        // Arrange
        String taskId = "task123";
        String userId = "user123";
        String newTitle = "Completely Updated Task";
        Priority newPriority = Priority.HIGH;
        LocalDateTime newDueDate = LocalDateTime.now().plusDays(10);
        Category newCategory = Category.SHOPPING;

        Task existingTask = Task.builder()
                .id(taskId)
                .userId(userId)
                .title("Original Task")
                .priority(Priority.LOW)
                .dueDate(LocalDateTime.now().plusDays(1))
                .category(Category.WORK)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        doNothing().when(taskRepository).update(any(Task.class));

        // Act
        Task result = updateTaskUseCase.execute(taskId, userId, newTitle, newPriority, newDueDate, newCategory);

        // Assert
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals(userId, result.getUserId());

        // Capture and verify the updated task
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).update(taskCaptor.capture());

        Task capturedTask = taskCaptor.getValue();
        assertEquals(existingTask, capturedTask);
    }
}