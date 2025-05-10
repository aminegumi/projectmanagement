package com.scrum.projectmanagement.dto;

import com.jiraclone.backend.model.Sprint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SprintDTO {
    private Long id;
    private String name;
    private String goal;
    private LocalDate startDate;
    private LocalDate endDate;
    private Sprint.Status status;
    private Long projectId;
    private String projectName;
    private LocalDateTime createdAt;
    private Set<TaskDTO> tasks = new HashSet<>();

    public static SprintDTO fromEntity(Sprint sprint) {
        SprintDTO dto = new SprintDTO();
        dto.setId(sprint.getId());
        dto.setName(sprint.getName());
        dto.setGoal(sprint.getGoal());
        dto.setStartDate(sprint.getStartDate());
        dto.setEndDate(sprint.getEndDate());
        dto.setStatus(sprint.getStatus());
        dto.setCreatedAt(sprint.getCreatedAt());
        
        if (sprint.getProject() != null) {
            dto.setProjectId(sprint.getProject().getId());
            dto.setProjectName(sprint.getProject().getName());
        }
        
        if (sprint.getTasks() != null) {
            dto.setTasks(sprint.getTasks().stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }
}