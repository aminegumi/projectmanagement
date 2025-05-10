package com.scrum.projectmanagement.controller;

import com.scrum.projectmanagement.dto.ReportDTO;
import com.scrum.projectmanagement.model.Report;
import com.scrum.projectmanagement.service.ReportService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports") 
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ReportDTO>> getReportsByProject(@PathVariable Long projectId) {
        List<ReportDTO> reports = reportService.getReportsByProject(projectId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDTO> getReportById(@PathVariable Long reportId) {
        ReportDTO report = reportService.getReportById(reportId);
        return ResponseEntity.ok(report);
    }

    // In your generateReport method:

@PostMapping("/generate")
public ResponseEntity<ReportDTO> generateReport(
        @RequestBody Map<String, Object> request,
        @AuthenticationPrincipal UserDetails userDetails) {
    
    try {
        System.out.println("Report generation request received: " + request);
        
        Long projectId = Long.valueOf(request.get("projectId").toString());
        String prompt = (String) request.get("prompt");
        String reportType = (String) request.get("type");
        
        System.out.println("Project ID: " + projectId);
        System.out.println("Prompt: " + prompt);
        System.out.println("Report Type: " + reportType);
        System.out.println("User: " + userDetails.getUsername());
        
        Report.ReportType type;
        try {
            type = Report.ReportType.valueOf(reportType);
        } catch (IllegalArgumentException e) {
            type = Report.ReportType.CUSTOM;
        }
        
        ReportDTO generatedReport = reportService.generateReport(
            projectId, 
            userDetails.getUsername(), 
            prompt, 
            type
        );
        
        System.out.println("Report generated successfully with ID: " + generatedReport.getId());
        
        return ResponseEntity.ok(generatedReport);
    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Error generating report: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
    }
}


@DeleteMapping("/{reportId}")
    public ResponseEntity<?> deleteReport(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }
}