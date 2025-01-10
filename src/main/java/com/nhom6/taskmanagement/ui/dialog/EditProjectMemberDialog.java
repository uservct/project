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
import javax.swing.JPanel;

import com.nhom6.taskmanagement.model.ProjectMember;
import com.nhom6.taskmanagement.model.ProjectRole;
import com.nhom6.taskmanagement.model.User;
import com.nhom6.taskmanagement.service.UserService;

public class EditProjectMemberDialog extends JDialog {
    private final ProjectMember member;
    private final UserService userService;
    private JComboBox<ProjectRole> roleComboBox;
    private boolean confirmed = false;

    public EditProjectMemberDialog(JFrame parent, ProjectMember member) {
        super(parent, "Thay đổi vai trò", true);
        this.member = member;
        this.userService = new UserService();
        initComponents();
        loadMemberData();
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

        // Member info
        User user = userService.getUserById(member.getUserId());
        if (user != null) {
            formPanel.add(new JLabel("Thành viên:"), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            formPanel.add(new JLabel(user.getFullName()), gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            formPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            formPanel.add(new JLabel(user.getEmail()), gbc);
        }

        // Role selection
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(new ProjectRole[] {
            ProjectRole.MANAGER,
            ProjectRole.MEMBER
        });
        formPanel.add(roleComboBox, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadMemberData() {
        roleComboBox.setSelectedItem(member.getRole());
    }

    public ProjectRole getSelectedRole() {
        return confirmed ? (ProjectRole) roleComboBox.getSelectedItem() : null;
    }
} 