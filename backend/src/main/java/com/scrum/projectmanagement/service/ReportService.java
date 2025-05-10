package com.scrum.projectmanagement.service;

import com.scrum.projectmanagement.dto.ReportDTO;
import com.scrum.projectmanagement.model.Project;
import com.scrum.projectmanagement.model.Report;
import com.scrum.projectmanagement.model.Task;
import com.scrum.projectmanagement.model.User;
import com.scrum.projectmanagement.repository.ProjectRepository;
import com.scrum.projectmanagement.repository.ReportRepository;
import com.scrum.projectmanagement.repository.TaskRepository;
import com.scrum.projectmanagement.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String openaiApiUrl;

    // Get all reports for a project
    public List<ReportDTO> getReportsByProject(Long projectId) {
        return reportRepository.findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(ReportDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Get a specific report
    public ReportDTO getReportById(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found with ID: " + reportId));
        return ReportDTO.fromEntity(report);
    }

    // Delete a report
    @Transactional
    public void deleteReport(Long reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new EntityNotFoundException("Report not found with ID: " + reportId);
        }
        reportRepository.deleteById(reportId);
    }

    // Generate a report using ChatGPT
    @Transactional
public ReportDTO generateReport(Long projectId, String userEmail, String prompt, Report.ReportType type) {
    // Get project
    Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));

    // Get user
    User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail));

    // Gather project data for context
    Map<String, Object> projectData = new HashMap<>();
    projectData.put("name", project.getName());
    projectData.put("key", project.getKey());
    projectData.put("description", project.getDescription());

    // Get tasks data
    // REMOVE THIS DUPLICATE PROJECT DECLARATION:
    // Project project = projectRepository.findById(projectId)
    // .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));
    
    // Just use the existing project variable:
    List<Task> tasks = taskRepository.findByProjectId(projectId);
        Map<String, Integer> taskStatusCounts = new HashMap<>();
    taskStatusCounts.put("TO_DO", 0);
    taskStatusCounts.put("IN_PROGRESS", 0);
    taskStatusCounts.put("DONE", 0);


        for (Task task : tasks) {
            String status = task.getStatus().name();
            taskStatusCounts.put(status, taskStatusCounts.getOrDefault(status, 0) + 1);
        }

        // Create project context for ChatGPT
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("Project Name: ").append(project.getName()).append("\n");
        contextBuilder.append("Project Key: ").append(project.getKey()).append("\n");
        if (project.getDescription() != null && !project.getDescription().isEmpty()) {
            contextBuilder.append("Description: ").append(project.getDescription()).append("\n");
        }

        // Add tasks data
        contextBuilder.append("\nTask Statistics:\n");
        contextBuilder.append("- Total Tasks: ").append(tasks.size()).append("\n");
        contextBuilder.append("- To Do: ").append(taskStatusCounts.get("TO_DO")).append("\n");
        contextBuilder.append("- In Progress: ").append(taskStatusCounts.get("IN_PROGRESS")).append("\n");
        contextBuilder.append("- Done: ").append(taskStatusCounts.get("DONE")).append("\n");

        // Add team info
        contextBuilder.append("\nTeam Information:\n");
        contextBuilder.append("- Team Size: ").append(project.getMembers().size()).append("\n");
        contextBuilder.append("- Project Lead: ").append(project.getLead() != null ? project.getLead().getName() : "Not assigned").append("\n");

        String context = contextBuilder.toString();

        // Generate report content using ChatGPT
        String reportContent = generateReportContent(context, prompt, type);

        // Create and save the report
        Report report = new Report();
        report.setTitle(generateReportTitle(project.getName(), type));
        report.setContent(reportContent);
        report.setPrompt(prompt);
        report.setType(type);
        report.setProject(project);
        report.setAuthor(user);

        Report savedReport = reportRepository.save(report);
                // Add these to ReportService.java inside generateReport method
        System.out.println("Starting report generation for project: " + projectId);
        System.out.println("User email: " + userEmail);
        System.out.println("Report type: " + type);
        System.out.println("Prompt: " + prompt);

