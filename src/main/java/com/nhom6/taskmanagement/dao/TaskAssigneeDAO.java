package com.nhom6.taskmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.nhom6.taskmanagement.config.DatabaseConfig;
import com.nhom6.taskmanagement.model.TaskAssignee;
import com.nhom6.taskmanagement.model.TaskStatus;

public class TaskAssigneeDAO {
    
    public List<TaskAssignee> findByTaskId(Long taskId) {
        List<TaskAssignee> assignees = new ArrayList<>();
        String sql = "SELECT * FROM task_assignees WHERE task_id = ? AND is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, taskId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                assignees.add(mapResultSetToTaskAssignee(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignees;
    }

    public TaskAssignee save(TaskAssignee assignee) {
        String sql = "INSERT INTO task_assignees (task_id, user_id, status, created_by, created_at) " +
                    "VALUES (?, ?, ?, ?, NOW())";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, assignee.getTaskId());
            stmt.setLong(2, assignee.getUserId());
            stmt.setString(3, TaskStatus.TODO.name());
            stmt.setLong(4, assignee.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    assignee.setId(rs.getLong(1));
                    assignee.setStatus(TaskStatus.TODO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignee;
    }

    public void delete(Long taskId, Long userId) {
        String sql = "UPDATE task_assignees SET is_deleted = true, deleted_at = NOW() " +
                    "WHERE task_id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, taskId);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStatus(Long taskId, Long userId, TaskStatus newStatus) {
        String sql = "UPDATE task_assignees SET status = ?, updated_at = NOW() " +
                    "WHERE task_id = ? AND user_id = ? AND is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus.name());
            stmt.setLong(2, taskId);
            stmt.setLong(3, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private TaskAssignee mapResultSetToTaskAssignee(ResultSet rs) throws SQLException {
        TaskAssignee assignee = new TaskAssignee();
        assignee.setId(rs.getLong("id"));
        assignee.setTaskId(rs.getLong("task_id"));
        assignee.setUserId(rs.getLong("user_id"));
        assignee.setCreatedBy(rs.getLong("created_by"));
        assignee.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        assignee.setUpdatedAt(rs.getTimestamp("updated_at") != null ? 
            rs.getTimestamp("updated_at").toLocalDateTime() : null);
        
        // BaseEntity fields
        assignee.setIsDeleted(rs.getBoolean("is_deleted"));
        assignee.setDeletedAt(rs.getTimestamp("deleted_at") != null ? 
            rs.getTimestamp("deleted_at").toLocalDateTime() : null);
        assignee.setDeletedBy(rs.getLong("deleted_by"));
        
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            assignee.setStatus(TaskStatus.valueOf(statusStr));
        } else {
            assignee.setStatus(TaskStatus.TODO);
        }
        
        return assignee;
    }
} 