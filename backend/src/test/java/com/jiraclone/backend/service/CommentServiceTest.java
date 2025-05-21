package com.jiraclone.backend.service;

import com.jiraclone.backend.model.Comment;
import com.jiraclone.backend.model.Task;
import com.jiraclone.backend.model.User;
import com.jiraclone.backend.repository.CommentRepository;
import com.jiraclone.backend.repository.TaskRepository;
import com.jiraclone.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateComment_Success() {
        Long taskId = 1L;
        String authorEmail = "user@example.com";

        Task task = new Task();
        task.setId(taskId);

        User user = new User();
        user.setEmail(authorEmail);

        Comment comment = new Comment();
        comment.setText("This is a comment");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(authorEmail)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));

        Comment result = commentService.createComment(comment, taskId, authorEmail);

        assertEquals("This is a comment", result.getText());
        assertEquals(user, result.getAuthor());
        assertEquals(task, result.getTask());
    }

    @Test
    void testCreateComment_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> commentService.createComment(new Comment(), 1L, "user@example.com"));
    }

    @Test
    void testCreateComment_UserNotFound() {
        Task task = new Task(); task.setId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commentService.createComment(new Comment(), 1L, "user@example.com"));
    }

    @Test
    void testGetCommentsByTask_Success() {
        Task task = new Task(); task.setId(1L);

        Comment c1 = new Comment(); c1.setText("Comment 1");
        Comment c2 = new Comment(); c2.setText("Comment 2");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(commentRepository.findByTaskOrderByCreatedAtDesc(task)).thenReturn(List.of(c1, c2));

        List<Comment> comments = commentService.getCommentsByTask(1L);

        assertEquals(2, comments.size());
        assertEquals("Comment 1", comments.get(0).getText());
    }

    @Test
    void testGetCommentsByTask_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> commentService.getCommentsByTask(1L));
    }

    @Test
    void testUpdateComment_Success() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Old text");

        User author = new User();
        author.setEmail("user@example.com");
        comment.setAuthor(author);

        Comment updatedDetails = new Comment();
        updatedDetails.setText("Updated text");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(author));
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));

        Comment result = commentService.updateComment(1L, updatedDetails, "user@example.com");

        assertEquals("Updated text", result.getText());
    }


@Test
void testUpdateComment_Unauthorized() {
    // Arrange
    Comment existingComment = new Comment();
    existingComment.setId(1L);
    
    User realAuthor = new User();
    realAuthor.setEmail("real@example.com");
    existingComment.setAuthor(realAuthor);
    
    Comment updatedComment = new Comment();
    updatedComment.setText("Updated text");
    
    when(commentRepository.findById(1L)).thenReturn(Optional.of(existingComment));
    when(userRepository.findByEmail("hacker@example.com")).thenReturn(Optional.of(new User()));

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> {
        commentService.updateComment(1L, updatedComment, "hackerr@example.com");
    }, "Only the author can update their comment");
}

@Test
void testDeleteComment_Unauthorized() {
    // Arrange
    Comment existingComment = new Comment();
    existingComment.setId(1L);
    
    User realAuthor = new User();
    realAuthor.setEmail("owner@example.com");
    existingComment.setAuthor(realAuthor);
    
    when(commentRepository.findById(1L)).thenReturn(Optional.of(existingComment));
    when(userRepository.findByEmail("notme@example.com")).thenReturn(Optional.of(new User()));

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> {
        commentService.deleteComment(1L, "notmee@example.com");
    }, "Only the author can delete their comment");
}
}
