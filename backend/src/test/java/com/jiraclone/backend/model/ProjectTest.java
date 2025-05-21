package com.jiraclone.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {
    
    private Project project;
    private User lead;
    
    @BeforeEach
    void setUp() {
        project = new Project();
        lead = new User();
        lead.setId(1L);
    }

    @Test
    void testProjectCreation() {
        // Arrange
        Long id = 1L;
        String name = "Test Project";
        String key = "TP";
        String description = "Test Description";

        // Act
        project.setId(id);
        project.setName(name);
        project.setKey(key);
        project.setDescription(description);
        project.setLead(lead);

        // Assert
        assertEquals(id, project.getId());
        assertEquals(name, project.getName());
        assertEquals(key, project.getKey());
        assertEquals(description, project.getDescription());
        assertEquals(lead, project.getLead());
        assertNotNull(project.getMembers());
        assertNotNull(project.getSprints());
        assertNotNull(project.getEpics());
        assertNotNull(project.getStories());
        assertNotNull(project.getTasks());
        assertNotNull(project.getReports());
    }

    @Test
    void testAddMember() {
        // Arrange
        User member = new User();
        member.setId(2L);

        // Act
        project.getMembers().add(member);

        // Assert
        assertTrue(project.getMembers().contains(member));
        assertEquals(1, project.getMembers().size());
    }

    @Test
    void testAddSprint() {
        // Arrange
        Sprint sprint = new Sprint();
        sprint.setId(1L);

        // Act
        project.getSprints().add(sprint);

        // Assert
        assertTrue(project.getSprints().contains(sprint));
        assertEquals(1, project.getSprints().size());
    }

    @Test
    void testProjectEqualsAndHashCode() {
        // Arrange
        Project project1 = new Project();
        Project project2 = new Project();
        
        project1.setId(1L);
        project2.setId(1L);

        // Assert
        assertEquals(project1, project2);
        assertEquals(project1.hashCode(), project2.hashCode());
    }
}