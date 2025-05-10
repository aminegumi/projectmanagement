package com.scrum.projectmanagement.repository;

import com.scrum.projectmanagement.model.Comment;
import com.scrum.projectmanagement.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskOrderByCreatedAtDesc(Task task);
}