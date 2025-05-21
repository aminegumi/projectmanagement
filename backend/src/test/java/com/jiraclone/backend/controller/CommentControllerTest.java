package com.jiraclone.backend.controller;

import com.jiraclone.backend.dto.CommentDTO;
import com.jiraclone.backend.mapper.CommentMapper;
import com.jiraclone.backend.model.Comment;
import com.jiraclone.backend.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;

class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(userDetails.getUsername()).thenReturn("test@example.com");
    }

    @Test
    void createComment_ShouldReturnCreatedComment() {
        // Arrange
        CommentDTO commentDTO = new CommentDTO();
        Comment comment = new Comment();
        Comment createdComment = new Comment();
        createdComment.setId(1L); // Set the ID on the created comment

        CommentDTO expectedDTO = new CommentDTO();
        expectedDTO.setId(1L);

        when(commentMapper.toEntity(commentDTO)).thenReturn(comment);
        when(commentService.createComment(comment, 1L, "test@example.com")).thenReturn(createdComment);
        when(commentMapper.toDTO(createdComment)).thenReturn(expectedDTO);

        // Act
        ResponseEntity<CommentDTO> response = commentController.createComment(commentDTO, 1L, userDetails);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedDTO, response.getBody());
        assertEquals("/comments/1", response.getHeaders().getLocation().getPath());
    }

    @Test
    void getCommentsByTask_ShouldReturnCommentList() {
        // Arrange
        Long taskId = 1L;
        List<Comment> comments = Arrays.asList(new Comment(), new Comment());
        List<CommentDTO> expectedDTOs = Arrays.asList(new CommentDTO(), new CommentDTO());

        when(commentService.getCommentsByTask(taskId)).thenReturn(comments);
        when(commentMapper.toDTO(any(Comment.class))).thenReturn(new CommentDTO());

        // Act
        ResponseEntity<List<CommentDTO>> response = commentController.getCommentsByTask(taskId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void updateComment_ShouldReturnUpdatedComment() {
        // Arrange
        Long commentId = 1L;
        CommentDTO commentDTO = new CommentDTO();
        Comment comment = new Comment();
        Comment updatedComment = new Comment();
        CommentDTO expectedDTO = new CommentDTO();

        when(commentMapper.toEntity(commentDTO)).thenReturn(comment);
        when(commentService.updateComment(eq(commentId), any(Comment.class), eq("test@example.com")))
                .thenReturn(updatedComment);
        when(commentMapper.toDTO(updatedComment)).thenReturn(expectedDTO);

        // Act
        ResponseEntity<CommentDTO> response = commentController.updateComment(commentId, commentDTO, userDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDTO, response.getBody());
    }

    @Test
    void deleteComment_ShouldReturnNoContent() {
        // Arrange
        Long commentId = 1L;

        // Act
        ResponseEntity<?> response = commentController.deleteComment(commentId, userDetails);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(commentService).deleteComment(commentId, "test@example.com");
    }
}