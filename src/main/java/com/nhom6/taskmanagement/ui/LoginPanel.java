package com.nhom6.taskmanagement.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.nhom6.taskmanagement.service.UserService;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private final UserService userService;
    private final JFrame mainFrame;

    public LoginPanel(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userService = new UserService();
        initComponents();
        setupListeners();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Panel chứa form đăng nhập
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Tiêu đề
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        loginPanel.add(titleLabel, gbc);

        // Username
        gbc.gridy++;
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        loginPanel.add(usernameLabel, gbc);

        gbc.gridy++;
        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(200, 30));
        loginPanel.add(usernameField, gbc);

        // Password
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        loginPanel.add(passwordLabel, gbc);

        gbc.gridy++;
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 30));
        loginPanel.add(passwordField, gbc);

        // Login button
        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        loginButton = new JButton("Đăng nhập");
        loginButton.setPreferredSize(new Dimension(120, 35));
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginPanel.add(loginButton, gbc);

        add(loginPanel);
    }

    private void setupListeners() {
        loginButton.addActionListener(e -> handleLogin());
        
        // Handle Enter key
        KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
        InputMap inputMap = loginButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(enter, "ENTER");
        loginButton.getActionMap().put("ENTER", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập đầy đủ thông tin đăng nhập",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userService.login(username, password)) {
            mainFrame.getContentPane().removeAll();
            mainFrame.add(new DashboardPanel(mainFrame));
            mainFrame.revalidate();
            mainFrame.repaint();
        } else {
            JOptionPane.showMessageDialog(this,
                "Tên đăng nhập hoặc mật khẩu không đúng",
                "Lỗi đăng nhập",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 