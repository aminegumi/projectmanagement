package com.jiraclone.backend.controller;

import com.jiraclone.backend.dto.SprintDTO;
import com.jiraclone.backend.model.Sprint;
import com.jiraclone.backend.service.SprintService;
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

class SprintControllerTest {

    @Mock
    private SprintService sprintService;

    @InjectMocks
    private SprintController sprintController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSprint_ShouldReturnCreatedSprint() {
        // Arrange
        Sprint sprint = new Sprint();
        Long projectId = 1L;
        SprintDTO expectedDto = new SprintDTO();
        expectedDto.setId(1L);
        when(sprintService.createSprint(any(Sprint.class), eq(projectId)))
                .thenReturn(expectedDto);

        // Act
        ResponseEntity<SprintDTO> response = sprintController.createSprint(sprint, projectId);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        assertEquals("/sprints/" + expectedDto.getId(),
                response.getHeaders().getLocation().getPath());
    }

    @Test
    void getSprintById_ShouldReturnSprint() {
        // Arrange
        Long sprintId = 1L;
        SprintDTO expectedDto = new SprintDTO();
        when(sprintService.getSprintById(sprintId)).thenReturn(expectedDto);

        // Act
        ResponseEntity<SprintDTO> response = sprintController.getSprintById(sprintId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void startSprint_ShouldReturnUpdatedSprint() {
        // Arrange
        Long sprintId = 1L;
        SprintDTO expectedDto = new SprintDTO();
        when(sprintService.startSprint(sprintId)).thenReturn(expectedDto);

        // Act
        ResponseEntity<SprintDTO> response = sprintController.startSprint(sprintId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void completeSprint_ShouldReturnUpdatedSprint() {
        // Arrange
        Long sprintId = 1L;
        SprintDTO expectedDto = new SprintDTO();
        when(sprintService.completeSprint(sprintId)).thenReturn(expectedDto);

        // Act
        ResponseEntity<SprintDTO> response = sprintController.completeSprint(sprintId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }
}