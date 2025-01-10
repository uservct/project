package com.nhom6.taskmanagement.service;

import java.time.LocalDateTime;
import java.util.List;

import com.nhom6.taskmanagement.dao.CommentDAO;
import com.nhom6.taskmanagement.model.Comment;

public class CommentService {
    private final CommentDAO commentDAO;

    public CommentService() {
        this.commentDAO = new CommentDAO();
    }

    public List<Comment> getCommentsByProject(Long projectId) {
        return commentDAO.findByProjectId(projectId);
    }

    public List<Comment> getCommentsByTask(Long taskId) {
        return commentDAO.findByTaskId(taskId);
    }

    public Comment addComment(String content, Long projectId, Long taskId) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setProjectId(projectId);
        comment.setTaskId(taskId);
        comment.setCreatedBy(UserService.getCurrentUser().getId());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setIsDeleted(false);
        
        return commentDAO.save(comment);
    }

    public void deleteComment(Long commentId) {
        commentDAO.delete(commentId);
    }
} 