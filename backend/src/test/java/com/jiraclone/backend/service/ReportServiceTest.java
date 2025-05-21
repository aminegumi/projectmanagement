package com.jiraclone.backend.service;

import com.jiraclone.backend.dto.ReportDTO;
import com.jiraclone.backend.model.*;
import com.jiraclone.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Mock private ReportRepository reportRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private UserRepository userRepository;
    @Mock private TaskRepository taskRepository;

    @InjectMocks private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetReportsByProject_ReturnsList() {
        Report report1 = new Report();
        report1.setTitle("Report 1");

        when(reportRepository.findByProjectIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(report1));

        List<ReportDTO> result = reportService.getReportsByProject(1L);

        assertEquals(1, result.size());
        assertEquals("Report 1", result.get(0).getTitle());
    }

    @Test
    void testGetReportById_Success() {
        Report report = new Report();
        report.setId(1L);
        report.setTitle("Test Report");

        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

        ReportDTO result = reportService.getReportById(1L);
        assertEquals("Test Report", result.getTitle());
    }

    @Test
    void testGetReportById_NotFound() {
        when(reportRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> reportService.getReportById(1L));
    }

    @Test
    void testDeleteReport_Success() {
        when(reportRepository.existsById(1L)).thenReturn(true);
        reportService.deleteReport(1L);
        verify(reportRepository).deleteById(1L);
    }

    @Test
    void testDeleteReport_NotFound() {
        when(reportRepository.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> reportService.deleteReport(1L));
    }

    @Test
    void testGenerateReport_WithDummyContent() {
       
        Project project = new Project();
        project.setId(1L);
        project.setName("MyProject");
        project.setKey("MP");
        project.setDescription("Sample project");
        project.setMembers(new HashSet<>());
        User lead = new User(); lead.setName("Lead User");
        project.setLead(lead);

        User user = new User();
        user.setEmail("user@example.com");
        user.setName("User");

        
        Task t1 = new Task(); t1.setStatus(Task.Status.TODO);
        Task t2 = new Task(); t2.setStatus(Task.Status.DONE);
        Task t3 = new Task(); t3.setStatus(Task.Status.IN_PROGRESS);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(taskRepository.findByProjectId(1L)).thenReturn(List.of(t1, t2, t3));
        when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArgument(0));

        ReportDTO result = reportService.generateReport(
                1L,
                "user@example.com",
                "Génère un rapport de statut",
                Report.ReportType.STATUS_REPORT
        );

        assertTrue(result.getContent().contains("Status Report"));
        assertEquals("MyProject - Status report", result.getTitle());
    }
}
