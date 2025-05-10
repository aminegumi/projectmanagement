package com.scrum.projectmanagement.repository;

import com.scrum.projectmanagement.model.Project;
import com.scrum.projectmanagement.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {
    List<Sprint> findByProjectOrderByStartDateDesc(Project project);
}