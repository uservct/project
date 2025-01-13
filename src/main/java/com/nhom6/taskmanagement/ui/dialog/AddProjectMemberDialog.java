package com.nhom6.taskmanagement.ui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.nhom6.taskmanagement.model.Project;
import com.nhom6.taskmanagement.model.ProjectRole;
import com.nhom6.taskmanagement.model.User;
import com.nhom6.taskmanagement.service.ProjectMemberService;
import com.nhom6.taskmanagement.service.UserService;

public class AddProjectMemberDialog extends JDialog {
    private final Project project;
    private final ProjectMemberService projectMemberService;
    private final UserService userService;
    private JComboBox<User> userComboBox;
    private JComboBox<ProjectRole> roleComboBox;
    private boolean confirmed = false;

    public AddProjectMemberDialog(JFrame parent, Project project) {
        super(parent, "Thêm thành viên", true);
        this.project = project;
        this.projectMemberService = new ProjectMemberService();
        this.userService = new UserService();
        initComponents();
        loadAvailableUsers();
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

        // User selection
        formPanel.add(new JLabel("Chọn thành viên:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        userComboBox = new JComboBox<>();
        userComboBox.setRenderer(new UserListCellRenderer()); // Custom renderer để hiển thị tên
        formPanel.add(userComboBox, gbc);

        // Role selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;

        // Lọc các role cần hiển thị
        ProjectRole[] availableRoles = Arrays.stream(ProjectRole.values())
            .filter(role -> role == ProjectRole.OWNER || role == ProjectRole.MEMBER)
            .toArray(ProjectRole[]::new);
        roleComboBox = new JComboBox<>(availableRoles);

        formPanel.add(roleComboBox, gbc);

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
        // Lấy tất cả user trong hệ thống
        List<User> allUsers = userService.getAllUsers();
        
        // Lọc ra những user chưa là thành viên của dự án
        List<User> availableUsers = allUsers.stream()
            .filter(user -> !projectMemberService.isProjectMember(project.getId(), user.getId()))
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

    public ProjectRole getSelectedRole() {
        return confirmed ? (ProjectRole) roleComboBox.getSelectedItem() : null;
    }
}

// Custom renderer để hiển thị tên user trong combobox
class UserListCellRenderer extends javax.swing.DefaultListCellRenderer {
    @Override
    public java.awt.Component getListCellRendererComponent(
            javax.swing.JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        if (value instanceof User) {
            User user = (User) value;
            setText(user.getFullName() + " (" + user.getUsername() + ")");
        }
        
        return this;
    }
} 