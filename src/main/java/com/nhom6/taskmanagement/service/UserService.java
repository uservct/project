package com.nhom6.taskmanagement.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import com.nhom6.taskmanagement.config.DatabaseConfig;
import com.nhom6.taskmanagement.dao.UserDAO;
import com.nhom6.taskmanagement.model.User;

public class UserService {
    private final UserDAO userDAO;
    private static User currentUser; // Lưu thông tin user đang đăng nhập

    public UserService() {
        this.userDAO = new UserDAO();
    }

    // Thêm phương thức xử lý đăng nhập
    public boolean login(String username, String password) {
        if (userDAO.authenticate(username, password)) {
            currentUser = userDAO.findByUsername(username);
            return true;
        }
        return false;
    }

    // Lấy thông tin user đang đăng nhập
    public static User getCurrentUser() {
        return currentUser;
    }

    // Đăng xuất
    public void logout() {
        currentUser = null;
    }

    // Thêm phương thức authenticate
    public boolean authenticate(String username, String password) {
        return userDAO.authenticate(username, password);
    }

    // Các phương thức khác giữ nguyên
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public User getUserById(Long id) {
        return userDAO.findById(id);
    }

    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public User createUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        return userDAO.save(user);
    }

    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userDAO.update(user);
        
        // Nếu user đang được cập nhật là user hiện tại, cập nhật thông tin trong session
        if (currentUser != null && currentUser.getId().equals(user.getId())) {
            currentUser = updatedUser;
        }
        
        return updatedUser;
    }

    public boolean deleteUser(Long userId) {
        try {
            Connection conn = DatabaseConfig.getConnection();
            // Thực hiện xóa thật sự từ database thay vì chỉ đánh dấu is_deleted
            String sql = "DELETE FROM users WHERE id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            
            int result = stmt.executeUpdate();
            
            stmt.close();
            conn.close();
            
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean resetPassword(Long userId, String newPassword) {
        return userDAO.updatePassword(userId, newPassword);
    }
} 