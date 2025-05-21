package com.jiraclone.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;
    private UserDetails userDetails;
    private final String SECRET_KEY = "testsecretkeytestsecretkeytestsecretkeytestsecretkey";
    private final long EXPIRATION = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtTokenUtil = new JwtTokenUtil();
        ReflectionTestUtils.setField(jwtTokenUtil, "secret", SECRET_KEY);
        ReflectionTestUtils.setField(jwtTokenUtil, "expiration", EXPIRATION);

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Act
        String token = jwtTokenUtil.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = jwtTokenUtil.generateToken(userDetails);

        // Act
        boolean isValid = jwtTokenUtil.validateToken(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Arrange
        String token = jwtTokenUtil.generateToken(userDetails);

        // Act
        String username = jwtTokenUtil.extractUsername(token);

        // Assert
        assertEquals("test@example.com", username);
    }
}