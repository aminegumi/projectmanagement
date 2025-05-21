package com.jiraclone.backend.dto;

import com.jiraclone.backend.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest extends BaseDTOTest {

    @Test
    void fromEntity_ShouldMapAllFields() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setRole(User.Role.MEMBER);

        // Act
        UserDTO dto = UserDTO.fromEntity(user);

        // Assert
        assertEquals(1L, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("john@example.com", dto.getEmail());
        assertEquals(User.Role.MEMBER, dto.getRole());
    }

    @Test
    void fromEntity_WithNullUser_ShouldReturnNull() {
        assertNull(UserDTO.fromEntity(null));
    }
}