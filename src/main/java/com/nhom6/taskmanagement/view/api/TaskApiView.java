package com.nhom6.taskmanagement.view.api;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.nhom6.taskmanagement.config.ServiceManager;
import com.nhom6.taskmanagement.model.Task;
import com.nhom6.taskmanagement.service.TaskService;

public class TaskApiView extends JFrame {
    private TaskService taskService;

    public TaskApiView() {
        this.taskService = ServiceManager.getTaskService();
        initUI();
    }

    private void initUI() {
        setTitle("API Nhiệm vụ");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        List<Task> tasks = taskService.getAllTasks();
        String[] columnNames = {"ID", "Tên", "Trạng thái"};
        Object[][] data = new Object[tasks.size()][3];

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            data[i][0] = task.getId();
            data[i][1] = task.getName();
            data[i][2] = task.getStatus();
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TaskApiView taskApiView = new TaskApiView();
            taskApiView.setVisible(true);
        });
    }
}