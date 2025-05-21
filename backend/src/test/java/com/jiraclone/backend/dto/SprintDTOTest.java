package com.jiraclone.backend.dto;

import com.jiraclone.backend.model.Sprint;
import com.jiraclone.backend.model.Project;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class SprintDTOTest extends BaseDTOTest {

    @Test
    void fromEntity_ShouldMapAllFields() {
        // Arrange
        Sprint sprint = new Sprint();
        sprint.setId(1L);
        sprint.setName("Sprint 1");
        sprint.setGoal("Sprint Goal");
        sprint.setStartDate(LocalDate.now());
        sprint.setEndDate(LocalDate.now().plusWeeks(2));
        sprint.setStatus(Sprint.Status.ACTIVE);
        sprint.setCreatedAt(now);

        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        sprint.setProject(project);

        // Act
        SprintDTO dto = SprintDTO.fromEntity(sprint);

        // Assert
        assertEquals(1L, dto.getId());
        assertEquals("Sprint 1", dto.getName());
        assertEquals("Sprint Goal", dto.getGoal());
        assertEquals(Sprint.Status.ACTIVE, dto.getStatus());
        assertEquals(1L, dto.getProjectId());
        assertEquals("Test Project", dto.getProjectName());
        assertNotNull(dto.getStartDate());
        assertNotNull(dto.getEndDate());
    }
}