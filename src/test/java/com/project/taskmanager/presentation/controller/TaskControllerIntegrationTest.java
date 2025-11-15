package com.project.taskmanager.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.taskmanager.application.dto.CreateTaskRequest;
import com.project.taskmanager.application.dto.UpdateTaskRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateTask_Success() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
                "Test Task",
                4,
                LocalDateTime.now().plusDays(1),
                "WORK"
        );

        mockMvc.perform(post("/api/tasks")
                        .header("X-User-Id", "user123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.priority").value(4))
                .andExpect(jsonPath("$.category").value("WORK"));
    }

    @Test
    void testGetTasks_WithSorting() throws Exception {
        // Create multiple tasks
        CreateTaskRequest task1 = new CreateTaskRequest("Task 1", 3,
                LocalDateTime.now().plusDays(1), "WORK");
        CreateTaskRequest task2 = new CreateTaskRequest("Task 2", 5,
                LocalDateTime.now().plusDays(2), "PERSONAL");

        mockMvc.perform(post("/api/tasks")
                        .header("X-User-Id", "user456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tasks")
                        .header("X-User-Id", "user456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task2)))
                .andExpect(status().isCreated());

        // Get tasks sorted by priority
        mockMvc.perform(get("/api/tasks")
                        .header("X-User-Id", "user456")
                        .param("sortBy", "PRIORITY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.tasks[0].priority").value(5));
    }

    @Test
    void testUpdateTask_Success() throws Exception {
        // Create a task first
        CreateTaskRequest createRequest = new CreateTaskRequest(
                "Original Task", 3, LocalDateTime.now().plusDays(1), "WORK");

        MvcResult createResult = mockMvc.perform(post("/api/tasks")
                        .header("X-User-Id", "user789")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String taskId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asText();

        // Update the task
        UpdateTaskRequest updateRequest = new UpdateTaskRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setPriority(5);

        mockMvc.perform(put("/api/tasks/" + taskId)
                        .header("X-User-Id", "user789")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.priority").value(5));
    }

    @Test
    void testDeleteTask_Success() throws Exception {
        // Create a task first
        CreateTaskRequest createRequest = new CreateTaskRequest(
                "Task to Delete", 2, LocalDateTime.now().plusDays(1), "PERSONAL");

        MvcResult createResult = mockMvc.perform(post("/api/tasks")
                        .header("X-User-Id", "user999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String taskId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asText();

        // Delete the task
        mockMvc.perform(delete("/api/tasks/" + taskId)
                        .header("X-User-Id", "user999"))
                .andExpect(status().isNoContent());

        // Verify task is deleted
        mockMvc.perform(get("/api/tasks/" + taskId)
                        .header("X-User-Id", "user999"))
                .andExpect(status().isNotFound());
    }
}