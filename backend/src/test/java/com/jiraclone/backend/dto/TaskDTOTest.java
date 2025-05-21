package com.jiraclone.backend.dto;

import com.jiraclone.backend.model.Task;
import com.jiraclone.backend.model.Project;
import com.jiraclone.backend.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskDTOTest extends BaseDTOTest {

    @Test
    void fromEntity_ShouldMapAllFields() {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setTaskKey("TP-1");
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setType(Task.Type.BUG);
        task.setPriority(Task.Priority.HIGH);
        task.setStatus(Task.Status.IN_PROGRESS);
        task.setEstimatedHours(8);
        task.setLoggedHours(4);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        task.setProject(project);

        User assignee = new User();
        assignee.setId(1L);
        assignee.setName("Assignee");
        task.setAssignee(assignee);

        // Act
        TaskDTO dto = TaskDTO.fromEntity(task);

        // Assert
        assertEquals(1L, dto.getId());
        assertEquals("TP-1", dto.getTaskKey());
        assertEquals("Test Task", dto.getTitle());
        assertEquals("Description", dto.getDescription());
        assertEquals(Task.Type.BUG, dto.getType());
        assertEquals(Task.Priority.HIGH, dto.getPriority());
        assertEquals(Task.Status.IN_PROGRESS, dto.getStatus());
        assertEquals(8, dto.getEstimatedHours());
        assertEquals(4, dto.getLoggedHours());
        assertEquals(1L, dto.getProjectId());
        assertEquals("Test Project", dto.getProjectName());
        assertNotNull(dto.getAssignee());
    }

    @Test
    void fromEntity_WithNullRelations_ShouldHandleGracefully() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");

        TaskDTO dto = TaskDTO.fromEntity(task);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test Task", dto.getTitle());
        assertNull(dto.getProjectId());
        assertNull(dto.getAssignee());
    }
}