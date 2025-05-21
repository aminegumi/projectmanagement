package com.jiraclone.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    
    private User user;
    
    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testUserCreation() {
        // Arrange
        Long id = 1L;
        String name = "John Doe";
        String email = "john@example.com";
        String password = "password123";
        User.Role role = User.Role.MEMBER;
        LocalDateTime now = LocalDateTime.now();

        // Act
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // Assert
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(role, user.getRole());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
        assertNotNull(user.getProjects());
        assertNotNull(user.getAssignedTasks());
        assertNotNull(user.getReportedTasks());
        assertNotNull(user.getComments());
    }

    @Test
    void testUserCollections() {
        // Arrange
        Project project = new Project();
        Task assignedTask = new Task();
        Task reportedTask = new Task();
        Comment comment = new Comment();

        project.setId(1L);
        assignedTask.setId(1L);
        reportedTask.setId(2L);
        comment.setId(1L);

        // Act
        user.getProjects().add(project);
        user.getAssignedTasks().add(assignedTask);
        user.getReportedTasks().add(reportedTask);
        user.getComments().add(comment);

        // Assert
        assertEquals(1, user.getProjects().size());
        assertEquals(1, user.getAssignedTasks().size());
        assertEquals(1, user.getReportedTasks().size());
        assertEquals(1, user.getComments().size());
        assertTrue(user.getProjects().contains(project));
        assertTrue(user.getAssignedTasks().contains(assignedTask));
        assertTrue(user.getReportedTasks().contains(reportedTask));
        assertTrue(user.getComments().contains(comment));
    }

    @Test
    void testUserRoles() {
        // Assert
        assertArrayEquals(new User.Role[]{
            User.Role.MEMBER,
            User.Role.PRODUCT_OWNER,
            User.Role.SCRUM_MASTER
        }, User.Role.values());
    }

    @Test
    void testUserEqualsAndHashCode() {
        // Arrange
        User user1 = new User();
        User user2 = new User();
        
        user1.setId(1L);
        user1.setEmail("john@example.com");
        user2.setId(1L);
        user2.setEmail("john@example.com");

        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setEmail("jane@example.com");

        // Assert
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1, differentUser);
        assertNotEquals(user1.hashCode(), differentUser.hashCode());
    }

    @Test
    void testUserToString() {
        // Arrange
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setRole(User.Role.MEMBER);

        // Act
        String toString = user.toString();

        // Assert
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=John Doe"));
        assertTrue(toString.contains("email=john@example.com"));
        assertTrue(toString.contains("role=MEMBER"));
    }
}