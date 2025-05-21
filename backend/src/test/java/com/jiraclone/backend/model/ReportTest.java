package com.jiraclone.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ReportTest {
    
    private Report report;
    private Project project;
    private User author;
    
    @BeforeEach
    void setUp() {
        report = new Report();
        project = new Project();
        author = new User();
        
        project.setId(1L);
        author.setId(1L);
    }

    @Test
    void testReportCreation() {
        // Arrange
        Long id = 1L;
        String title = "Sprint Review";
        String content = "Sprint review content";
        String prompt = "Generate sprint review";
        Report.ReportType type = Report.ReportType.SPRINT_ANALYSIS;
        LocalDateTime now = LocalDateTime.now();

        // Act
        report.setId(id);
        report.setTitle(title);
        report.setContent(content);
        report.setPrompt(prompt);
        report.setType(type);
        report.setProject(project);
        report.setAuthor(author);
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        // Assert
        assertEquals(id, report.getId());
        assertEquals(title, report.getTitle());
        assertEquals(content, report.getContent());
        assertEquals(prompt, report.getPrompt());
        assertEquals(type, report.getType());
        assertEquals(project, report.getProject());
        assertEquals(author, report.getAuthor());
        assertEquals(now, report.getCreatedAt());
        assertEquals(now, report.getUpdatedAt());
    }

    @Test
    void testReportType() {
        // Assert
        assertArrayEquals(new Report.ReportType[]{
            Report.ReportType.STATUS_REPORT,
            Report.ReportType.SPRINT_ANALYSIS,
            Report.ReportType.TEAM_PERFORMANCE,
            Report.ReportType.RISK_ASSESSMENT,
            Report.ReportType.CUSTOM
        }, Report.ReportType.values());
    }
}