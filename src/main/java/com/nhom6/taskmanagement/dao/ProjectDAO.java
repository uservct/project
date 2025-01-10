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
import com.nhom6.taskmanagement.model.Project;
import com.nhom6.taskmanagement.model.ProjectPriority;
import com.nhom6.taskmanagement.model.ProjectStatus;

public class ProjectDAO {
    
    public List<Project> findAll() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }

    public Project findById(Long id) {
        String sql = "SELECT * FROM projects WHERE id = ? AND is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProject(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Project save(Project project) {
        String sql = "INSERT INTO projects (name, description, status, start_date, due_date, " +
                    "created_by, created_at, priority, tag) " +
                    "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setString(3, project.getStatus().name());
            stmt.setDate(4, Date.valueOf(project.getStartDate()));
            stmt.setDate(5, Date.valueOf(project.getDueDate()));
            stmt.setLong(6, project.getCreatedBy());
            stmt.setString(7, project.getPriority().name());
            stmt.setString(8, project.getTag());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    project.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return project;
    }

    public Project update(Project project) {
        String sql = "UPDATE projects SET name = ?, description = ?, status = ?, " +
                    "start_date = ?, due_date = ?, end_date = ?, priority = ?, " +
                    "tag = ?, updated_at = NOW(), is_deleted = ?, deleted_at = ?, " +
                    "deleted_by = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setString(3, project.getStatus().name());
            stmt.setDate(4, Date.valueOf(project.getStartDate()));
            stmt.setDate(5, Date.valueOf(project.getDueDate()));
            stmt.setDate(6, project.getEndDate() != null ? 
                Date.valueOf(project.getEndDate()) : null);
            stmt.setString(7, project.getPriority().name());
            stmt.setString(8, project.getTag());
            stmt.setBoolean(9, project.getIsDeleted());
            stmt.setTimestamp(10, project.getDeletedAt() != null ? 
                Timestamp.valueOf(project.getDeletedAt()) : null);
            stmt.setObject(11, project.getDeletedBy());
            stmt.setLong(12, project.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return project;
    }

    public List<Project> findByMemberId(Long userId) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT p.* FROM projects p " +
                    "INNER JOIN project_members pm ON p.id = pm.project_id " +
                    "WHERE pm.user_id = ? AND p.is_deleted = false AND pm.is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }

    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getLong("id"));
        project.setName(rs.getString("name"));
        project.setDescription(rs.getString("description"));
        project.setStatus(ProjectStatus.valueOf(rs.getString("status")));
        project.setStartDate(rs.getDate("start_date").toLocalDate());
        project.setDueDate(rs.getDate("due_date").toLocalDate());
        project.setEndDate(rs.getDate("end_date") != null ? 
            rs.getDate("end_date").toLocalDate() : null);
        project.setCreatedBy(rs.getLong("created_by"));
        project.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        project.setUpdatedAt(rs.getTimestamp("updated_at") != null ? 
            rs.getTimestamp("updated_at").toLocalDateTime() : null);
        project.setPriority(ProjectPriority.valueOf(rs.getString("priority")));
        project.setTag(rs.getString("tag"));
        
        // BaseEntity fields
        project.setIsDeleted(rs.getBoolean("is_deleted"));
        project.setDeletedAt(rs.getTimestamp("deleted_at") != null ? 
            rs.getTimestamp("deleted_at").toLocalDateTime() : null);
        project.setDeletedBy(rs.getLong("deleted_by"));
        
        return project;
    }
} 