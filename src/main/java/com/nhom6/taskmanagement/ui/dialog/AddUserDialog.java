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
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.nhom6.taskmanagement.model.User;
import com.nhom6.taskmanagement.model.UserRole;
import com.nhom6.taskmanagement.model.UserStatus;

public class AddUserDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JTextField phoneField;
    private JTextField positionField;
    private JComboBox<UserRole> roleComboBox;
    private JComboBox<UserStatus> statusComboBox;
    private boolean confirmed = false;

    public AddUserDialog(JFrame parent) {
        super(parent, "Thêm thành viên mới", true);
        initComponents();
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

        // Username
        formPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Full Name
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1;
        fullNameField = new JTextField(20);
        formPanel.add(fullNameField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        // Position
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Vị trí:"), gbc);
        gbc.gridx = 1;
        positionField = new JTextField(20);
        formPanel.add(positionField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(UserRole.values());
        formPanel.add(roleComboBox, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(UserStatus.values());
        formPanel.add(statusComboBox, gbc);

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

    private boolean validateInput() {
        if (usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập tên đăng nhập",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập mật khẩu",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public User getUser() {
        if (!confirmed) return null;

        User user = new User();
        user.setUsername(usernameField.getText().trim());
        user.setPassword(new String(passwordField.getPassword()));
        user.setEmail(emailField.getText().trim());
        user.setFullName(fullNameField.getText().trim());
        user.setPhoneNumber(phoneField.getText().trim());
        user.setPosition(positionField.getText().trim());
        user.setRole((UserRole) roleComboBox.getSelectedItem());
        user.setStatus((UserStatus) statusComboBox.getSelectedItem());

        return user;
    }
} 