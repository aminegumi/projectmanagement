package com.jiraclone.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;

    @NotBlank(message = "Comment text cannot be empty")
    private String text;

    private Long taskId;

    private Long authorId;
    private String authorName;
    private String authorEmail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}