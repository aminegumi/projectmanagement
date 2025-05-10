package com.scrum.projectmanagement.service;


import com.scrum.projectmanagement.dto.TaskDTO;
import com.scrum.projectmanagement.model.Project;
import com.scrum.projectmanagement.model.Sprint;
import com.scrum.projectmanagement.model.Task;
import com.scrum.projectmanagement.model.User;
import com.scrum.projectmanagement.repository.ProjectRepository;
import com.scrum.projectmanagement.repository.SprintRepository;
import com.scrum.projectmanagement.repository.TaskRepository;
import com.scrum.projectmanagement.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final SprintRepository sprintRepository;

    @Transactional
    public TaskDTO createTask(Task task, Long projectId, String reporterEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));
        
        User reporter = userRepository.findByEmail(reporterEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + reporterEmail));

        // Generate task key
        String taskKey = project.getKey() + "-" + (project.getTasks().size() + 1);
        task.setTaskKey(taskKey);
        task.setProject(project);
        task.setReporter(reporter);
        
        // Default status for new task
        if (task.getStatus() == null) {
            task.setStatus(Task.Status.TODO);
        }
        
        Task savedTask = taskRepository.save(task);
        return TaskDTO.fromEntity(savedTask);
    }

    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + id));
        return TaskDTO.fromEntity(task);
    }

    public List<TaskDTO> getTasksByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));
        
        return taskRepository.findByProjectOrderByCreatedAtDesc(project).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByAssignee(String userEmail) {
        User assignee = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail));
        
        return taskRepository.findByAssigneeOrderByCreatedAtDesc(assignee).stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskDTO updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + id));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setType(taskDetails.getType());
        task.setPriority(taskDetails.getPriority());
        task.setStatus(taskDetails.getStatus());
        task.setDueDate(taskDetails.getDueDate());
        task.setEstimatedHours(taskDetails.getEstimatedHours());
        task.setLoggedHours(taskDetails.getLoggedHours());

        if (taskDetails.getSprint() != null) {
            Sprint sprint = sprintRepository.findById(taskDetails.getSprint().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Sprint not found"));
            task.setSprint(sprint);
        }
        // Update assignee if provided
        if (taskDetails.getAssignee() != null) {
            User assignee = userRepository.findById(taskDetails.getAssignee().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Assignee not found"));
            task.setAssignee(assignee);
        }

        Task updatedTask = taskRepository.save(task);
        return TaskDTO.fromEntity(updatedTask);
    }

    @Transactional
    public TaskDTO updateTaskStatus(Long id, Task.Status status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + id));

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return TaskDTO.fromEntity(updatedTask);
    }

    @Transactional
    public TaskDTO assignTask(Long taskId, Long assigneeId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + assigneeId));
        
        // Check if user is a member of the project
        if (!task.getProject().getMembers().contains(assignee)) {
            throw new IllegalArgumentException("User is not a member of the project");
        }

        task.setAssignee(assignee);
        Task updatedTask = taskRepository.save(task);
        return TaskDTO.fromEntity(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task not found with ID: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Transactional
    public TaskDTO updateTaskPriority(Long id, Task.Priority priority) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + id));

        task.setPriority(priority);
        Task updatedTask = taskRepository.save(task);
        return TaskDTO.fromEntity(updatedTask);
    }

    public List<TaskDTO> getTasksBySprint(Long sprintId) {
        List<Task> tasks = taskRepository.findBySprintIdOrderByCreatedAtDesc(sprintId);
        return tasks.stream()
                .map(task -> {
                    TaskDTO dto = TaskDTO.fromEntity(task);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}