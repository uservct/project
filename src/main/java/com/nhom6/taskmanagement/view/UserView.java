package com.nhom6.taskmanagement.view;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.nhom6.taskmanagement.config.ServiceManager;
import com.nhom6.taskmanagement.model.User;
import com.nhom6.taskmanagement.service.UserService;

public class UserView extends JFrame {
    private UserService userService;

    public UserView() {
        this.userService = ServiceManager.getUserService();
        initUI();
    }

    private void initUI() {
        setTitle("Danh sách người dùng");
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
            UserView userView = new UserView();
            userView.setVisible(true);
        });
    }
}