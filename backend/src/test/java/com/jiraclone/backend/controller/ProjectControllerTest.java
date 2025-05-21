package com.jiraclone.backend.controller;

import com.jiraclone.backend.dto.ProjectDTO;
import com.jiraclone.backend.dto.UserDTO;
import com.jiraclone.backend.model.Project;
import com.jiraclone.backend.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;

class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private ProjectController projectController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(userDetails.getUsername()).thenReturn("test@example.com");
    }

    @Test
    void createProject_ShouldReturnCreatedProject() {
        // Arrange
        Project project = new Project();
        ProjectDTO expectedDto = new ProjectDTO();
        expectedDto.setId(1L);
        when(projectService.createProject(any(Project.class), eq("test@example.com")))
                .thenReturn(expectedDto);

        // Act
        ResponseEntity<ProjectDTO> response = projectController.createProject(project, userDetails);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void getProjectById_ShouldReturnProject() {
        // Arrange
        Long projectId = 1L;
        ProjectDTO expectedDto = new ProjectDTO();
        when(projectService.getProjectById(projectId)).thenReturn(expectedDto);

        // Act
        ResponseEntity<ProjectDTO> response = projectController.getProjectById(projectId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void getProjectMembers_ShouldReturnMembersList() {
        // Arrange
        Long projectId = 1L;
        List<UserDTO> expectedMembers = Arrays.asList(new UserDTO(), new UserDTO());
        when(projectService.getProjectMembers(projectId)).thenReturn(expectedMembers);

        // Act
        ResponseEntity<?> response = projectController.getProjectMembers(projectId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMembers, response.getBody());
    }

    @Test
    void changeProjectLead_ShouldReturnUpdatedProject() {
        // Arrange
        Long projectId = 1L;
        Long newLeadId = 2L;
        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("leadId", newLeadId);
        ProjectDTO expectedDto = new ProjectDTO();
        when(projectService.changeProjectLead(projectId, newLeadId)).thenReturn(expectedDto);

        // Act
        ResponseEntity<ProjectDTO> response = projectController.changeProjectLead(projectId, requestBody);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }
}