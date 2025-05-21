package com.jiraclone.backend.controller;

import com.jiraclone.backend.dto.ReportDTO;
import com.jiraclone.backend.model.Report;
import com.jiraclone.backend.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    void setUp() {
        // Remove MockitoAnnotations.openMocks(this) as @ExtendWith(MockitoExtension.class) handles it
        //when(userDetails.getUsername()).thenReturn("test@example.com");
    }

    @Test
    void generateReport_ShouldReturnGeneratedReport() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("test@example.com"); // Move the stubbing here

        Map<String, Object> request = new HashMap<>();
        request.put("projectId", 1L);
        request.put("prompt", "Test prompt");
        request.put("type", Report.ReportType.STATUS_REPORT.toString());

        ReportDTO expectedReport = new ReportDTO();
        expectedReport.setId(1L);

        when(reportService.generateReport(
                eq(1L),
                eq("test@example.com"),
                eq("Test prompt"),
                eq(Report.ReportType.STATUS_REPORT)
        )).thenReturn(expectedReport);

        // Act
        ResponseEntity<ReportDTO> response = reportController.generateReport(request, userDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedReport, response.getBody());
    }

    @Test
    void getReportsByProject_ShouldReturnReportList() {
        // Arrange
        Long projectId = 1L;
        List<ReportDTO> expectedReports = Arrays.asList(new ReportDTO(), new ReportDTO());
        when(reportService.getReportsByProject(projectId)).thenReturn(expectedReports);

        // Act
        ResponseEntity<List<ReportDTO>> response = reportController.getReportsByProject(projectId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedReports, response.getBody());
    }

    @Test
    void getReportById_ShouldReturnReport() {
        // Arrange
        Long reportId = 1L;
        ReportDTO expectedReport = new ReportDTO();
        when(reportService.getReportById(reportId)).thenReturn(expectedReport);

        // Act
        ResponseEntity<ReportDTO> response = reportController.getReportById(reportId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedReport, response.getBody());
    }

    @Test
    void deleteReport_ShouldReturnNoContent() {
        // Arrange
        Long reportId = 1L;

        // Act
        ResponseEntity<?> response = reportController.deleteReport(reportId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reportService).deleteReport(reportId);
    }
}