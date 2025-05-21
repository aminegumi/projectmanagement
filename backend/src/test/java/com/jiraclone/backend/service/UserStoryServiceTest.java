package com.jiraclone.backend.service;

import com.jiraclone.backend.dto.UserStoryDTO;
import com.jiraclone.backend.model.Epic;
import com.jiraclone.backend.model.Project;
import com.jiraclone.backend.model.UserStory;
import com.jiraclone.backend.repository.EpicRepository;
import com.jiraclone.backend.repository.ProjectRepository;
import com.jiraclone.backend.repository.UserStoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserStoryServiceTest {

    @Mock
    private UserStoryRepository userStoryRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private EpicRepository epicRepository;

    @InjectMocks
    private UserStoryService userStoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

   @Test
void testCreateUserStory_WithEpic() {
    Long projectId = 1L;
    Long epicId = 2L;

    Project project = new Project();
    project.setId(projectId);

    Epic epic = new Epic();
    epic.setId(epicId);

    UserStory userStory = new UserStory();
    userStory.setTitle("Story 1");
    userStory.setDescription("Desc");

    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));
    when(userStoryRepository.save(any(UserStory.class))).thenAnswer(i -> i.getArgument(0));

    UserStoryDTO result = userStoryService.createUserStory(userStory, projectId, epicId);

    assertEquals("Story 1", result.getTitle());
    verify(userStoryRepository).save(any(UserStory.class));
}
@Test
void testCreateUserStory_ProjectNotFound() {
    when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> {
        userStoryService.createUserStory(new UserStory(), 1L, null);
    });
}
@Test
void testGetUserStoryById_Success() {
    UserStory story = new UserStory();
    story.setId(1L);
    story.setTitle("Test");

    when(userStoryRepository.findById(1L)).thenReturn(Optional.of(story));

    UserStoryDTO result = userStoryService.getUserStoryById(1L);

    assertEquals("Test", result.getTitle());
}
@Test
void testGetUserStoryById_NotFound() {
    when(userStoryRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> userStoryService.getUserStoryById(1L));
}
@Test
void testDeleteUserStory_Success() {
    when(userStoryRepository.existsById(1L)).thenReturn(true);

    userStoryService.deleteUserStory(1L);

    verify(userStoryRepository).deleteById(1L);
}
@Test
void testDeleteUserStory_NotFound() {
    when(userStoryRepository.existsById(1L)).thenReturn(false);

    assertThrows(EntityNotFoundException.class, () -> userStoryService.deleteUserStory(1L));
}

}

