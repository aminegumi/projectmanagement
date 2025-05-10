package com.scrum.projectmanagement.dto;

import com.scrum.projectmanagement.model.Project;
import com.scrum.projectmanagement.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.scrum.projectmanagement.model.User; // Add this import for the User class

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;
    private String name;
    private String key;
    private String description;
    private UserDTO lead;
    private LocalDateTime createdAt;
    private Set<UserDTO> members = new HashSet<>();
    private int tasksCount;
    private int completedPercentage;

    public static ProjectDTO fromEntity(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setKey(project.getKey());
        dto.setDescription(project.getDescription());
        dto.setCreatedAt(project.getCreatedAt());
        
        // Set lead
        if (project.getLead() != null) {
            UserDTO leadDTO = new UserDTO();
            leadDTO.setId(project.getLead().getId());
            leadDTO.setName(project.getLead().getName());
            leadDTO.setEmail(project.getLead().getEmail());
            leadDTO.setRole(project.getLead().getRole());
            dto.setLead(leadDTO);
        }
        
        // Set members
        Set<UserDTO> memberDTOs = new HashSet<>();
        if (project.getMembers() != null && !project.getMembers().isEmpty()) {
            for (User member : project.getMembers()) {
                UserDTO memberDTO = UserDTO.fromEntity(member);
                memberDTOs.add(memberDTO);
            }
            dto.setMembers(memberDTOs);
        }
        
        // Calculate statistics
        if (project.getTasks() != null) {
            dto.setTasksCount(project.getTasks().size());
            
            long completedTasks = project.getTasks().stream()
                .filter(task -> task.getStatus() == Task.Status.DONE)
                .count();
            
            int completedPercentage = project.getTasks().isEmpty() ? 0 : 
                (int) ((completedTasks * 100) / project.getTasks().size());
            
            dto.setCompletedPercentage(completedPercentage);
        }
        
        return dto;
    }
    
}