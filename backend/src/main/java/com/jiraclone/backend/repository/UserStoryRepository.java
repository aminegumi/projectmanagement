package com.scrum.projectmanagement.repository;

import com.scrum.projectmanagement.model.Epic;
import com.scrum.projectmanagement.model.Project;
import com.scrum.projectmanagement.model.UserStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStoryRepository extends JpaRepository<UserStory, Long> {
    List<UserStory> findByProjectOrderByCreatedAtDesc(Project project);
    List<UserStory> findByEpicOrderByCreatedAtDesc(Epic epic);
}