// After fetching tasks
System.out.println("Found " + tasks.size() + " tasks for project");
        return ReportDTO.fromEntity(savedReport);
    }

    // Helper method to generate report title
    private String generateReportTitle(String projectName, Report.ReportType type) {
        String typeStr = type.toString().replace("_", " ");
        return projectName + " - " + typeStr.substring(0, 1).toUpperCase() + typeStr.substring(1).toLowerCase();
    }

    // Method to call ChatGPT API
    private String generateReportContent(String context, String userPrompt, Report.ReportType type) {
        // If OpenAI API key is not configured, return dummy content
        if (openaiApiKey == null || openaiApiKey.isEmpty() || openaiApiKey.equals("your_openai_api_key_here")) {
            return generateDummyReport(context, userPrompt, type);
        }

        try {
    RestTemplate restTemplate = new RestTemplate();

    // Set headers
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(openaiApiKey);  // Check if this is the correct auth method
    
    // Print the API URL for debugging
    System.out.println("Using OpenAI API URL: " + openaiApiUrl);
    System.out.println("API Key starting with: " + openaiApiKey.substring(0, 10) + "...");

            // Prepare system message based on report type
            String systemMessage = getSystemMessageForReportType(type);

            // Format the request
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");

            List<Map<String, String>> messages = new ArrayList<>();

            // System message to define the assistant's role
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemMessage);
            messages.add(systemMsg);

            // Add project context
            Map<String, String> contextMsg = new HashMap<>();
            contextMsg.put("role", "user");
            contextMsg.put("content", "Here is the project data:\n" + context);
            messages.add(contextMsg);

            // Add user prompt
            Map<String, String> promptMsg = new HashMap<>();
            promptMsg.put("role", "user");
            promptMsg.put("content", userPrompt);
            messages.add(promptMsg);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);

            // Create request entity
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // Call OpenAI API
            ResponseEntity<Map> response = restTemplate.exchange(
                    openaiApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            // Process response
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");

                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    return message.get("content");
                }
            }

            return "Failed to generate report content.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating report: " + e.getMessage();
        }
    }

    // Get system message based on report type
    private String getSystemMessageForReportType(Report.ReportType type) {
        switch (type) {
            case STATUS_REPORT:
                return "You are a project management assistant that creates detailed status reports. Format your response in Markdown with clear sections including Project Overview, Task Status, Team Performance, and Next Steps.";
            case SPRINT_ANALYSIS:
                return "You are a scrum master assistant that analyzes sprint performance. Format your response in Markdown with sections for Sprint Overview, Velocity Analysis, Completed vs Planned Work, and Recommendations for the next sprint.";
            case TEAM_PERFORMANCE:
                return "You are a team performance analyst. Format your response in Markdown with sections for Team Composition, Productivity Metrics, Strengths and Areas for Improvement, and Recommendations.";
            case RISK_ASSESSMENT:
                return "You are a project risk assessment specialist. Format your response in Markdown with sections for Risk Overview, Key Risks Identified, Impact Analysis, Mitigation Strategies, and Recommendations.";
            case CUSTOM:
            default:
                return "You are a project management assistant that helps create reports based on project data. Your responses should be in Markdown format with clear sections and actionable insights.";
        }
    }

    // Generate dummy report content for testing or when API key is not available
    private String generateDummyReport(String context, String userPrompt, Report.ReportType type) {
        String[] lines = context.split("\n");
        String projectName = "";
        int totalTasks = 0;
        int todoTasks = 0;
        int inProgressTasks = 0;
        int doneTasks = 0;
        int teamSize = 0;

        for (String line : lines) {
            if (line.startsWith("Project Name:")) {
                projectName = line.substring("Project Name:".length()).trim();
            } else if (line.startsWith("- Total Tasks:")) {
                totalTasks = Integer.parseInt(line.substring("- Total Tasks:".length()).trim());
            } else if (line.startsWith("- To Do:")) {
                todoTasks = Integer.parseInt(line.substring("- To Do:".length()).trim());
            } else if (line.startsWith("- In Progress:")) {
                inProgressTasks = Integer.parseInt(line.substring("- In Progress:".length()).trim());
            } else if (line.startsWith("- Done:")) {
                doneTasks = Integer.parseInt(line.substring("- Done:".length()).trim());
            } else if (line.startsWith("- Team Size:")) {
                teamSize = Integer.parseInt(line.substring("- Team Size:".length()).trim());
            }
        }

        int completionPercentage = totalTasks > 0 ? (doneTasks * 100) / totalTasks : 0;

        StringBuilder report = new StringBuilder();

        switch (type) {
            case STATUS_REPORT:
                report.append("# Status Report for ").append(projectName).append("\n\n");
                report.append("## Project Overview\n\n");
                report.append("This project is currently ").append(completionPercentage).append("% complete based on task completion. ");
                report.append("There are a total of ").append(totalTasks).append(" tasks in the project.\n\n");
                report.append("## Task Status\n\n");
                report.append("- **To Do**: ").append(todoTasks).append(" tasks\n");
                report.append("- **In Progress**: ").append(inProgressTasks).append(" tasks\n");
                report.append("- **Done**: ").append(doneTasks).append(" tasks\n\n");
                report.append("## Team Performance\n\n");
                report.append("The team consists of ").append(teamSize).append(" members who are working collaboratively on the project tasks.\n\n");
                report.append("## Next Steps\n\n");
                report.append("1. Focus on completing the ").append(inProgressTasks).append(" in-progress tasks\n");
                report.append("2. Begin planning for the next ").append(Math.min(5, todoTasks)).append(" tasks in the To Do column\n");
                report.append("3. Schedule a team review for completed work\n\n");
                break;

            case SPRINT_ANALYSIS:
                report.append("# Sprint Analysis for ").append(projectName).append("\n\n");
                report.append("## Sprint Overview\n\n");
                report.append("The current sprint has ").append(totalTasks).append(" total tasks, with ").append(doneTasks).append(" completed (").append(completionPercentage).append("% completion rate).\n\n");
                report.append("## Velocity Analysis\n\n");
                report.append("The team's velocity is trending ").append(completionPercentage > 70 ? "positively" : "below target").append(". ");
                report.append("Current velocity is approximately ").append(doneTasks).append(" tasks per sprint.\n\n");
                report.append("## Completed vs Planned Work\n\n");
                report.append("- **Planned**: ").append(totalTasks).append(" tasks\n");
                report.append("- **Completed**: ").append(doneTasks).append(" tasks\n");
                report.append("- **Completion Rate**: ").append(completionPercentage).append("%\n\n");
                report.append("## Recommendations\n\n");
                if (completionPercentage < 70) {
                    report.append("1. Consider reducing the sprint scope in the next planning session\n");
                    report.append("2. Review any blockers that might be slowing down task completion\n");
                    report.append("3. Schedule a team retrospective to identify improvement areas\n");
                } else {
                    report.append("1. Maintain current sprint velocity and team dynamics\n");
                    report.append("2. Consider increasing sprint scope if the team feels comfortable\n");
                    report.append("3. Recognize team members for their excellent performance\n");
                }
                break;

            default:
                report.append("# Custom Report for ").append(projectName).append("\n\n");
                report.append("## Project Analysis\n\n");
                report.append("Based on your prompt: \"").append(userPrompt).append("\"\n\n");
                report.append("The project currently has ").append(totalTasks).append(" tasks with the following breakdown:\n\n");
                report.append("- **To Do**: ").append(todoTasks).append(" tasks\n");
                report.append("- **In Progress**: ").append(inProgressTasks).append(" tasks\n");
                report.append("- **Done**: ").append(doneTasks).append(" tasks\n\n");
                report.append("The overall completion percentage is ").append(completionPercentage).append("%\n\n");
                report.append("## Team Overview\n\n");
                report.append("There are ").append(teamSize).append(" team members working on this project.\n\n");
                report.append("## Recommendations\n\n");
                report.append("1. Review the in-progress tasks to ensure they're on track\n");
                report.append("2. Schedule regular check-ins with the team\n");
                report.append("3. Prioritize the remaining to-do tasks\n");
                report.append("4. Document lessons learned from completed tasks\n");
        }

        return report.toString();
    }
}