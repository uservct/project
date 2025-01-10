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

import com.nhom6.taskmanagement.model.Task;
import com.nhom6.taskmanagement.model.TaskStatus;
import com.nhom6.taskmanagement.service.TaskService;

public class EditTaskDialog extends JDialog {
    private final Task task;
    private final TaskService taskService;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JComboBox<TaskStatus> statusComboBox;
    private JSpinner startDateSpinner;
    private JSpinner dueDateSpinner;
    private boolean confirmed = false;

    public EditTaskDialog(JFrame parent, Task task) {
        super(parent, "Chỉnh sửa công việc", true);
        this.task = task;
        this.taskService = new TaskService();
        initComponents();
        loadTaskData();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
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

        // Status
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(TaskStatus.values());
        formPanel.add(statusComboBox, gbc);

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

    private void loadTaskData() {
        nameField.setText(task.getName());
        descriptionArea.setText(task.getDescription());
        statusComboBox.setSelectedItem(task.getStatus());
        
        // Convert LocalDate to java.util.Date for spinners
        java.util.Date startDate = java.util.Date.from(
            task.getStartDate().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        java.util.Date dueDate = java.util.Date.from(
            task.getDueDate().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        
        startDateSpinner.setValue(startDate);
        dueDateSpinner.setValue(dueDate);
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

    public Task getUpdatedTask() {
        if (!confirmed) return null;

        task.setName(nameField.getText().trim());
        task.setDescription(descriptionArea.getText().trim());
        task.setStatus((TaskStatus) statusComboBox.getSelectedItem());
        
        // Convert java.util.Date to LocalDate
        java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
        java.util.Date dueDate = (java.util.Date) dueDateSpinner.getValue();
        task.setStartDate(startDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        task.setDueDate(dueDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());

        return task;
    }
} 