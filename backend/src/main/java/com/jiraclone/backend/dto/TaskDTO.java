package com.scrum.projectmanagement.dto;


import com.scrum.projectmanagement.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long id;
    private String taskKey;
    private String title;
    private String description;
    private Task.Type type;
    private Task.Priority priority;
    private Task.Status status;
    private LocalDate dueDate;
    private Integer estimatedHours;
    private Integer loggedHours;
    private Long projectId;
    private String projectName;
    private Long sprintId;
    private Long userStoryId;
    private UserDTO assignee;
    private UserDTO reporter;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskDTO fromEntity(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTaskKey(task.getTaskKey());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setType(task.getType());
        dto.setPriority(task.getPriority());
        dto.setStatus(task.getStatus());
        dto.setDueDate(task.getDueDate());
        dto.setEstimatedHours(task.getEstimatedHours());
        dto.setLoggedHours(task.getLoggedHours());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        
        if (task.getProject() != null) {
            dto.setProjectId(task.getProject().getId());
            dto.setProjectName(task.getProject().getName());
        }
        
        if (task.getSprint() != null) {
            dto.setSprintId(task.getSprint().getId());
        }
        
        if (task.getUserStory() != null) {
            dto.setUserStoryId(task.getUserStory().getId());
        }
        
        if (task.getAssignee() != null) {
            dto.setAssignee(UserDTO.fromEntity(task.getAssignee()));
        }
        
        if (task.getReporter() != null) {
            dto.setReporter(UserDTO.fromEntity(task.getReporter()));
        }
        
        return dto;
    }
}