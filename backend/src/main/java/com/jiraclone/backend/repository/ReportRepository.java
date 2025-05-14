package com.jiraclone.backend.repository;

import com.jiraclone.backend.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByProjectIdOrderByCreatedAtDesc(Long projectId);
    List<Report> findByAuthorIdOrderByCreatedAtDesc(Long userId);
    List<Report> findByTypeOrderByCreatedAtDesc(Report.ReportType type);
}