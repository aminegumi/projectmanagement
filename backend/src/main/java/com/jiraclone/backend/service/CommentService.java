package com.jiraclone.backend.service;


import com.jiraclone.backend.model.Comment;
import com.jiraclone.backend.model.Task;
import com.jiraclone.backend.model.User;
import com.jiraclone.backend.repository.CommentRepository;
import com.jiraclone.backend.repository.TaskRepository;
import com.jiraclone.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional
    public Comment createComment(Comment comment, Long taskId, String authorEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));
        
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + authorEmail));

        comment.setTask(task);
        comment.setAuthor(author);
        
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));
        
        return commentRepository.findByTaskOrderByCreatedAtDesc(task);
    }

    @Transactional
    public Comment updateComment(Long id, Comment commentDetails, String userEmail) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + id));
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail));

        // Only the author can edit the comment
        if (!comment.getAuthor().equals(user)) {
            throw new IllegalArgumentException("You are not authorized to edit this comment");
        }

        comment.setText(commentDetails.getText());
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long id, String userEmail) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + id));
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail));

        // Only the author can delete the comment
        if (!comment.getAuthor().equals(user)) {
            throw new IllegalArgumentException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }
}