package com.jiraclone.backend.controller;

import com.jiraclone.backend.dto.EpicDTO;
import com.jiraclone.backend.model.Epic;
import com.jiraclone.backend.service.EpicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/epics")
@RequiredArgsConstructor
public class EpicController {

    private final EpicService epicService;

    @PostMapping
    public ResponseEntity<EpicDTO> createEpic(
            @Valid @RequestBody Epic epic,
            @RequestParam Long projectId) {
        EpicDTO createdEpic = epicService.createEpic(epic, projectId);
        return ResponseEntity
                .created(URI.create("/epics/" + createdEpic.getId()))
                .body(createdEpic);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EpicDTO> getEpicById(@PathVariable Long id) {
        EpicDTO epic = epicService.getEpicById(id);
        return ResponseEntity.ok(epic);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<EpicDTO>> getEpicsByProject(@PathVariable Long projectId) {
        List<EpicDTO> epics = epicService.getEpicsByProject(projectId);
        return ResponseEntity.ok(epics);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EpicDTO> updateEpic(
            @PathVariable Long id,
            @Valid @RequestBody Epic epicDetails) {
        EpicDTO updatedEpic = epicService.updateEpic(id, epicDetails);
        return ResponseEntity.ok(updatedEpic);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEpic(@PathVariable Long id) {
        epicService.deleteEpic(id);
        return ResponseEntity.noContent().build();
    }
}