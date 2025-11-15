package com.project.taskmanager.domain.usecase;

import com.project.taskmanager.domain.entity.Task;
import com.project.taskmanager.domain.repository.TaskRepository;
import com.project.taskmanager.domain.valueobject.Category;
import com.project.taskmanager.domain.valueobject.Priority;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateTaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    private CreateTaskUseCase createTaskUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        createTaskUseCase = new CreateTaskUseCase(taskRepository);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        String title = "Test Task";
        Priority priority = Priority.HIGH;
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
        Category category = Category.WORK;
        String userId = "user123";

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Task result = createTaskUseCase.execute(title, priority, dueDate, category, userId);

        // Assert
        assertNotNull(result);
        assertEquals(title, result.getTitle());
        assertEquals(priority, result.getPriority());
        assertEquals(dueDate, result.getDueDate());
        assertEquals(category, result.getCategory());
        assertEquals(userId, result.getUserId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }
}