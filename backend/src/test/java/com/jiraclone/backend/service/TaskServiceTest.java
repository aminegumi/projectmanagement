package com.jiraclone.backend.service;

import com.jiraclone.backend.dto.TaskDTO;
import com.jiraclone.backend.model.*;
import com.jiraclone.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private UserRepository userRepository;
    @Mock private SprintRepository sprintRepository;

    @InjectMocks private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTask_Success() {
        Long projectId = 1L;
        String email = "user@example.com";

        Project project = new Project();
        project.setId(projectId);
        project.setKey("PRJ");
        project.setTasks(new HashSet<>()); 

        User reporter = new User();
        reporter.setEmail(email);

        Task task = new Task();
        task.setTitle("Task 1");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(reporter));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        TaskDTO result = taskService.createTask(task, projectId, email);

        assertEquals("Task 1", result.getTitle());
        assertTrue(result.getTaskKey().startsWith("PRJ-"));
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testCreateTask_ProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
            taskService.createTask(new Task(), 1L, "email@example.com"));
    }

    @Test
    void testCreateTask_UserNotFound() {
        Project project = new Project();
        project.setId(1L);
        project.setKey("X");
        project.setTasks(new HashSet<>()); 

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            taskService.createTask(new Task(), 1L, "email@example.com"));
    }

    @Test
    void testGetTaskById_Success() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskDTO result = taskService.getTaskById(1L);

        assertEquals("Test", result.getTitle());
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    void testDeleteTask_Success() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        taskService.deleteTask(1L);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void testDeleteTask_NotFound() {
        when(taskRepository.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> taskService.deleteTask(1L));
    }
}
