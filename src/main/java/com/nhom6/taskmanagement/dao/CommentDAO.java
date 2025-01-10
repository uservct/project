package com.nhom6.taskmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.nhom6.taskmanagement.config.DatabaseConfig;
import com.nhom6.taskmanagement.model.Comment;

public class CommentDAO {
    
    public List<Comment> findByProjectId(Long projectId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE project_id = ? AND is_deleted = false " +
                    "ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, projectId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                comments.add(mapResultSetToComment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public List<Comment> findByTaskId(Long taskId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE task_id = ? AND is_deleted = false " +
                    "ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, taskId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                comments.add(mapResultSetToComment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public Comment save(Comment comment) {
        String sql = "INSERT INTO comments (content, project_id, task_id, created_by, created_at) " +
                    "VALUES (?, ?, ?, ?, NOW())";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, comment.getContent());
            stmt.setObject(2, comment.getProjectId());
            stmt.setObject(3, comment.getTaskId());
            stmt.setLong(4, comment.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    comment.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comment;
    }

    public void delete(Long id) {
        String sql = "UPDATE comments SET is_deleted = true, deleted_at = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setContent(rs.getString("content"));
        comment.setProjectId(rs.getLong("project_id"));
        comment.setTaskId(rs.getLong("task_id"));
        comment.setCreatedBy(rs.getLong("created_by"));
        comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        comment.setUpdatedAt(rs.getTimestamp("updated_at") != null ? 
            rs.getTimestamp("updated_at").toLocalDateTime() : null);
        
        // BaseEntity fields
        comment.setIsDeleted(rs.getBoolean("is_deleted"));
        comment.setDeletedAt(rs.getTimestamp("deleted_at") != null ? 
            rs.getTimestamp("deleted_at").toLocalDateTime() : null);
        comment.setDeletedBy(rs.getLong("deleted_by"));
        
        return comment;
    }
} 