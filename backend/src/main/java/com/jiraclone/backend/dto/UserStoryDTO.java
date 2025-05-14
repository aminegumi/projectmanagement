package com.jiraclone.backend.dto;

import com.jiraclone.backend.model.UserStory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStoryDTO {
    private Long id;
    private String title;
    private String description;
    private Integer storyPoints;
    private UserStory.Status status;
    private Long projectId;
    private String projectName;
    private Long epicId;
    private String epicName;
    private LocalDateTime createdAt;
    private Set<TaskDTO> tasks = new HashSet<>();

    public static UserStoryDTO fromEntity(UserStory story) {
        UserStoryDTO dto = new UserStoryDTO();
        dto.setId(story.getId());
        dto.setTitle(story.getTitle());
        dto.setDescription(story.getDescription());
        dto.setStoryPoints(story.getStoryPoints());
        dto.setStatus(story.getStatus());
        dto.setCreatedAt(story.getCreatedAt());
        
        if (story.getProject() != null) {
            dto.setProjectId(story.getProject().getId());
            dto.setProjectName(story.getProject().getName());
        }
        
        if (story.getEpic() != null) {
            dto.setEpicId(story.getEpic().getId());
            dto.setEpicName(story.getEpic().getName());
        }
        
        if (story.getTasks() != null) {
            dto.setTasks(story.getTasks().stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }
}