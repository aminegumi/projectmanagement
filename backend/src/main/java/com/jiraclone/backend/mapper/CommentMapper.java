package com.scrum.projectmanagement.mapper;


import com.scrum.projectmanagement.dto.CommentDTO;
import com.scrum.projectmanagement.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentDTO toDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setTaskId(comment.getTask().getId());
        dto.setAuthorId(comment.getAuthor().getId());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setAuthorEmail(comment.getAuthor().getEmail());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }

    public Comment toEntity(CommentDTO dto) {
        Comment comment = new Comment();
        comment.setId(dto.getId());
        comment.setText(dto.getText());
        return comment;
    }
}