package com.scrum.projectmanagement.controller;

import com.scrum.projectmanagement.dto.UserStoryDTO;
import com.scrum.projectmanagement.model.UserStory;
import com.scrum.projectmanagement.service.UserStoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/user-stories")
@RequiredArgsConstructor
public class UserStoryController {

    private final UserStoryService userStoryService;

    @PostMapping
    public ResponseEntity<UserStoryDTO> createUserStory(
            @Valid @RequestBody UserStory story,
            @RequestParam Long projectId,
            @RequestParam(required = false) Long epicId) {
        UserStoryDTO createdStory = userStoryService.createUserStory(story, projectId, epicId);
        return ResponseEntity
                .created(URI.create("/user-stories/" + createdStory.getId()))
                .body(createdStory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserStoryDTO> getUserStoryById(@PathVariable Long id) {
        UserStoryDTO story = userStoryService.getUserStoryById(id);
        return ResponseEntity.ok(story);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<UserStoryDTO>> getUserStoriesByProject(@PathVariable Long projectId) {
        List<UserStoryDTO> stories = userStoryService.getUserStoriesByProject(projectId);
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/epic/{epicId}")
    public ResponseEntity<List<UserStoryDTO>> getUserStoriesByEpic(@PathVariable Long epicId) {
        List<UserStoryDTO> stories = userStoryService.getUserStoriesByEpic(epicId);
        return ResponseEntity.ok(stories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserStoryDTO> updateUserStory(
            @PathVariable Long id,
            @Valid @RequestBody UserStory storyDetails) {
        UserStoryDTO updatedStory = userStoryService.updateUserStory(id, storyDetails);
        return ResponseEntity.ok(updatedStory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserStory(@PathVariable Long id) {
        userStoryService.deleteUserStory(id);
        return ResponseEntity.noContent().build();
    }
}