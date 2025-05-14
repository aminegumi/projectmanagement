package com.scrum.projectmanagement.service;

import com.scrum.projectmanagement.dto.EpicDTO;
import com.scrum.projectmanagement.model.Epic;
import com.scrum.projectmanagement.model.Project;
import com.scrum.projectmanagement.repository.EpicRepository;
import com.scrum.projectmanagement.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EpicService {

    private final EpicRepository epicRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public EpicDTO createEpic(Epic epic, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));

        epic.setProject(project);
        epic.setStatus(Epic.Status.TODO);
        Epic savedEpic = epicRepository.save(epic);
        return EpicDTO.fromEntity(savedEpic);
    }

    public EpicDTO getEpicById(Long id) {
        Epic epic = epicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Epic not found with ID: " + id));
        return EpicDTO.fromEntity(epic);
    }

    public List<EpicDTO> getEpicsByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));
        
        return epicRepository.findByProjectOrderByCreatedAtDesc(project).stream()
                .map(EpicDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public EpicDTO updateEpic(Long id, Epic epicDetails) {
        Epic epic = epicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Epic not found with ID: " + id));

        epic.setName(epicDetails.getName());
        epic.setSummary(epicDetails.getSummary());
        epic.setStatus(epicDetails.getStatus());

        Epic updatedEpic = epicRepository.save(epic);
        return EpicDTO.fromEntity(updatedEpic);
    }

    @Transactional
    public void deleteEpic(Long id) {
        if (!epicRepository.existsById(id)) {
            throw new EntityNotFoundException("Epic not found with ID: " + id);
        }
        epicRepository.deleteById(id);
    }
}