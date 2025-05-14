package com.jiraclone.backend.repository;

import com.jiraclone.backend.model.Epic;
import com.jiraclone.backend.model.Project;
import com.jiraclone.backend.model.UserStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStoryRepository extends JpaRepository<UserStory, Long> {
    List<UserStory> findByProjectOrderByCreatedAtDesc(Project project);
    List<UserStory> findByEpicOrderByCreatedAtDesc(Epic epic);
}