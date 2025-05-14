package com.jiraclone.backend.dto;

import com.jiraclone.backend.model.Epic;
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
public class EpicDTO {
    private Long id;
    private String name;
    private String summary;
    private Epic.Status status;
    private Long projectId;
    private String projectName;
    private LocalDateTime createdAt;
    private Set<UserStoryDTO> stories = new HashSet<>();

    public static EpicDTO fromEntity(Epic epic) {
        EpicDTO dto = new EpicDTO();
        dto.setId(epic.getId());
        dto.setName(epic.getName());
        dto.setSummary(epic.getSummary());
        dto.setStatus(epic.getStatus());
        dto.setCreatedAt(epic.getCreatedAt());
        
        if (epic.getProject() != null) {
            dto.setProjectId(epic.getProject().getId());
            dto.setProjectName(epic.getProject().getName());
        }
        
        if (epic.getStories() != null) {
            dto.setStories(epic.getStories().stream()
                .map(UserStoryDTO::fromEntity)
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }
}
