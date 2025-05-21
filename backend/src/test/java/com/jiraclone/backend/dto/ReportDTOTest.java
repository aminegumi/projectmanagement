package com.jiraclone.backend.dto;

import com.jiraclone.backend.model.Report;
import com.jiraclone.backend.model.Project;
import com.jiraclone.backend.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReportDTOTest extends BaseDTOTest {

    @Test
    void fromEntity_ShouldMapAllFields() {
        // Arrange
        Report report = new Report();
        report.setId(1L);
        report.setTitle("Test Report");
        report.setContent("Report Content");
        report.setPrompt("Report Prompt");
        report.setType(Report.ReportType.STATUS_REPORT);
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        report.setProject(project);

        User author = new User();
        author.setId(1L);
        author.setName("Report Author");
        report.setAuthor(author);

        // Act
        ReportDTO dto = ReportDTO.fromEntity(report);

        // Assert
        assertEquals(1L, dto.getId());
        assertEquals("Test Report", dto.getTitle());
        assertEquals("Report Content", dto.getContent());
        assertEquals("Report Prompt", dto.getPrompt());
        assertEquals(Report.ReportType.STATUS_REPORT, dto.getType());
        assertEquals(1L, dto.getProjectId());
        assertEquals("Test Project", dto.getProjectName());
        assertEquals(1L, dto.getAuthorId());
        assertEquals("Report Author", dto.getAuthorName());
    }
}