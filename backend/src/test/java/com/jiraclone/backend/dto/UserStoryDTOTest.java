package com.jiraclone.backend.dto;

import com.jiraclone.backend.model.UserStory;
import com.jiraclone.backend.model.Project;
import com.jiraclone.backend.model.Epic;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserStoryDTOTest extends BaseDTOTest {

    @Test
    void fromEntity_ShouldMapAllFields() {
        // Arrange
        UserStory story = new UserStory();
        story.setId(1L);
        story.setTitle("User Story");
        story.setDescription("Description");
        story.setStoryPoints(5);
        story.setStatus(UserStory.Status.IN_PROGRESS);
        story.setCreatedAt(now);

        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        story.setProject(project);

        Epic epic = new Epic();
        epic.setId(1L);
        epic.setName("Test Epic");
        story.setEpic(epic);

        // Act
        UserStoryDTO dto = UserStoryDTO.fromEntity(story);

        // Assert
        assertEquals(1L, dto.getId());
        assertEquals("User Story", dto.getTitle());
        assertEquals("Description", dto.getDescription());
        assertEquals(5, dto.getStoryPoints());
        assertEquals(UserStory.Status.IN_PROGRESS, dto.getStatus());
        assertEquals(1L, dto.getProjectId());
        assertEquals("Test Project", dto.getProjectName());
        assertEquals(1L, dto.getEpicId());
        assertEquals("Test Epic", dto.getEpicName());
    }
}