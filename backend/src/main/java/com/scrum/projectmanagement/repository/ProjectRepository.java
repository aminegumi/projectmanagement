package com.scrum.projectmanagement.repository;

import com.scrum.projectmanagement.model.Project;
import com.scrum.projectmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByMembersContaining(User user);
    Optional<Project> findByKey(String key);
    boolean existsByKey(String key);
    
    @Query("SELECT p FROM Project p WHERE p.lead = :user OR :user MEMBER OF p.members")
    List<Project> findByLeadOrMembers(User user);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.members WHERE p.id = :projectId")
    Optional<Project> findByIdWithMembers(@Param("projectId") Long projectId);

}