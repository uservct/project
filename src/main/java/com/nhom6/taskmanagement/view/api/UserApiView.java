package com.nhom6.taskmanagement.view.api;

import com.nhom6.taskmanagement.config.AppConfig;
import com.nhom6.taskmanagement.model.User;
import com.nhom6.taskmanagement.service.UserService;

import javax.swing.*;

import java.awt.*;
import java.util.List;

import com.nhom6.taskmanagement.config.ServiceManager;

public class UserApiView extends JFrame {
    private UserService userService;

    public UserApiView() {
        this.userService = ServiceManager.getUserService();
        initUI();
    }

    private void initUI() {
        setTitle("API Người dùng");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        List<User> users = userService.getAllUsers();
        String[] columnNames = {"ID", "Tên", "Email"};
        Object[][] data = new Object[users.size()][3];

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            data[i][0] = user.getId();
            data[i][1] = user.getName();
            data[i][2] = user.getEmail();
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserApiView userApiView = new UserApiView();
            userApiView.setVisible(true);
        });
    }
}