package com.scrum.projectmanagement.repository;

import com.scrum.projectmanagement.model.Epic;
import com.scrum.projectmanagement.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpicRepository extends JpaRepository<Epic, Long> {
    List<Epic> findByProjectOrderByCreatedAtDesc(Project project);
}