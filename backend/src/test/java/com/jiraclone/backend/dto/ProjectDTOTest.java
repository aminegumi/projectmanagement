package com.jiraclone.backend.dto;

import com.jiraclone.backend.model.Project;
import com.jiraclone.backend.model.Task;
import com.jiraclone.backend.model.User;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ProjectDTOTest extends BaseDTOTest {

    @Test
    void fromEntity_ShouldMapAllFields() {
        // Arrange
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setKey("TP");
        project.setDescription("Description");
        project.setCreatedAt(now);

        User lead = new User();
        lead.setId(1L);
        lead.setName("Project Lead");
        project.setLead(lead);

        Set<Task> tasks = new HashSet<>();
        Task task = new Task();
        task.setStatus(Task.Status.DONE);
        tasks.add(task);
        project.setTasks(tasks);

        // Act
        ProjectDTO dto = ProjectDTO.fromEntity(project);

        // Assert
        assertEquals(1L, dto.getId());
        assertEquals("Test Project", dto.getName());
        assertEquals("TP", dto.getKey());
        assertEquals("Description", dto.getDescription());
        assertEquals(now, dto.getCreatedAt());
        assertNotNull(dto.getLead());
        assertEquals(1, dto.getTasksCount());
        assertEquals(100, dto.getCompletedPercentage());
    }

    @Test
    void fromEntity_WithNoTasks_ShouldHaveZeroCompletion() {
        Project project = new Project();
        project.setTasks(new HashSet<>());

        ProjectDTO dto = ProjectDTO.fromEntity(project);

        assertEquals(0, dto.getTasksCount());
        assertEquals(0, dto.getCompletedPercentage());
    }
}