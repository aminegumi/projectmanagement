package com.jiraclone.backend.repository;


import com.jiraclone.backend.model.Project;
import com.jiraclone.backend.model.Task;
import com.jiraclone.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectOrderByCreatedAtDesc(Project project);
    List<Task> findByAssigneeOrderByCreatedAtDesc(User assignee);
    Optional<Task> findByProjectAndTaskKey(Project project, String taskKey);
    List<Task> findByAssigneeAndStatus(User assignee, Task.Status status);
    List<Task> findBySprintIdOrderByCreatedAtDesc(Long sprintId);
    List<Task> findByProjectId(Long projectId);
}