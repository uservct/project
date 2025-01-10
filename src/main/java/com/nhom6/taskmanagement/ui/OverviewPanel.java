package com.nhom6.taskmanagement.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nhom6.taskmanagement.model.Project;
import com.nhom6.taskmanagement.model.Task;
import com.nhom6.taskmanagement.model.TaskStatus;
import com.nhom6.taskmanagement.service.ProjectService;
import com.nhom6.taskmanagement.service.TaskService;

public class OverviewPanel extends JPanel {
    private final ProjectService projectService;
    private final TaskService taskService;

    public OverviewPanel() {
        this.projectService = new ProjectService();
        this.taskService = new TaskService();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Tổng quan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Tạo panel container để căn giữa statsPanel
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(Box.createHorizontalGlue(), BorderLayout.WEST);
        centerContainer.add(statsPanel, BorderLayout.CENTER);
        centerContainer.add(Box.createHorizontalGlue(), BorderLayout.EAST);

        // Project Stats
        List<Project> projects = projectService.getAllProjects();
        statsPanel.add(createStatPanel("Tổng số dự án", String.valueOf(projects.size())));

        // Task Stats
        int totalTasks = 0;
        int completedTasks = 0;
        for (Project project : projects) {
            List<Task> tasks = taskService.getTasksByProject(project.getId());
            totalTasks += tasks.size();
            completedTasks += tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();
        }

        statsPanel.add(createStatPanel("Tổng số công việc", String.valueOf(totalTasks)));
        statsPanel.add(createStatPanel("Công việc đã hoàn thành", String.valueOf(completedTasks)));
        statsPanel.add(createStatPanel("Tỷ lệ hoàn thành", 
            String.format("%.1f%%", totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0)));

        add(centerContainer, BorderLayout.CENTER);
    }

    private JPanel createStatPanel(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Tạo container panel cho nội dung
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false); // Để không ảnh hưởng đến màu nền

        JLabel titleLabel = new JLabel(title);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(valueLabel);

        // Thêm glue để căn giữa theo chiều dọc
        panel.add(Box.createVerticalGlue());
        panel.add(contentPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }
} 