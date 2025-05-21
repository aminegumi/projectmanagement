package com.jiraclone.backend.service;



import com.jiraclone.backend.dto.UserDTO;
import com.jiraclone.backend.model.User;
import com.jiraclone.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserById_UserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getName(), result.getName());
    }

    @Test
    void testGetUserById_UserNotFound() {
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
    }

   @Test
void testUpdateUser_UserExists_UpdateNameAndPassword() {
    Long userId = 1L;

    
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setName("Old Name");
    existingUser.setPassword("oldpass");

    
    User updatedDetails = new User();
    updatedDetails.setName("New Name");
    updatedDetails.setPassword("newpass");

    
    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(passwordEncoder.encode("newpass")).thenReturn("hashedNewPass");

    User savedUser = new User();
    savedUser.setId(userId);
    savedUser.setName("New Name");
    savedUser.setPassword("hashedNewPass");

    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    
    UserDTO result = userService.updateUser(userId, updatedDetails);

    
    assertEquals("New Name", result.getName());
    
    verify(userRepository).save(existingUser);
}

@Test
void testUpdateUser_UserNotFound() {
    Long userId = 99L;
    User updatedDetails = new User();
    updatedDetails.setName("Whatever");

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> userService.updateUser(userId, updatedDetails));
}
@Test
void testGetAllUsers_ReturnsList() {
    User user1 = new User();
    user1.setId(1L);
    user1.setEmail("user1@example.com");
    user1.setName("User One");

    User user2 = new User();
    user2.setId(2L);
    user2.setEmail("user2@example.com");
    user2.setName("User Two");

    List<User> users = List.of(user1, user2);

    when(userRepository.findAll()).thenReturn(users);

    List<UserDTO> result = userService.getAllUsers();

    assertEquals(2, result.size());
    assertEquals("user1@example.com", result.get(0).getEmail());
    assertEquals("user2@example.com", result.get(1).getEmail());
}


@Test
void testDeleteUser_UserExists() {
    Long userId = 1L;

    when(userRepository.existsById(userId)).thenReturn(true);

    userService.deleteUser(userId);

    verify(userRepository).deleteById(userId);
}

@Test
void testDeleteUser_UserNotFound() {
    Long userId = 999L;

    when(userRepository.existsById(userId)).thenReturn(false);

    assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(userId));
}




}
