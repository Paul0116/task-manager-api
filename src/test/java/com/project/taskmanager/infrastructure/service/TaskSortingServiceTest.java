package com.project.taskmanager.infrastructure.service;

import com.project.taskmanager.domain.entity.Task;
import com.project.taskmanager.domain.valueobject.Category;
import com.project.taskmanager.domain.valueobject.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskSortingServiceTest {

    private TaskSortingService sortingService;

    @BeforeEach
    void setUp() {
        sortingService = new TaskSortingService();
    }

    @Test
    void testSortByPriority() {
        // Arrange
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Low Priority", Priority.LOW, LocalDateTime.now(), Category.WORK, "user1"));
        tasks.add(new Task("High Priority", Priority.HIGHEST, LocalDateTime.now(), Category.WORK, "user1"));
        tasks.add(new Task("Medium Priority", Priority.MEDIUM, LocalDateTime.now(), Category.WORK, "user1"));

        // Act
        Comparator<Task> comparator = sortingService.getComparator(TaskSortingService.SortCriteria.PRIORITY);
        tasks.sort(comparator);

        // Assert
        assertEquals(Priority.HIGHEST, tasks.get(0).getPriority());
        assertEquals(Priority.MEDIUM, tasks.get(1).getPriority());
        assertEquals(Priority.LOW, tasks.get(2).getPriority());
    }

    @Test
    void testSortByDueDate() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Future", Priority.MEDIUM, now.plusDays(5), Category.WORK, "user1"));
        tasks.add(new Task("Tomorrow", Priority.MEDIUM, now.plusDays(1), Category.WORK, "user1"));
        tasks.add(new Task("Next Week", Priority.MEDIUM, now.plusDays(7), Category.WORK, "user1"));

        // Act
        Comparator<Task> comparator = sortingService.getComparator(TaskSortingService.SortCriteria.DUE_DATE);
        tasks.sort(comparator);

        // Assert
        assertEquals("Tomorrow", tasks.get(0).getTitle());
        assertEquals("Future", tasks.get(1).getTitle());
        assertEquals("Next Week", tasks.get(2).getTitle());
    }

    @Test
    void testSortByCategory() {
        // Arrange
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Work Task", Priority.MEDIUM, LocalDateTime.now(), Category.WORK, "user1"));
        tasks.add(new Task("Health Task", Priority.MEDIUM, LocalDateTime.now(), Category.HEALTH, "user1"));
        tasks.add(new Task("Personal Task", Priority.MEDIUM, LocalDateTime.now(), Category.PERSONAL, "user1"));

        // Act
        Comparator<Task> comparator = sortingService.getComparator(TaskSortingService.SortCriteria.CATEGORY);
        tasks.sort(comparator);

        // Assert
        assertEquals(Category.HEALTH, tasks.get(0).getCategory());
        assertEquals(Category.PERSONAL, tasks.get(1).getCategory());
        assertEquals(Category.WORK, tasks.get(2).getCategory());
    }

    @Test
    void testInvalidSortCriteria() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            sortingService.getComparator("INVALID");
        });
    }
}