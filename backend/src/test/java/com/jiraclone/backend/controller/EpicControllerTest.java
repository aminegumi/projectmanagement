package com.jiraclone.backend.controller;

import com.jiraclone.backend.dto.EpicDTO;
import com.jiraclone.backend.model.Epic;
import com.jiraclone.backend.service.EpicService;
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

class EpicControllerTest {

    @Mock
    private EpicService epicService;

    @InjectMocks
    private EpicController epicController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEpic_ShouldReturnCreatedEpic() {
        // Arrange
        Epic epic = new Epic();
        Long projectId = 1L;
        EpicDTO expectedDto = new EpicDTO();
        expectedDto.setId(1L);
        when(epicService.createEpic(any(Epic.class), eq(projectId))).thenReturn(expectedDto);

        // Act
        ResponseEntity<EpicDTO> response = epicController.createEpic(epic, projectId);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        assertEquals("/epics/" + expectedDto.getId(), response.getHeaders().getLocation().getPath());
    }

    @Test
    void getEpicById_ShouldReturnEpic() {
        // Arrange
        Long epicId = 1L;
        EpicDTO expectedDto = new EpicDTO();
        expectedDto.setId(epicId);
        when(epicService.getEpicById(epicId)).thenReturn(expectedDto);

        // Act
        ResponseEntity<EpicDTO> response = epicController.getEpicById(epicId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void getEpicsByProject_ShouldReturnEpicList() {
        // Arrange
        Long projectId = 1L;
        List<EpicDTO> expectedDtos = Arrays.asList(new EpicDTO(), new EpicDTO());
        when(epicService.getEpicsByProject(projectId)).thenReturn(expectedDtos);

        // Act
        ResponseEntity<List<EpicDTO>> response = epicController.getEpicsByProject(projectId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDtos, response.getBody());
    }

    @Test
    void updateEpic_ShouldReturnUpdatedEpic() {
        // Arrange
        Long epicId = 1L;
        Epic epic = new Epic();
        EpicDTO expectedDto = new EpicDTO();
        when(epicService.updateEpic(eq(epicId), any(Epic.class))).thenReturn(expectedDto);

        // Act
        ResponseEntity<EpicDTO> response = epicController.updateEpic(epicId, epic);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void deleteEpic_ShouldReturnNoContent() {
        // Arrange
        Long epicId = 1L;

        // Act
        ResponseEntity<?> response = epicController.deleteEpic(epicId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(epicService).deleteEpic(epicId);
    }
}