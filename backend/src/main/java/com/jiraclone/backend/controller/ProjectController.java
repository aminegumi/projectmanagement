package com.jiraclone.backend.controller;

import com.jiraclone.backend.dto.ProjectDTO;
import com.jiraclone.backend.dto.UserDTO;
import com.jiraclone.backend.model.Project;
import com.jiraclone.backend.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(
            @Valid @RequestBody Project project,
            @AuthenticationPrincipal UserDetails userDetails) {
        ProjectDTO createdProject = projectService.createProject(project, userDetails.getUsername());
        return ResponseEntity
                .created(URI.create("/projects/" + createdProject.getId()))
                .body(createdProject);
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getProjects(@AuthenticationPrincipal UserDetails userDetails) {
        List<ProjectDTO> projects = projectService.getProjectsByUser(userDetails.getUsername());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        ProjectDTO project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody Project projectDetails) {
        ProjectDTO updatedProject = projectService.updateProject(id, projectDetails);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/members/{userId}")
    public ResponseEntity<ProjectDTO> addMemberToProject(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        ProjectDTO updatedProject = projectService.addMemberToProject(projectId, userId);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<ProjectDTO> removeMemberFromProject(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        ProjectDTO updatedProject = projectService.removeMemberFromProject(projectId, userId);
        return ResponseEntity.ok(updatedProject);
    }

    @PutMapping("/{projectId}/lead")
    public ResponseEntity<ProjectDTO> changeProjectLead(
            @PathVariable Long projectId,
            @RequestBody Map<String, Long> requestBody) {
        Long newLeadId = requestBody.get("leadId");
        if (newLeadId == null) {
            return ResponseEntity.badRequest().build();
        }
        ProjectDTO updatedProject = projectService.changeProjectLead(projectId, newLeadId);
        return ResponseEntity.ok(updatedProject);
    }

        // Add this method to your ProjectController class
    @GetMapping("/{projectId}/members")
    public ResponseEntity<?> getProjectMembers(@PathVariable Long projectId) {
        List<UserDTO> members = projectService.getProjectMembers(projectId);
        return ResponseEntity.ok(members);
    }
    // Add this method for testing
@GetMapping("/{projectId}/debug-members")
public ResponseEntity<?> debugMembers(@PathVariable Long projectId) {
    // Just a simple map to hold our results
    java.util.Map<String, Object> result = new java.util.HashMap<>();
    
    // Get direct SQL count of members
    // This requires adding EntityManager to your ProjectService
    int count = projectService.countMembersByNativeQuery(projectId);
    result.put("memberCount", count);
    
    // Try to get the actual members
    List<UserDTO> members = projectService.getProjectMembers(projectId);
    result.put("members", members);
    
    return ResponseEntity.ok(result);
}

        // Add this endpoint to your ProjectController class
    @PutMapping("/{projectId}/members/{userId}/role")
    public ResponseEntity<?> updateMemberRole(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestBody Map<String, String> requestBody) {
        
        String role = requestBody.get("role");
        if (role == null) {
            return ResponseEntity.badRequest().build();
        }
        
        projectService.updateMemberRole(projectId, userId, role);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{projectId}/members-sql")
public ResponseEntity<?> getMembersBySQL(@PathVariable Long projectId) {
    List<UserDTO> members = projectService.getProjectMembersDirectSQL(projectId);
    return ResponseEntity.ok(members);
}
}