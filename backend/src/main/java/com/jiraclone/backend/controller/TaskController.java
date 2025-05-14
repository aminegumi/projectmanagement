package com.jiraclone.backend.controller;


import com.jiraclone.backend.dto.TaskDTO;
import com.jiraclone.backend.model.Task;
import com.jiraclone.backend.service.TaskService;
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
@RequestMapping("/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // Adjust the origin as needed
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(
            @Valid @RequestBody Task task,
            @RequestParam Long projectId,
            @AuthenticationPrincipal UserDetails userDetails) {
        TaskDTO createdTask = taskService.createTask(task, projectId, userDetails.getUsername());
        return ResponseEntity
                .created(URI.create("/tasks/" + createdTask.getId()))
                .body(createdTask);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDTO>> getTasksByProject(@PathVariable Long projectId) {
        List<TaskDTO> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<TaskDTO>> getAssignedTasks(@AuthenticationPrincipal UserDetails userDetails) {
        List<TaskDTO> tasks = taskService.getTasksByAssignee(userDetails.getUsername());
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody Task taskDetails) {
        TaskDTO updatedTask = taskService.updateTask(id, taskDetails);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{id}/priority")
    public ResponseEntity<TaskDTO> updatePriority(
            @PathVariable Long id,
            @RequestBody Map<String, String> priorityUpdate) {
        String priority = priorityUpdate.get("priority");
        if (priority == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Task.Priority taskPriority = Task.Priority.valueOf(priority);
        TaskDTO updatedTask = taskService.updateTaskPriority(id, taskPriority);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        String status = statusUpdate.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Task.Status taskStatus = Task.Status.valueOf(status);
        TaskDTO updatedTask = taskService.updateTaskStatus(id, taskStatus);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<TaskDTO> assignTask(
            @PathVariable Long id,
            @RequestBody Map<String, Long> assignRequest) {
        Long assigneeId = assignRequest.get("assigneeId");
        if (assigneeId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        TaskDTO updatedTask = taskService.assignTask(id, assigneeId);
        return ResponseEntity.ok(updatedTask);
    }

    @PutMapping("/{id}/assignee")
    public ResponseEntity<TaskDTO> updateAssignee(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        Long assigneeId = request.get("assigneeId");
        TaskDTO updatedTask = taskService.assignTask(id, assigneeId);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<List<TaskDTO>> getTasksBySprint(@PathVariable Long sprintId) {
        List<TaskDTO> tasks = taskService.getTasksBySprint(sprintId);
        return ResponseEntity.ok(tasks);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}