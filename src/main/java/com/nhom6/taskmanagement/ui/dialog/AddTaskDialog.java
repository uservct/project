package com.nhom6.taskmanagement.ui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

import com.nhom6.taskmanagement.model.Project;
import com.nhom6.taskmanagement.model.Task;
import com.nhom6.taskmanagement.model.TaskStatus;
import com.nhom6.taskmanagement.service.ProjectService;

public class AddTaskDialog extends JDialog {
    private final ProjectService projectService;
    private final Long projectId;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JComboBox<Project> projectComboBox;
    private JSpinner startDateSpinner;
    private JSpinner dueDateSpinner;
    private boolean confirmed = false;

    public AddTaskDialog(JFrame parent, Long projectId) {
        super(parent, "Thêm công việc mới", true);
        this.projectService = new ProjectService();
        this.projectId = projectId;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Task Name
        formPanel.add(new JLabel("Tên công việc:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nameField = new JTextField(30);
        formPanel.add(nameField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        // Project selection (if projectId is null)
        if (projectId == null) {
            gbc.gridx = 0;
            gbc.gridy++;
            formPanel.add(new JLabel("Dự án:"), gbc);
            gbc.gridx = 1;
            projectComboBox = new JComboBox<>();
            loadProjects();
            formPanel.add(projectComboBox, gbc);
        }

        // Start Date
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Ngày bắt đầu:"), gbc);
        gbc.gridx = 1;
        SpinnerDateModel startModel = new SpinnerDateModel();
        startDateSpinner = new JSpinner(startModel);
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "dd/MM/yyyy"));
        formPanel.add(startDateSpinner, gbc);

        // Due Date
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Ngày kết thúc:"), gbc);
        gbc.gridx = 1;
        SpinnerDateModel dueModel = new SpinnerDateModel();
        dueDateSpinner = new JSpinner(dueModel);
        dueDateSpinner.setEditor(new JSpinner.DateEditor(dueDateSpinner, "dd/MM/yyyy"));
        formPanel.add(dueDateSpinner, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadProjects() {
        projectComboBox.removeAllItems();
        for (Project project : projectService.getAllProjects()) {
            projectComboBox.addItem(project);
        }
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập tên công việc", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public Task getTask() {
        if (!confirmed) return null;

        Task task = new Task();
        task.setName(nameField.getText().trim());
        task.setDescription(descriptionArea.getText().trim());
        task.setStatus(TaskStatus.TODO);
        task.setProjectId(projectId != null ? projectId : 
            ((Project) projectComboBox.getSelectedItem()).getId());
        
        // Convert java.util.Date to LocalDate
        java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
        java.util.Date dueDate = (java.util.Date) dueDateSpinner.getValue();
        task.setStartDate(startDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        task.setDueDate(dueDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());

        return task;
    }
} 