package com.jiraclone.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    
    private Epic epic;
    private Project project;
    
    @BeforeEach
    void setUp() {
        epic = new Epic();
        project = new Project();
        project.setId(1L);
    }

    @Test
    void testEpicCreation() {
        // Arrange
        Long id = 1L;
        String name = "Test Epic";
        String summary = "Epic Summary";
        Epic.Status status = Epic.Status.TODO;
        LocalDateTime now = LocalDateTime.now();

        // Act
        epic.setId(id);
        epic.setName(name);
        epic.setSummary(summary);
        epic.setStatus(status);
        epic.setProject(project);
        epic.setCreatedAt(now);
        epic.setUpdatedAt(now);

        // Assert
        assertEquals(id, epic.getId());
        assertEquals(name, epic.getName());
        assertEquals(summary, epic.getSummary());
        assertEquals(status, epic.getStatus());
        assertEquals(project, epic.getProject());
        assertEquals(now, epic.getCreatedAt());
        assertEquals(now, epic.getUpdatedAt());
        assertNotNull(epic.getStories());
    }

    @Test
    void testAddUserStory() {
        // Arrange
        UserStory story = new UserStory();
        story.setId(1L);

        // Act
        epic.getStories().add(story);

        // Assert
        assertTrue(epic.getStories().contains(story));
        assertEquals(1, epic.getStories().size());
    }

    @Test
    void testEpicStatus() {
        // Assert
        assertArrayEquals(new Epic.Status[]{
            Epic.Status.TODO,
            Epic.Status.IN_PROGRESS,
            Epic.Status.DONE
        }, Epic.Status.values());
    }
}