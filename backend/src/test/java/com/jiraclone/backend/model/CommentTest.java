package com.jiraclone.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CommentTest {
    
    private Comment comment;
    private Task task;
    private User author;
    
    @BeforeEach
    void setUp() {
        comment = new Comment();
        task = new Task();
        author = new User();
        
        task.setId(1L);
        author.setId(1L);
    }

    @Test
    void testCommentCreation() {
        // Arrange
        Long id = 1L;
        String text = "Test comment";
        LocalDateTime now = LocalDateTime.now();

        // Act
        comment.setId(id);
        comment.setText(text);
        comment.setTask(task);
        comment.setAuthor(author);
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);

        // Assert
        assertEquals(id, comment.getId());
        assertEquals(text, comment.getText());
        assertEquals(task, comment.getTask());
        assertEquals(author, comment.getAuthor());
        assertEquals(now, comment.getCreatedAt());
        assertEquals(now, comment.getUpdatedAt());
    }

    @Test
    void testCommentEqualsAndHashCode() {
        // Arrange
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        Comment differentComment = new Comment();

        comment1.setId(1L);
        comment2.setId(1L);
        differentComment.setId(2L);

        // Assert
        assertEquals(comment1, comment2, "Comments with same ID should be equal");
        assertNotEquals(comment1, differentComment, "Comments with different IDs should not be equal");
        assertEquals(comment1.hashCode(), comment2.hashCode(), "Hash codes should be equal for same ID");
    }

    @Test
    void testCommentToString() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");

        // Act
        String toString = comment.toString();

        // Assert
        assertTrue(toString.contains("id=1"), "ToString should contain id");
        assertTrue(toString.contains("text=Test comment"), "ToString should contain text");
    }
}