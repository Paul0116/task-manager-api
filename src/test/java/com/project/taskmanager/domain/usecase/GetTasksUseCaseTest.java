package com.project.taskmanager.domain.usecase;

import com.project.taskmanager.domain.entity.Task;
import com.project.taskmanager.domain.repository.TaskRepository;
import com.project.taskmanager.domain.valueobject.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetTasksUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    private GetTasksUseCase getTasksUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        getTasksUseCase = new GetTasksUseCase(taskRepository);
    }

    @Test
    void testExecute_WithoutDateRange_Success() {
        // Arrange
        String userId = "user123";
        List<Task> mockTasks = Arrays.asList(
                Task.builder().id("1").userId(userId).title("Task 1").build(),
                Task.builder().id("2").userId(userId).title("Task 2").build()
        );

        when(taskRepository.findByUserId(userId)).thenReturn(mockTasks);

        // Act
        List<Task> result = getTasksUseCase.execute(userId, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Task 1", result.get(0).getTitle());
        assertEquals("Task 2", result.get(1).getTitle());
        verify(taskRepository, times(1)).findByUserId(userId);
        verify(taskRepository, never()).findByUserIdAndDateRange(anyString(), any(), any());
    }

    @Test
    void testExecute_WithDateRange_Success() {
        // Arrange
        String userId = "user123";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        List<Task> mockTasks = Arrays.asList(
                Task.builder().id("1").userId(userId).title("Task 1").dueDate(startDate.plusDays(1)).build(),
                Task.builder().id("2").userId(userId).title("Task 2").dueDate(startDate.plusDays(3)).build()
        );

        when(taskRepository.findByUserIdAndDateRange(userId, startDate, endDate)).thenReturn(mockTasks);

        // Act
        List<Task> result = getTasksUseCase.execute(userId, startDate, endDate, null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findByUserIdAndDateRange(userId, startDate, endDate);
        verify(taskRepository, never()).findByUserId(anyString());
    }

    @Test
    void testExecute_WithComparator_SortsByTitle() {
        // Arrange
        String userId = "user123";
        List<Task> mockTasks = Arrays.asList(
                Task.builder().id("1").userId(userId).title("Zebra Task").build(),
                Task.builder().id("2").userId(userId).title("Apple Task").build(),
                Task.builder().id("3").userId(userId).title("Mango Task").build()
        );

        when(taskRepository.findByUserId(userId)).thenReturn(mockTasks);
        Comparator<Task> titleComparator = Comparator.comparing(Task::getTitle);

        // Act
        List<Task> result = getTasksUseCase.execute(userId, null, null, titleComparator);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Apple Task", result.get(0).getTitle());
        assertEquals("Mango Task", result.get(1).getTitle());
        assertEquals("Zebra Task", result.get(2).getTitle());
        verify(taskRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testExecute_WithComparator_SortsByPriority() {
        // Arrange
        String userId = "user123";
        List<Task> mockTasks = Arrays.asList(
                Task.builder().id("1").userId(userId).title("Task 1").priority(Priority.LOW).build(),
                Task.builder().id("2").userId(userId).title("Task 2").priority(Priority.HIGH).build(),
                Task.builder().id("3").userId(userId).title("Task 3").priority(Priority.MEDIUM).build()
        );

        when(taskRepository.findByUserId(userId)).thenReturn(mockTasks);
        // Sort by priority in descending order (HIGH -> MEDIUM -> LOW)
        Comparator<Task> priorityComparator = Comparator.comparing(Task::getPriority).reversed();

        // Act
        List<Task> result = getTasksUseCase.execute(userId, null, null, priorityComparator);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(Priority.HIGH, result.get(0).getPriority());
        assertEquals(Priority.MEDIUM, result.get(1).getPriority());
        assertEquals(Priority.LOW, result.get(2).getPriority());
        verify(taskRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testExecute_WithDateRangeAndComparator_Success() {
        // Arrange
        String userId = "user123";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        List<Task> mockTasks = Arrays.asList(
                Task.builder().id("1").userId(userId).title("Task C").dueDate(startDate.plusDays(1)).build(),
                Task.builder().id("2").userId(userId).title("Task A").dueDate(startDate.plusDays(3)).build(),
                Task.builder().id("3").userId(userId).title("Task B").dueDate(startDate.plusDays(5)).build()
        );

        when(taskRepository.findByUserIdAndDateRange(userId, startDate, endDate)).thenReturn(mockTasks);
        Comparator<Task> titleComparator = Comparator.comparing(Task::getTitle);

        // Act
        List<Task> result = getTasksUseCase.execute(userId, startDate, endDate, titleComparator);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Task A", result.get(0).getTitle());
        assertEquals("Task B", result.get(1).getTitle());
        assertEquals("Task C", result.get(2).getTitle());
        verify(taskRepository, times(1)).findByUserIdAndDateRange(userId, startDate, endDate);
    }

    @Test
    void testExecute_EmptyResult() {
        // Arrange
        String userId = "user123";
        List<Task> emptyList = Arrays.asList();

        when(taskRepository.findByUserId(userId)).thenReturn(emptyList);

        // Act
        List<Task> result = getTasksUseCase.execute(userId, null, null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taskRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testExecute_WithOnlyStartDate_UsesDateRange() {
        // Arrange
        String userId = "user123";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        List<Task> mockTasks = Arrays.asList(
                Task.builder().id("1").userId(userId).title("Task 1").build()
        );

        when(taskRepository.findByUserIdAndDateRange(userId, startDate, endDate)).thenReturn(mockTasks);

        // Act
        List<Task> result = getTasksUseCase.execute(userId, startDate, endDate, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByUserIdAndDateRange(userId, startDate, endDate);
    }
}