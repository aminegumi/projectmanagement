package com.jiraclone.backend.controller;

import com.jiraclone.backend.dto.TaskDTO;
import com.jiraclone.backend.model.Task;
import com.jiraclone.backend.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;



class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(userDetails.getUsername()).thenReturn("test@example.com");
    }

    @Test
    void createTask_ShouldReturnCreatedTask() {
        // Arrange
        Task task = new Task();
        Long projectId = 1L;
        TaskDTO expectedDto = new TaskDTO();
        expectedDto.setId(1L);
        when(taskService.createTask(any(Task.class), eq(projectId), eq("test@example.com")))
                .thenReturn(expectedDto);

        // Act
        ResponseEntity<TaskDTO> response = taskController.createTask(task, projectId, userDetails);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        assertEquals("/tasks/" + expectedDto.getId(),
                response.getHeaders().getLocation().getPath());
    }

    @Test
    void getTaskById_ShouldReturnTask() {
        // Arrange
        Long taskId = 1L;
        TaskDTO expectedDto = new TaskDTO();
        expectedDto.setId(taskId);
        when(taskService.getTaskById(taskId)).thenReturn(expectedDto);

        // Act
        ResponseEntity<TaskDTO> response = taskController.getTaskById(taskId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void updateTaskStatus_ShouldReturnUpdatedTask() {
        // Arrange
        Long taskId = 1L;
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "IN_PROGRESS");
        TaskDTO expectedDto = new TaskDTO();
        when(taskService.updateTaskStatus(eq(taskId), any(Task.Status.class))).thenReturn(expectedDto);

        // Act
        ResponseEntity<TaskDTO> response = taskController.updateTaskStatus(taskId, statusUpdate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void updateTaskPriority_ShouldReturnUpdatedTask() {
        // Arrange
        Long taskId = 1L;
        Map<String, String> priorityUpdate = new HashMap<>();
        priorityUpdate.put("priority", "HIGH");
        TaskDTO expectedDto = new TaskDTO();
        when(taskService.updateTaskPriority(eq(taskId), any(Task.Priority.class))).thenReturn(expectedDto);

        // Act
        ResponseEntity<TaskDTO> response = taskController.updatePriority(taskId, priorityUpdate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void getAssignedTasks_ShouldReturnTaskList() {
        // Arrange
        List<TaskDTO> expectedDtos = Arrays.asList(new TaskDTO(), new TaskDTO());
        when(taskService.getTasksByAssignee("test@example.com")).thenReturn(expectedDtos);

        // Act
        ResponseEntity<List<TaskDTO>> response = taskController.getAssignedTasks(userDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDtos, response.getBody());
    }
}