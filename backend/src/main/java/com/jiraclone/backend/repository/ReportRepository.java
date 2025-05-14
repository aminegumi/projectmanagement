package com.scrum.projectmanagement.repository;

import com.scrum.projectmanagement.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByProjectIdOrderByCreatedAtDesc(Long projectId);
    List<Report> findByAuthorIdOrderByCreatedAtDesc(Long userId);
    List<Report> findByTypeOrderByCreatedAtDesc(Report.ReportType type);
}