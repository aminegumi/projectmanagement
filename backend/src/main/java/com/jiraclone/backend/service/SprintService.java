package com.jiraclone.backend.service;

import com.jiraclone.backend.dto.SprintDTO;
import com.jiraclone.backend.model.Project;
import com.jiraclone.backend.model.Sprint;
import com.jiraclone.backend.repository.ProjectRepository;
import com.jiraclone.backend.repository.SprintRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public SprintDTO createSprint(Sprint sprint, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));

        sprint.setProject(project);
        sprint.setStatus(Sprint.Status.PLANNING);
        Sprint savedSprint = sprintRepository.save(sprint);
        return SprintDTO.fromEntity(savedSprint);
    }

    public SprintDTO getSprintById(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sprint not found with ID: " + id));
        return SprintDTO.fromEntity(sprint);
    }

    public List<SprintDTO> getSprintsByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));
        
        return sprintRepository.findByProjectOrderByStartDateDesc(project).stream()
                .map(SprintDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public SprintDTO updateSprint(Long id, Sprint sprintDetails) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sprint not found with ID: " + id));

        sprint.setName(sprintDetails.getName());
        sprint.setGoal(sprintDetails.getGoal());
        sprint.setStartDate(sprintDetails.getStartDate());
        sprint.setEndDate(sprintDetails.getEndDate());
        sprint.setStatus(sprintDetails.getStatus());

        Sprint updatedSprint = sprintRepository.save(sprint);
        return SprintDTO.fromEntity(updatedSprint);
    }

    @Transactional
    public void deleteSprint(Long id) {
        if (!sprintRepository.existsById(id)) {
            throw new EntityNotFoundException("Sprint not found with ID: " + id);
        }
        sprintRepository.deleteById(id);
    }

    @Transactional
    public SprintDTO startSprint(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sprint not found with ID: " + id));

        sprint.setStatus(Sprint.Status.ACTIVE);
        Sprint updatedSprint = sprintRepository.save(sprint);
        return SprintDTO.fromEntity(updatedSprint);
    }

    @Transactional
    public SprintDTO completeSprint(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sprint not found with ID: " + id));

        sprint.setStatus(Sprint.Status.COMPLETED);
        Sprint updatedSprint = sprintRepository.save(sprint);
        return SprintDTO.fromEntity(updatedSprint);
    }
}