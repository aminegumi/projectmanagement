package com.jiraclone.backend.mapper;

import com.jiraclone.backend.dto.CommentDTO;
import com.jiraclone.backend.model.Comment;
import com.jiraclone.backend.model.Task;
import com.jiraclone.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    private CommentMapper commentMapper;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        commentMapper = new CommentMapper();
        now = LocalDateTime.now();
    }

    @Test
    void toDTO_ShouldMapAllFields() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);

        Task task = new Task();
        task.setId(2L);
        comment.setTask(task);

        User author = new User();
        author.setId(3L);
        author.setName("John Doe");
        author.setEmail("john@example.com");
        comment.setAuthor(author);

        // Act
        CommentDTO dto = commentMapper.toDTO(comment);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test comment", dto.getText());
        assertEquals(2L, dto.getTaskId());
        assertEquals(3L, dto.getAuthorId());
        assertEquals("John Doe", dto.getAuthorName());
        assertEquals("john@example.com", dto.getAuthorEmail());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void toDTO_WithNullValues_ShouldHandleGracefully() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");

        // Act
        CommentDTO dto = commentMapper.toDTO(comment);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test comment", dto.getText());
        assertNull(dto.getTaskId());
        assertNull(dto.getAuthorId());
        assertNull(dto.getAuthorName());
        assertNull(dto.getAuthorEmail());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }

    @Test
    void toEntity_ShouldMapBasicFields() {
        // Arrange
        CommentDTO dto = new CommentDTO();
        dto.setId(1L);
        dto.setText("Test comment");
        dto.setTaskId(2L);
        dto.setAuthorId(3L);
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        // Act
        Comment entity = commentMapper.toEntity(dto);

        // Assert
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("Test comment", entity.getText());
        // Note: Task and Author are not mapped in toEntity method
        assertNull(entity.getTask());
        assertNull(entity.getAuthor());
    }

    @Test
    void toEntity_WithNullDTO_ShouldReturnNewCommentInstance() {
        // Act
        Comment entity = commentMapper.toEntity(null);

        // Assert
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getText());
    }
}