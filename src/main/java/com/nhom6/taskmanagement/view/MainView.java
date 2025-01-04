package com.nhom6.taskmanagement.view;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    public MainView() {
        initUI();
    }

    private void initUI() {
        setTitle("Task Management Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add components to the main view
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JButton userButton = new JButton("Users");
        userButton.addActionListener(e -> {
            UserView userView = new UserView();
            userView.setVisible(true);
        });

        JButton projectButton = new JButton("Projects");
        projectButton.addActionListener(e -> {
            ProjectView projectView = new ProjectView();
            projectView.setVisible(true);
        });

        JButton taskButton = new JButton("Tasks");
        taskButton.addActionListener(e -> {
            TaskView taskView = new TaskView();
            taskView.setVisible(true);
        });

        panel.add(userButton, BorderLayout.NORTH);
        panel.add(projectButton, BorderLayout.CENTER);
        panel.add(taskButton, BorderLayout.SOUTH);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView();
            mainView.setVisible(true);
        });
    }
}