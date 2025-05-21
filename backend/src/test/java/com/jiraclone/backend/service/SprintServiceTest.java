package com.jiraclone.backend.service;

import com.jiraclone.backend.dto.SprintDTO;
import com.jiraclone.backend.model.Project;
import com.jiraclone.backend.model.Sprint;
import com.jiraclone.backend.repository.ProjectRepository;
import com.jiraclone.backend.repository.SprintRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SprintServiceTest {

    @Mock private SprintRepository sprintRepository;
    @Mock private ProjectRepository projectRepository;

    @InjectMocks private SprintService sprintService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSprint_Success() {
        Project project = new Project();
        project.setId(1L);

        Sprint sprint = new Sprint();
        sprint.setName("Sprint 1");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(sprintRepository.save(any(Sprint.class))).thenAnswer(i -> i.getArgument(0));

        SprintDTO result = sprintService.createSprint(sprint, 1L);

        assertEquals("Sprint 1", result.getName());
        verify(sprintRepository).save(any(Sprint.class));
    }

    @Test
    void testCreateSprint_ProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
            sprintService.createSprint(new Sprint(), 1L));
    }

    @Test
    void testGetSprintById_Success() {
        Sprint sprint = new Sprint();
        sprint.setId(1L);
        sprint.setName("Test Sprint");

        when(sprintRepository.findById(1L)).thenReturn(Optional.of(sprint));

        SprintDTO result = sprintService.getSprintById(1L);
        assertEquals("Test Sprint", result.getName());
    }

    @Test
    void testGetSprintById_NotFound() {
        when(sprintRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> sprintService.getSprintById(1L));
    }

    @Test
    void testGetSprintsByProject_Success() {
        Project project = new Project();
        project.setId(1L);

        Sprint sprint1 = new Sprint();
        sprint1.setName("Sprint A");
        sprint1.setStartDate(LocalDate.now());

        Sprint sprint2 = new Sprint();
        sprint2.setName("Sprint B");
        sprint2.setStartDate(LocalDate.now().minusDays(10));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(sprintRepository.findByProjectOrderByStartDateDesc(project)).thenReturn(List.of(sprint1, sprint2));

        List<SprintDTO> result = sprintService.getSprintsByProject(1L);

        assertEquals(2, result.size());
        assertEquals("Sprint A", result.get(0).getName());
    }

    @Test
    void testGetSprintsByProject_ProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> sprintService.getSprintsByProject(1L));
    }

    @Test
    void testUpdateSprint_Success() {
        Sprint existing = new Sprint();
        existing.setId(1L);
        existing.setName("Old");
        existing.setStatus(Sprint.Status.PLANNING);

        Sprint update = new Sprint();
        update.setName("Updated");
        update.setGoal("Finish it");
        update.setStartDate(LocalDate.now());
        update.setEndDate(LocalDate.now().plusDays(7));
        update.setStatus(Sprint.Status.ACTIVE);

        when(sprintRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(sprintRepository.save(any(Sprint.class))).thenAnswer(i -> i.getArgument(0));

        SprintDTO result = sprintService.updateSprint(1L, update);

        assertEquals("Updated", result.getName());
        assertEquals(Sprint.Status.ACTIVE, result.getStatus());
    }

    @Test
    void testUpdateSprint_NotFound() {
        when(sprintRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> sprintService.updateSprint(1L, new Sprint()));
    }

    @Test
    void testDeleteSprint_Success() {
        when(sprintRepository.existsById(1L)).thenReturn(true);

        sprintService.deleteSprint(1L);

        verify(sprintRepository).deleteById(1L);
    }

    @Test
    void testDeleteSprint_NotFound() {
        when(sprintRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> sprintService.deleteSprint(1L));
    }

    @Test
    void testStartSprint_Success() {
        Sprint sprint = new Sprint();
        sprint.setId(1L);
        sprint.setStatus(Sprint.Status.PLANNING);

        when(sprintRepository.findById(1L)).thenReturn(Optional.of(sprint));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(sprint);

        SprintDTO result = sprintService.startSprint(1L);

        assertEquals(Sprint.Status.ACTIVE, result.getStatus());
    }

    @Test
    void testCompleteSprint_Success() {
        Sprint sprint = new Sprint();
        sprint.setId(1L);
        sprint.setStatus(Sprint.Status.ACTIVE);

        when(sprintRepository.findById(1L)).thenReturn(Optional.of(sprint));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(sprint);

        SprintDTO result = sprintService.completeSprint(1L);

        assertEquals(Sprint.Status.COMPLETED, result.getStatus());
    }

    @Test
    void testStartSprint_NotFound() {
        when(sprintRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> sprintService.startSprint(1L));
    }

    @Test
    void testCompleteSprint_NotFound() {
        when(sprintRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> sprintService.completeSprint(1L));
    }
}
