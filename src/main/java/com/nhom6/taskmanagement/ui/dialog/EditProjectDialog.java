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
import com.nhom6.taskmanagement.model.ProjectPriority;
import com.nhom6.taskmanagement.model.ProjectStatus;
import com.nhom6.taskmanagement.service.ProjectService;

public class EditProjectDialog extends JDialog {
    private final Project project;
    private final ProjectService projectService;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JComboBox<ProjectPriority> priorityComboBox;
    private JComboBox<ProjectStatus> statusComboBox;
    private JSpinner startDateSpinner;
    private JSpinner dueDateSpinner;
    private boolean confirmed = false;

    public EditProjectDialog(JFrame parent, Project project) {
        super(parent, "Chỉnh sửa dự án", true);
        this.project = project;
        this.projectService = new ProjectService();
        initComponents();
        loadProjectData();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        // Similar to AddProjectDialog but add status selection
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Project Name
        formPanel.add(new JLabel("Tên dự án:"), gbc);
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

        // Status
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(ProjectStatus.values());
        formPanel.add(statusComboBox, gbc);

        // Priority
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Độ ưu tiên:"), gbc);
        gbc.gridx = 1;
        priorityComboBox = new JComboBox<>(ProjectPriority.values());
        formPanel.add(priorityComboBox, gbc);

        // Dates
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Ngày bắt đầu:"), gbc);
        gbc.gridx = 1;
        SpinnerDateModel startModel = new SpinnerDateModel();
        startDateSpinner = new JSpinner(startModel);
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "dd/MM/yyyy"));
        formPanel.add(startDateSpinner, gbc);

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

    private void loadProjectData() {
        nameField.setText(project.getName());
        descriptionArea.setText(project.getDescription());
        statusComboBox.setSelectedItem(project.getStatus());
        priorityComboBox.setSelectedItem(project.getPriority());
        
        // Convert LocalDate to java.util.Date for spinners
        java.util.Date startDate = java.util.Date.from(
            project.getStartDate().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        java.util.Date dueDate = java.util.Date.from(
            project.getDueDate().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        
        startDateSpinner.setValue(startDate);
        dueDateSpinner.setValue(dueDate);
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập tên dự án",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public Project getUpdatedProject() {
        if (!confirmed) return null;

        project.setName(nameField.getText().trim());
        project.setDescription(descriptionArea.getText().trim());
        project.setStatus((ProjectStatus) statusComboBox.getSelectedItem());
        project.setPriority((ProjectPriority) priorityComboBox.getSelectedItem());
        
        // Convert java.util.Date to LocalDate
        java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
        java.util.Date dueDate = (java.util.Date) dueDateSpinner.getValue();
        project.setStartDate(startDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        project.setDueDate(dueDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());

        return project;
    }
} 