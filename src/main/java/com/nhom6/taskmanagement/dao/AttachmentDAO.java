package com.nhom6.taskmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.nhom6.taskmanagement.config.DatabaseConfig;
import com.nhom6.taskmanagement.model.Attachment;

public class AttachmentDAO {
    
    public List<Attachment> findByProjectId(Long projectId) {
        List<Attachment> attachments = new ArrayList<>();
        String sql = "SELECT * FROM attachments WHERE project_id = ? AND is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, projectId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                attachments.add(mapResultSetToAttachment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attachments;
    }

    public List<Attachment> findByTaskId(Long taskId) {
        List<Attachment> attachments = new ArrayList<>();
        String sql = "SELECT * FROM attachments WHERE task_id = ? AND is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, taskId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                attachments.add(mapResultSetToAttachment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attachments;
    }

    public Attachment save(Attachment attachment) {
        String sql = "INSERT INTO attachments (project_id, task_id, file_name, file_path, " +
                    "file_type, file_size, created_by, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setObject(1, attachment.getProjectId());
            stmt.setObject(2, attachment.getTaskId());
            stmt.setString(3, attachment.getFileName());
            stmt.setString(4, attachment.getFilePath());
            stmt.setString(5, attachment.getFileType());
            stmt.setLong(6, attachment.getFileSize());
            stmt.setLong(7, attachment.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    attachment.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attachment;
    }

    public void delete(Long id) {
        String sql = "UPDATE attachments SET is_deleted = true, deleted_at = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Attachment findById(Long id) {
        String sql = "SELECT * FROM attachments WHERE id = ? AND is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAttachment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Attachment mapResultSetToAttachment(ResultSet rs) throws SQLException {
        Attachment attachment = new Attachment();
        attachment.setId(rs.getLong("id"));
        attachment.setProjectId(rs.getLong("project_id"));
        attachment.setTaskId(rs.getLong("task_id"));
        attachment.setFileName(rs.getString("file_name"));
        attachment.setFilePath(rs.getString("file_path"));
        attachment.setFileSize(rs.getLong("file_size"));
        attachment.setCreatedBy(rs.getLong("created_by"));
        attachment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        attachment.setUpdatedAt(rs.getTimestamp("updated_at") != null ? 
            rs.getTimestamp("updated_at").toLocalDateTime() : null);
        
        // BaseEntity fields
        attachment.setIsDeleted(rs.getBoolean("is_deleted"));
        attachment.setDeletedAt(rs.getTimestamp("deleted_at") != null ? 
            rs.getTimestamp("deleted_at").toLocalDateTime() : null);
        attachment.setDeletedBy(rs.getLong("deleted_by"));
        
        return attachment;
    }
} 