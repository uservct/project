package com.nhom6.taskmanagement.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.nhom6.taskmanagement.model.Task;
import com.nhom6.taskmanagement.model.TaskAssignee;
import com.nhom6.taskmanagement.model.User;
import com.nhom6.taskmanagement.service.TaskAssigneeService;
import com.nhom6.taskmanagement.service.UserService;

public class AddTaskMemberDialog extends JDialog {
    private final Task task;
    private final TaskAssigneeService taskAssigneeService;
    private final UserService userService;
    private JComboBox<User> userComboBox;
    private boolean confirmed = false;

    public AddTaskMemberDialog(JFrame parent, Task task) {
        super(parent, "Thêm thành viên", true);
        this.task = task;
        this.taskAssigneeService = new TaskAssigneeService();
        this.userService = new UserService();
        initComponents();
        loadAvailableUsers();
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

        // User selection
        formPanel.add(new JLabel("Chọn thành viên:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        userComboBox = new JComboBox<>();
        userComboBox.setRenderer(new UserListCellRenderer());
        formPanel.add(userComboBox, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Thêm");
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

    private void loadAvailableUsers() {
        List<User> allUsers = userService.getAllUsers();
        List<TaskAssignee> existingAssignees = taskAssigneeService.getAssigneesByTask(task.getId());
        
        List<User> availableUsers = allUsers.stream()
            .filter(user -> existingAssignees.stream()
                .noneMatch(assignee -> assignee.getUserId().equals(user.getId())))
            .collect(Collectors.toList());
        
        for (User user : availableUsers) {
            userComboBox.addItem(user);
        }
    }

    private boolean validateInput() {
        if (userComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn thành viên",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public User getSelectedUser() {
        return confirmed ? (User) userComboBox.getSelectedItem() : null;
    }

    private static class UserListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof User) {
                User user = (User) value;
                setText(user.getFullName() + " (" + user.getUsername() + ")");
            }
            return this;
        }
    }
} 