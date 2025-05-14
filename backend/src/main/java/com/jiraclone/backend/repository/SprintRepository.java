package com.jiraclone.backend.repository;

import com.jiraclone.backend.model.Project;
import com.jiraclone.backend.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {
    List<Sprint> findByProjectOrderByStartDateDesc(Project project);
}