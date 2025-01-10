package com.nhom6.taskmanagement.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.nhom6.taskmanagement.config.DatabaseConfig;
import com.nhom6.taskmanagement.model.Task;
import com.nhom6.taskmanagement.model.TaskStatus;

public class TaskDAO {
    
    public List<Task> findByProjectId(Long projectId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE project_id = ? AND is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, projectId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public Task findById(Long id) {
        String sql = "SELECT * FROM tasks WHERE id = ? AND is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTask(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Task save(Task task) {
        String sql = "INSERT INTO tasks (name, description, status, start_date, due_date, " +
                    "project_id, tag, created_by, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, task.getName());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus().name());
            stmt.setDate(4, Date.valueOf(task.getStartDate()));
            stmt.setDate(5, Date.valueOf(task.getDueDate()));
            stmt.setLong(6, task.getProjectId());
            stmt.setString(7, task.getTag());
            stmt.setLong(8, task.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    task.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return task;
    }

    public Task update(Task task) {
        String sql = "UPDATE tasks SET name = ?, description = ?, status = ?, " +
                    "start_date = ?, due_date = ?, updated_at = NOW(), " +
                    "is_deleted = ?, deleted_at = ?, deleted_by = ? " +
                    "WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, task.getName());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus().name());
            stmt.setDate(4, Date.valueOf(task.getStartDate()));
            stmt.setDate(5, Date.valueOf(task.getDueDate()));
            stmt.setBoolean(6, task.getIsDeleted());
            stmt.setTimestamp(7, task.getDeletedAt() != null ? 
                Timestamp.valueOf(task.getDeletedAt()) : null);
            stmt.setObject(8, task.getDeletedBy());
            stmt.setLong(9, task.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return task;
    }

    public List<Task> findByAssigneeId(Long userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT t.* FROM tasks t " +
                    "INNER JOIN task_assignees ta ON t.id = ta.task_id " +
                    "WHERE ta.user_id = ? AND t.is_deleted = false AND ta.is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public boolean isTaskAssignee(Long taskId, Long userId) {
        String sql = "SELECT COUNT(*) FROM task_assignees " +
                    "WHERE task_id = ? AND user_id = ? AND is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, taskId);
            stmt.setLong(2, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setName(rs.getString("name"));
        task.setDescription(rs.getString("description"));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        task.setStartDate(rs.getDate("start_date").toLocalDate());
        task.setDueDate(rs.getDate("due_date").toLocalDate());
        task.setProjectId(rs.getLong("project_id"));
        task.setTag(rs.getString("tag"));
        task.setCreatedBy(rs.getLong("created_by"));
        task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        task.setUpdatedAt(rs.getTimestamp("updated_at") != null ? 
            rs.getTimestamp("updated_at").toLocalDateTime() : null);
        
        // BaseEntity fields
        task.setIsDeleted(rs.getBoolean("is_deleted"));
        task.setDeletedAt(rs.getTimestamp("deleted_at") != null ? 
            rs.getTimestamp("deleted_at").toLocalDateTime() : null);
        task.setDeletedBy(rs.getLong("deleted_by"));
        
        return task;
    }
} 