package com.jiraclone.backend.dto;

import com.jiraclone.backend.model.Epic;
import com.jiraclone.backend.model.Project;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicDTOTest extends BaseDTOTest {

    @Test
    void fromEntity_ShouldMapAllFields() {
        // Arrange
        Epic epic = new Epic();
        epic.setId(1L);
        epic.setName("Test Epic");
        epic.setSummary("Epic Summary");
        epic.setStatus(Epic.Status.IN_PROGRESS);
        epic.setCreatedAt(now);

        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        epic.setProject(project);

        // Act
        EpicDTO dto = EpicDTO.fromEntity(epic);

        // Assert
        assertEquals(1L, dto.getId());
        assertEquals("Test Epic", dto.getName());
        assertEquals("Epic Summary", dto.getSummary());
        assertEquals(Epic.Status.IN_PROGRESS, dto.getStatus());
        assertEquals(1L, dto.getProjectId());
        assertEquals("Test Project", dto.getProjectName());
        assertEquals(now, dto.getCreatedAt());
    }
}