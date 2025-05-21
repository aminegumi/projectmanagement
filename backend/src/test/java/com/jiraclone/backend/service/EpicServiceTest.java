package com.jiraclone.backend.service;

import com.jiraclone.backend.dto.EpicDTO;
import com.jiraclone.backend.model.Epic;
import com.jiraclone.backend.model.Project;
import com.jiraclone.backend.repository.EpicRepository;
import com.jiraclone.backend.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EpicServiceTest {

    @Mock private EpicRepository epicRepository;
    @Mock private ProjectRepository projectRepository;

    @InjectMocks private EpicService epicService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateEpic_Success() {
        Long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);

        Epic epic = new Epic();
        epic.setName("Epic A");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(epicRepository.save(any(Epic.class))).thenAnswer(i -> i.getArgument(0));

        EpicDTO result = epicService.createEpic(epic, projectId);

        assertEquals("Epic A", result.getName());
        verify(epicRepository).save(any(Epic.class));
    }

    @Test
    void testCreateEpic_ProjectNotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            epicService.createEpic(new Epic(), 99L));
    }

    @Test
    void testGetEpicById_Success() {
        Epic epic = new Epic();
        epic.setId(1L);
        epic.setName("Feature 1");

        when(epicRepository.findById(1L)).thenReturn(Optional.of(epic));

        EpicDTO result = epicService.getEpicById(1L);

        assertEquals("Feature 1", result.getName());
    }

    @Test
    void testGetEpicById_NotFound() {
        when(epicRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> epicService.getEpicById(1L));
    }

    @Test
    void testGetEpicsByProject_Success() {
        Project project = new Project();
        project.setId(1L);

        Epic epic1 = new Epic(); epic1.setName("Epic One");
        Epic epic2 = new Epic(); epic2.setName("Epic Two");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(epicRepository.findByProjectOrderByCreatedAtDesc(project))
                .thenReturn(List.of(epic1, epic2));

        List<EpicDTO> result = epicService.getEpicsByProject(1L);

        assertEquals(2, result.size());
        assertEquals("Epic One", result.get(0).getName());
    }

    @Test
    void testGetEpicsByProject_ProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> epicService.getEpicsByProject(1L));
    }

    @Test
    void testUpdateEpic_Success() {
        Epic existing = new Epic();
        existing.setId(1L);
        existing.setName("Old Name");

        Epic updatedDetails = new Epic();
        updatedDetails.setName("New Name");
        updatedDetails.setSummary("Updated Summary");
        updatedDetails.setStatus(Epic.Status.IN_PROGRESS);

        when(epicRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(epicRepository.save(any(Epic.class))).thenAnswer(i -> i.getArgument(0));

        EpicDTO result = epicService.updateEpic(1L, updatedDetails);

        assertEquals("New Name", result.getName());
        assertEquals("Updated Summary", result.getSummary());
        assertEquals(Epic.Status.IN_PROGRESS, result.getStatus());
    }

    @Test
    void testUpdateEpic_NotFound() {
        when(epicRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> epicService.updateEpic(1L, new Epic()));
    }

    @Test
    void testDeleteEpic_Success() {
        when(epicRepository.existsById(1L)).thenReturn(true);
        epicService.deleteEpic(1L);
        verify(epicRepository).deleteById(1L);
    }

    @Test
    void testDeleteEpic_NotFound() {
        when(epicRepository.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> epicService.deleteEpic(1L));
    }
}
