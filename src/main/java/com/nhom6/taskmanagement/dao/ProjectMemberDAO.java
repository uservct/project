package com.nhom6.taskmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.nhom6.taskmanagement.config.DatabaseConfig;
import com.nhom6.taskmanagement.model.ProjectMember;
import com.nhom6.taskmanagement.model.ProjectRole;

public class ProjectMemberDAO {
    
    public List<ProjectMember> findByProjectId(Long projectId) {
        List<ProjectMember> members = new ArrayList<>();
        String sql = "SELECT * FROM project_members WHERE project_id = ? AND is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, projectId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                members.add(mapResultSetToProjectMember(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public ProjectMember findById(Long id) {
        String sql = "SELECT * FROM project_members WHERE id = ? AND is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProjectMember(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ProjectMember save(ProjectMember member) {
        String sql = "INSERT INTO project_members (project_id, user_id, role, joined_at, " +
                    "created_by, created_at) VALUES (?, ?, ?, NOW(), ?, NOW())";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, member.getProjectId());
            stmt.setLong(2, member.getUserId());
            stmt.setString(3, member.getRole().name());
            stmt.setLong(4, member.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    member.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return member;
    }

    public ProjectMember update(ProjectMember member) {
        String sql = "UPDATE project_members SET role = ?, updated_at = NOW(), " +
                    "is_deleted = ?, deleted_at = ?, deleted_by = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, member.getRole().name());
            stmt.setBoolean(2, member.getIsDeleted());
            stmt.setTimestamp(3, member.getDeletedAt() != null ? 
                Timestamp.valueOf(member.getDeletedAt()) : null);
            stmt.setObject(4, member.getDeletedBy());
            stmt.setLong(5, member.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return member;
    }

    private ProjectMember mapResultSetToProjectMember(ResultSet rs) throws SQLException {
        ProjectMember member = new ProjectMember();
        member.setId(rs.getLong("id"));
        member.setProjectId(rs.getLong("project_id"));
        member.setUserId(rs.getLong("user_id"));
        member.setRole(ProjectRole.valueOf(rs.getString("role")));
        member.setJoinedAt(rs.getTimestamp("joined_at").toLocalDateTime());
        member.setCreatedBy(rs.getLong("created_by"));
        member.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        member.setUpdatedAt(rs.getTimestamp("updated_at") != null ? 
            rs.getTimestamp("updated_at").toLocalDateTime() : null);
        
        // BaseEntity fields
        member.setIsDeleted(rs.getBoolean("is_deleted"));
        member.setDeletedAt(rs.getTimestamp("deleted_at") != null ? 
            rs.getTimestamp("deleted_at").toLocalDateTime() : null);
        member.setDeletedBy(rs.getLong("deleted_by"));
        
        return member;
    }
} 