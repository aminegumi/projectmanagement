package com.jiraclone.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    
    private Task task;
    private Project project;
    private Sprint sprint;
    private User assignee;
    private User reporter;
    
    @BeforeEach
    void setUp() {
        task = new Task();
        project = new Project();
        sprint = new Sprint();
        assignee = new User();
        reporter = new User();
        
        project.setId(1L);
        sprint.setId(1L);
        assignee.setId(1L);
        reporter.setId(2L);
    }

    @Test
    void testTaskCreation() {
        // Arrange
        Long id = 1L;
        String taskKey = "TP-1";
        String title = "Test Task";
        LocalDate dueDate = LocalDate.now();

        // Act
        task.setId(id);
        task.setTaskKey(taskKey);
        task.setTitle(title);
        task.setType(Task.Type.TASK);
        task.setPriority(Task.Priority.HIGH);
        task.setStatus(Task.Status.TODO);
        task.setDueDate(dueDate);
        task.setProject(project);
        task.setSprint(sprint);
        task.setAssignee(assignee);
        task.setReporter(reporter);

        // Assert
        assertEquals(id, task.getId());
        assertEquals(taskKey, task.getTaskKey());
        assertEquals(title, task.getTitle());
        assertEquals(Task.Type.TASK, task.getType());
        assertEquals(Task.Priority.HIGH, task.getPriority());
        assertEquals(Task.Status.TODO, task.getStatus());
        assertEquals(dueDate, task.getDueDate());
        assertEquals(project, task.getProject());
        assertEquals(sprint, task.getSprint());
        assertEquals(assignee, task.getAssignee());
        assertEquals(reporter, task.getReporter());
        assertNotNull(task.getComments());
    }

    @Test
    void testAddComment() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(1L);

        // Act
        task.getComments().add(comment);

        // Assert
        assertTrue(task.getComments().contains(comment));
        assertEquals(1, task.getComments().size());
    }

    @Test
    void testEnumValues() {
        // Assert
        assertArrayEquals(new Task.Type[]{
            Task.Type.TASK, Task.Type.BUG, Task.Type.STORY, Task.Type.EPIC
        }, Task.Type.values());
        
        assertArrayEquals(new Task.Priority[]{
            Task.Priority.HIGHEST, Task.Priority.HIGH, Task.Priority.MEDIUM,
            Task.Priority.LOW, Task.Priority.LOWEST
        }, Task.Priority.values());
        
        assertArrayEquals(new Task.Status[]{
            Task.Status.TODO, Task.Status.IN_PROGRESS,
            Task.Status.IN_REVIEW, Task.Status.DONE
        }, Task.Status.values());
    }
}