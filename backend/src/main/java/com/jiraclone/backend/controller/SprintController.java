package com.scrum.projectmanagement.controller;

import com.scrum.projectmanagement.dto.SprintDTO;
import com.scrum.projectmanagement.model.Sprint;
import com.scrum.projectmanagement.service.SprintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/sprints")
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;

    @PostMapping
    public ResponseEntity<SprintDTO> createSprint(
            @Valid @RequestBody Sprint sprint,
            @RequestParam Long projectId) {
        SprintDTO createdSprint = sprintService.createSprint(sprint, projectId);
        return ResponseEntity
                .created(URI.create("/sprints/" + createdSprint.getId()))
                .body(createdSprint);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SprintDTO> getSprintById(@PathVariable Long id) {
        SprintDTO sprint = sprintService.getSprintById(id);
        return ResponseEntity.ok(sprint);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<SprintDTO>> getSprintsByProject(@PathVariable Long projectId) {
        List<SprintDTO> sprints = sprintService.getSprintsByProject(projectId);
        return ResponseEntity.ok(sprints);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SprintDTO> updateSprint(
            @PathVariable Long id,
            @Valid @RequestBody Sprint sprintDetails) {
        SprintDTO updatedSprint = sprintService.updateSprint(id, sprintDetails);
        return ResponseEntity.ok(updatedSprint);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSprint(@PathVariable Long id) {
        sprintService.deleteSprint(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<SprintDTO> startSprint(@PathVariable Long id) {
        SprintDTO updatedSprint = sprintService.startSprint(id);
        return ResponseEntity.ok(updatedSprint);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<SprintDTO> completeSprint(@PathVariable Long id) {
        SprintDTO updatedSprint = sprintService.completeSprint(id);
        return ResponseEntity.ok(updatedSprint);
    }
}