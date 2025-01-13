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
import com.nhom6.taskmanagement.model.User;
import com.nhom6.taskmanagement.model.UserRole;
import com.nhom6.taskmanagement.model.UserStatus;

public class UserDAO {
    
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ? AND is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public User save(User user) {
        String sql = "INSERT INTO users (username, password, email, full_name, " +
                    "avatar_url, phone_number, position, role, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getAvatarUrl());
            stmt.setString(6, user.getPhoneNumber());
            stmt.setString(7, user.getPosition());
            stmt.setString(8, user.getRole().name());
            stmt.setString(9, user.getStatus().name());
            stmt.setTimestamp(10, Timestamp.valueOf(user.getCreatedAt()));
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getLong(1));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public User update(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, full_name = ?, " +
                    "avatar_url = ?, phone_number = ?, position = ?, role = ?, " +
                    "status = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getAvatarUrl());
            stmt.setString(5, user.getPhoneNumber());
            stmt.setString(6, user.getPosition());
            stmt.setString(7, user.getRole().name());
            stmt.setString(8, user.getStatus().name());
            stmt.setTimestamp(9, Timestamp.valueOf(user.getUpdatedAt()));
            stmt.setLong(10, user.getId());
            
            stmt.executeUpdate();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // public void delete(Long id) {
    //     String sql = "UPDATE users SET is_deleted = true, deleted_at = ? WHERE id = ?";
        
    //     try (Connection conn = DatabaseConfig.getConnection();
    //          PreparedStatement stmt = conn.prepareStatement(sql)) {
            
    //         stmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
    //         stmt.setLong(2, id);
    //         stmt.executeUpdate();
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    // }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ? AND is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_deleted = false";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(Long userId, String hashedPassword) {
        String sql = "UPDATE users SET password = ?, updated_at = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, hashedPassword);
            stmt.setLong(2, userId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //chuyển đổi dữ liệu 1 dòng ResultSet sang 1 User
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email")); 
        user.setFullName(rs.getString("full_name"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setPosition(rs.getString("position"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setStatus(UserStatus.valueOf(rs.getString("status")));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return user;
    }
} 