package com.scrum.projectmanagement.dto;

import com.scrum.projectmanagement.model.Report;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Long id;
    private String title;
    private String content;
    private String prompt;
    private Report.ReportType type;
    private Long projectId;
    private String projectName;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReportDTO fromEntity(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        dto.setTitle(report.getTitle());
        dto.setContent(report.getContent());
        dto.setPrompt(report.getPrompt());
        dto.setType(report.getType());

        if (report.getProject() != null) {
            dto.setProjectId(report.getProject().getId());
            dto.setProjectName(report.getProject().getName());
        }

        if (report.getAuthor() != null) {
            dto.setAuthorId(report.getAuthor().getId());
            dto.setAuthorName(report.getAuthor().getName());
        }

        dto.setCreatedAt(report.getCreatedAt());
        dto.setUpdatedAt(report.getUpdatedAt());

        return dto;
    }
}