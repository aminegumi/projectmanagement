package com.scrum.projectmanagement.service;

import com.scrum.projectmanagement.dto.ProjectDTO;
import com.scrum.projectmanagement.dto.UserDTO;
import com.scrum.projectmanagement.model.Project;
import com.scrum.projectmanagement.model.User;
import com.scrum.projectmanagement.repository.ProjectRepository;
import com.scrum.projectmanagement.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectDTO createProject(Project project, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail));

        if (projectRepository.existsByKey(project.getKey())) {
            throw new IllegalArgumentException("Project key already exists");
        }

        project.setLead(user);
        project.getMembers().add(user);
        Project savedProject = projectRepository.save(project);
        return ProjectDTO.fromEntity(savedProject);
    }

    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + id));
        return ProjectDTO.fromEntity(project);
    }

    public List<ProjectDTO> getProjectsByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail));

        return projectRepository.findByLeadOrMembers(user).stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectDTO updateProject(Long id, Project projectDetails) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + id));

        project.setName(projectDetails.getName());
        project.setDescription(projectDetails.getDescription());
        
        // Don't update the key if it's already in use by another project
        if (!project.getKey().equals(projectDetails.getKey()) && 
            projectRepository.existsByKey(projectDetails.getKey())) {
            throw new IllegalArgumentException("Project key already exists");
        }
        project.setKey(projectDetails.getKey());

        Project updatedProject = projectRepository.save(project);
        return ProjectDTO.fromEntity(updatedProject);
    }

    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("Project not found with ID: " + id);
        }
        projectRepository.deleteById(id);
    }

    @Transactional
    public ProjectDTO addMemberToProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        if (project.getMembers().contains(user)) {
            throw new IllegalArgumentException("User is already a member of this project");
        }

        project.getMembers().add(user);
        Project updatedProject = projectRepository.save(project);
        return ProjectDTO.fromEntity(updatedProject);
    }

    @Transactional
    public ProjectDTO removeMemberFromProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        if (!project.getMembers().contains(user)) {
            throw new IllegalArgumentException("User is not a member of this project");
        }

        // Prevent removing the project lead
        if (project.getLead().equals(user)) {
            throw new IllegalArgumentException("Cannot remove the project lead");
        }

        project.getMembers().remove(user);
        Project updatedProject = projectRepository.save(project);
        return ProjectDTO.fromEntity(updatedProject);
    }
    

    @Transactional
    public ProjectDTO changeProjectLead(Long projectId, Long newLeadId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));

        User newLead = userRepository.findById(newLeadId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + newLeadId));

        if (!project.getMembers().contains(newLead)) {
            project.getMembers().add(newLead);
        }
        
        project.setLead(newLead);
        Project updatedProject = projectRepository.save(project);
        return ProjectDTO.fromEntity(updatedProject);
    }
    
    // Add this method to your ProjectService class

    @Transactional(readOnly = true)
public List<UserDTO> getProjectMembers(Long projectId) {
    // Use direct SQL query instead of relying on JPA relationships
    List<UserDTO> membersList = new java.util.ArrayList<>();
    
    // Check if the project exists
    Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));
    
    // First add the project lead
    if (project.getLead() != null) {
        UserDTO leadDTO = new UserDTO();
        leadDTO.setId(project.getLead().getId());
        leadDTO.setName(project.getLead().getName());
        leadDTO.setEmail(project.getLead().getEmail());
        leadDTO.setRole(User.Role.PRODUCT_OWNER);
        membersList.add(leadDTO);
        System.out.println("Added lead to response: " + project.getLead().getId() + " - " + project.getLead().getName());
    }
    
    // Get all members from database using direct SQL
    jakarta.persistence.Query query = entityManager.createNativeQuery(
        "SELECT u.id, u.name, u.email, u.role " +
        "FROM users u " +
        "JOIN project_members pm ON u.id = pm.user_id " +
        "WHERE pm.project_id = :projectId");
    query.setParameter("projectId", projectId);
    
    List<Object[]> results = query.getResultList();
    
    // Process SQL results
    for (Object[] row : results) {
        Long memberId = ((Number) row[0]).longValue();
        
        // Skip the lead since we already added them
        if (project.getLead() != null && memberId.equals(project.getLead().getId())) {
            continue;
        }
        
        String name = (String) row[1];
        String email = (String) row[2];
        String role = (String) row[3];
        
        UserDTO memberDTO = new UserDTO();
        memberDTO.setId(memberId);
        memberDTO.setName(name);
        memberDTO.setEmail(email);
        
        // Parse role or use MEMBER as default
        try {
            memberDTO.setRole(role != null ? User.Role.valueOf(role) : User.Role.MEMBER);
        } catch (IllegalArgumentException e) {
            memberDTO.setRole(User.Role.MEMBER);
        }
        
        membersList.add(memberDTO);
        System.out.println("Added member to response: " + memberId + " - " + name);
    }
    
    System.out.println("Returning " + membersList.size() + " members total");
    return membersList;
}
    @Transactional
public void updateMemberRole(Long projectId, Long userId, String roleName) {
    Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));
    
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    
    // Ensure the user is a member of the project
    boolean isMember = project.getMembers().contains(user);
    boolean isLead = project.getLead() != null && project.getLead().getId().equals(user.getId());
    
    if (!isMember && !isLead) {
        throw new IllegalArgumentException("User is not associated with this project");
    }
    
    try {
        // Update the user's role
        User.Role newRole = User.Role.valueOf(roleName);
        user.setRole(newRole);
        userRepository.save(user);
        
        // If changing to PRODUCT_OWNER, update the project lead
        if (newRole == User.Role.PRODUCT_OWNER && !isLead) {
            project.setLead(user);
            projectRepository.save(project);
        }
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid role: " + roleName);
    }
}

private final jakarta.persistence.EntityManager entityManager;
// Add this debug method
public int countMembersByNativeQuery(Long projectId) {
    // Direct SQL query to count members
    jakarta.persistence.Query query = entityManager.createNativeQuery(
        "SELECT COUNT(*) FROM project_members WHERE project_id = :projectId");
    query.setParameter("projectId", projectId);
    
    // Get the result as a Number
    Number count = (Number) query.getSingleResult();
    System.out.println("SQL direct count of members for project " + projectId + ": " + count);
    return count.intValue();
}

public List<UserDTO> getProjectMembersDirectSQL(Long projectId) {
    // Check if project exists
    projectRepository.findById(projectId)
            .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));
            
    // Use direct SQL to get all members
    jakarta.persistence.Query query = entityManager.createNativeQuery(
        "SELECT u.id, u.name, u.email, u.role " +
        "FROM users u " +
        "JOIN project_members pm ON u.id = pm.user_id " +
        "WHERE pm.project_id = :projectId");
    query.setParameter("projectId", projectId);
    
    List<Object[]> results = query.getResultList();
    List<UserDTO> members = new java.util.ArrayList<>();
    
    for (Object[] row : results) {
        UserDTO dto = new UserDTO();
        dto.setId(((Number) row[0]).longValue());
        dto.setName((String) row[1]);
        dto.setEmail((String) row[2]);
        
        String role = (String) row[3];
        try {
            dto.setRole(role != null ? User.Role.valueOf(role) : User.Role.MEMBER);
        } catch (IllegalArgumentException e) {
            dto.setRole(User.Role.MEMBER);
        }
        
        members.add(dto);
    }
    
    return members;
}
    
}