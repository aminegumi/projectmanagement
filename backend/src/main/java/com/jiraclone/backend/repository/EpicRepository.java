package com.jiraclone.backend.repository;

import com.jiraclone.backend.model.Epic;
import com.jiraclone.backend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpicRepository extends JpaRepository<Epic, Long> {
    List<Epic> findByProjectOrderByCreatedAtDesc(Project project);
}