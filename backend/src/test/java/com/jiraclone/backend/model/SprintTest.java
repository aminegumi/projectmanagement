package com.jiraclone.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class SprintTest {
    
    private Sprint sprint;
    private Project project;
    
    @BeforeEach
    void setUp() {
        sprint = new Sprint();
        project = new Project();
        project.setId(1L);
    }

    @Test
    void testSprintCreation() {
        // Arrange
        Long id = 1L;
        String name = "Sprint 1";
        String goal = "Complete feature X";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusWeeks(2);
        Sprint.Status status = Sprint.Status.PLANNING;
        LocalDateTime now = LocalDateTime.now();

        // Act
        sprint.setId(id);
        sprint.setName(name);
        sprint.setGoal(goal);
        sprint.setStartDate(startDate);
        sprint.setEndDate(endDate);
        sprint.setStatus(status);
        sprint.setProject(project);
        sprint.setCreatedAt(now);
        sprint.setUpdatedAt(now);

        // Assert
        assertEquals(id, sprint.getId());
        assertEquals(name, sprint.getName());
        assertEquals(goal, sprint.getGoal());
        assertEquals(startDate, sprint.getStartDate());
        assertEquals(endDate, sprint.getEndDate());
        assertEquals(status, sprint.getStatus());
        assertEquals(project, sprint.getProject());
        assertEquals(now, sprint.getCreatedAt());
        assertEquals(now, sprint.getUpdatedAt());
        assertNotNull(sprint.getTasks());
    }

    @Test
    void testAddTask() {
        // Arrange
        Task task = new Task();
        task.setId(1L);

        // Act
        sprint.getTasks().add(task);

        // Assert
        assertTrue(sprint.getTasks().contains(task));
        assertEquals(1, sprint.getTasks().size());
    }

    @Test
    void testSprintStatus() {
        // Assert
        assertArrayEquals(new Sprint.Status[]{
            Sprint.Status.PLANNING,
            Sprint.Status.ACTIVE,
            Sprint.Status.COMPLETED
        }, Sprint.Status.values());
    }
}