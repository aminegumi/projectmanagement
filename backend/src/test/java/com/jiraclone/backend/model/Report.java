package com.jiraclone.backend.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 10000)
    private String content;

    private String prompt;

    @Enumerated(EnumType.STRING)
    private ReportType type;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Define report types
    public enum ReportType {
        STATUS_REPORT,
        SPRINT_ANALYSIS,
        TEAM_PERFORMANCE,
        RISK_ASSESSMENT,
        CUSTOM
    }
}