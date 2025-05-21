package com.jiraclone.backend.service;

import com.jiraclone.backend.dto.ProjectDTO;
import com.jiraclone.backend.model.Project;
import com.jiraclone.backend.model.User;
import com.jiraclone.backend.repository.ProjectRepository;
import com.jiraclone.backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private UserRepository userRepository;
    @Mock private EntityManager entityManager;

    @InjectMocks private ProjectService projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProject_Success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("lead@example.com");

        Project project = new Project();
        project.setKey("PRJ");
        project.setMembers(new HashSet<>());

        when(userRepository.findByEmail("lead@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.existsByKey("PRJ")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        ProjectDTO result = projectService.createProject(project, "lead@example.com");

        assertEquals("PRJ", result.getKey());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testCreateProject_UserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
            () -> projectService.createProject(new Project(), "unknown@example.com"));
    }

    @Test
    void testCreateProject_KeyAlreadyExists() {
        User user = new User(); user.setEmail("lead@example.com");
        Project project = new Project(); project.setKey("EXISTING");

        when(userRepository.findByEmail("lead@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.existsByKey("EXISTING")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
            () -> projectService.createProject(project, "lead@example.com"));
    }

    @Test
    void testGetProjectById_Success() {
        Project project = new Project();
        project.setId(1L);
        project.setKey("PRJ");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        ProjectDTO result = projectService.getProjectById(1L);

        assertEquals("PRJ", result.getKey());
    }

    @Test
    void testGetProjectById_NotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(99L));
    }

    @Test
    void testDeleteProject_Success() {
        when(projectRepository.existsById(1L)).thenReturn(true);
        projectService.deleteProject(1L);
        verify(projectRepository).deleteById(1L);
    }

    @Test
    void testDeleteProject_NotFound() {
        when(projectRepository.existsById(2L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> projectService.deleteProject(2L));
    }

    @Test
    void testCountMembersByNativeQuery() {
        Query mockQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("projectId"), any())).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(5);

        int count = projectService.countMembersByNativeQuery(1L);
        assertEquals(5, count);
    }
}