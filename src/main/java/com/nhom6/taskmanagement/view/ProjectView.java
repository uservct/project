package com.nhom6.taskmanagement.view;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.nhom6.taskmanagement.config.ServiceManager;
import com.nhom6.taskmanagement.model.Project;
import com.nhom6.taskmanagement.service.ProjectService;
import com.nhom6.taskmanagement.service.UserService;

public class ProjectView extends JFrame {
    private ProjectService projectService;
    private UserService userService;

    public ProjectView() {
        this.projectService = ServiceManager.getProjectService();
        this.userService = ServiceManager.getUserService();
        initUI();
    }

    private void initUI() {
        setTitle("Danh sách dự án");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        List<Project> projects = projectService.getAllProjects();
        String[] columnNames = {"ID", "Tên", "Trạng thái"};
        Object[][] data = new Object[projects.size()][3];

        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            data[i][0] = project.getId();
            data[i][1] = project.getName();
            data[i][2] = project.getStatus();
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProjectView projectView = new ProjectView();
            projectView.setVisible(true);
        });
    }
}