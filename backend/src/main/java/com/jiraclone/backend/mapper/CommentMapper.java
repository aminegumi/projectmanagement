package com.jiraclone.backend.mapper;


import com.jiraclone.backend.dto.CommentDTO;
import com.jiraclone.backend.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentDTO toDTO(Comment comment) {
        if(comment == null) {
            return null;
        }
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        if (comment.getTask() != null) {
            dto.setTaskId(comment.getTask().getId());
        }
        
        if (comment.getAuthor() != null) {
            dto.setAuthorId(comment.getAuthor().getId());
            dto.setAuthorName(comment.getAuthor().getName());
            dto.setAuthorEmail(comment.getAuthor().getEmail());
        }
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }

    public Comment toEntity(CommentDTO dto) {
        Comment comment = new Comment();
        if(dto != null) {
            comment.setId(dto.getId());
            comment.setText(dto.getText());
        }
        return comment;
    }
}