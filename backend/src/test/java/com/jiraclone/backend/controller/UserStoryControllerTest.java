package com.jiraclone.backend.controller;

import com.jiraclone.backend.dto.UserStoryDTO;
import com.jiraclone.backend.model.UserStory;
import com.jiraclone.backend.service.UserStoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;

class UserStoryControllerTest {

    @Mock
    private UserStoryService userStoryService;

    @InjectMocks
    private UserStoryController userStoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUserStory_ShouldReturnCreatedStory() {
        // Arrange
        UserStory story = new UserStory();
        Long projectId = 1L;
        Long epicId = 2L;
        UserStoryDTO expectedDto = new UserStoryDTO();
        expectedDto.setId(1L);
        when(userStoryService.createUserStory(any(UserStory.class), eq(projectId), eq(epicId)))
                .thenReturn(expectedDto);

        // Act
        ResponseEntity<UserStoryDTO> response = userStoryController.createUserStory(story, projectId, epicId);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        assertEquals("/user-stories/" + expectedDto.getId(),
                response.getHeaders().getLocation().getPath());
    }

    @Test
    void getUserStoryById_ShouldReturnUserStory() {
        // Arrange
        Long storyId = 1L;
        UserStoryDTO expectedDto = new UserStoryDTO();
        expectedDto.setId(storyId);
        when(userStoryService.getUserStoryById(storyId)).thenReturn(expectedDto);

        // Act
        ResponseEntity<UserStoryDTO> response = userStoryController.getUserStoryById(storyId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void getUserStoriesByProject_ShouldReturnStoryList() {
        // Arrange
        Long projectId = 1L;
        List<UserStoryDTO> expectedDtos = Arrays.asList(new UserStoryDTO(), new UserStoryDTO());
        when(userStoryService.getUserStoriesByProject(projectId)).thenReturn(expectedDtos);

        // Act
        ResponseEntity<List<UserStoryDTO>> response = userStoryController.getUserStoriesByProject(projectId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDtos, response.getBody());
    }

    @Test
    void getUserStoriesByEpic_ShouldReturnStoryList() {
        // Arrange
        Long epicId = 1L;
        List<UserStoryDTO> expectedDtos = Arrays.asList(new UserStoryDTO(), new UserStoryDTO());
        when(userStoryService.getUserStoriesByEpic(epicId)).thenReturn(expectedDtos);

        // Act
        ResponseEntity<List<UserStoryDTO>> response = userStoryController.getUserStoriesByEpic(epicId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDtos, response.getBody());
    }

    @Test
    void updateUserStory_ShouldReturnUpdatedStory() {
        // Arrange
        Long storyId = 1L;
        UserStory story = new UserStory();
        UserStoryDTO expectedDto = new UserStoryDTO();
        when(userStoryService.updateUserStory(eq(storyId), any(UserStory.class))).thenReturn(expectedDto);

        // Act
        ResponseEntity<UserStoryDTO> response = userStoryController.updateUserStory(storyId, story);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void deleteUserStory_ShouldReturnNoContent() {
        // Arrange
        Long storyId = 1L;

        // Act
        ResponseEntity<?> response = userStoryController.deleteUserStory(storyId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userStoryService).deleteUserStory(storyId);
    }
}