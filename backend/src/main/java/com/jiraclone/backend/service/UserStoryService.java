package com.scrum.projectmanagement.service;

import com.scrum.projectmanagement.dto.UserStoryDTO;
import com.scrum.projectmanagement.model.Epic;
import com.scrum.projectmanagement.model.Project;
import com.scrum.projectmanagement.model.UserStory;
import com.scrum.projectmanagement.repository.EpicRepository;
import com.scrum.projectmanagement.repository.ProjectRepository;
import com.scrum.projectmanagement.repository.UserStoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserStoryService {

    private final UserStoryRepository userStoryRepository;
    private final ProjectRepository projectRepository;
    private final EpicRepository epicRepository;

    @Transactional
    public UserStoryDTO createUserStory(UserStory story, Long projectId, Long epicId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));

        story.setProject(project);
        story.setStatus(UserStory.Status.TODO);

        if (epicId != null) {
            Epic epic = epicRepository.findById(epicId)
                    .orElseThrow(() -> new EntityNotFoundException("Epic not found with ID: " + epicId));
            story.setEpic(epic);
        }

        UserStory savedStory = userStoryRepository.save(story);
        return UserStoryDTO.fromEntity(savedStory);
    }

    public UserStoryDTO getUserStoryById(Long id) {
        UserStory story = userStoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User Story not found with ID: " + id));
        return UserStoryDTO.fromEntity(story);
    }

    public List<UserStoryDTO> getUserStoriesByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));
        
        return userStoryRepository.findByProjectOrderByCreatedAtDesc(project).stream()
                .map(UserStoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<UserStoryDTO> getUserStoriesByEpic(Long epicId) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new EntityNotFoundException("Epic not found with ID: " + epicId));
        
        return userStoryRepository.findByEpicOrderByCreatedAtDesc(epic).stream()
                .map(UserStoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserStoryDTO updateUserStory(Long id, UserStory storyDetails) {
        UserStory story = userStoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User Story not found with ID: " + id));

        story.setTitle(storyDetails.getTitle());
        story.setDescription(storyDetails.getDescription());
        story.setStoryPoints(storyDetails.getStoryPoints());
        story.setStatus(storyDetails.getStatus());

        UserStory updatedStory = userStoryRepository.save(story);
        return UserStoryDTO.fromEntity(updatedStory);
    }

    @Transactional
    public void deleteUserStory(Long id) {
        if (!userStoryRepository.existsById(id)) {
            throw new EntityNotFoundException("User Story not found with ID: " + id);
        }
        userStoryRepository.deleteById(id);
    }
}