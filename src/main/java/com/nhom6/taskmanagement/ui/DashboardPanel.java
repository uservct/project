package com.nhom6.taskmanagement.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import com.nhom6.taskmanagement.model.User;
import com.nhom6.taskmanagement.model.UserRole;
import com.nhom6.taskmanagement.service.UserService;

public class DashboardPanel extends JPanel {
    private final JFrame mainFrame;
    private final User currentUser;
    private final UserService userService;
    private JPanel menuPanel;
    private JPanel contentPanel;
    private static final Font MENU_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 14);

    public DashboardPanel(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.currentUser = UserService.getCurrentUser();
        this.userService = new UserService();
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Menu panel
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        menuPanel.setBackground(new Color(51, 51, 51));
        menuPanel.setPreferredSize(new Dimension(200, getHeight()));

        // User info
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setOpaque(false);
        JLabel nameLabel = new JLabel(currentUser.getFullName());
        JLabel roleLabel = new JLabel(currentUser.getRole().toString());
        nameLabel.setForeground(Color.WHITE);
        roleLabel.setForeground(Color.WHITE);
        userInfoPanel.add(nameLabel);
        userInfoPanel.add(roleLabel);
        menuPanel.add(userInfoPanel);
        menuPanel.add(Box.createVerticalStrut(20));

        // Menu buttons
        addMenuButton("Tổng quan", e -> showOverview());
        
        // Nếu là ADMIN thì hiển thị tất cả các nút
        if (currentUser.getRole() == UserRole.ADMIN) {
            addMenuButton("Dự án", e -> showProjects());
            addMenuButton("Công việc", e -> showTasks());
            addMenuButton("Quản lý thành viên", e -> showUserManagement());
        } else {
            // Nếu là thành viên thường thì chỉ hiển thị dự án và công việc của họ
            addMenuButton("Dự án của tôi", e -> showMyProjects());
            addMenuButton("Công việc của tôi", e -> showMyTasks());
        }

        addMenuButton("Đổi mật khẩu", e -> showChangePasswordDialog());
        addMenuButton("Đăng xuất", e -> logout());

        add(menuPanel, BorderLayout.WEST);

        // Content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(contentPanel, BorderLayout.CENTER);

        // Hiển thị tổng quan mặc định
        showOverview();
    }

    private void setupLayout() {
        mainFrame.setTitle("Task Management System");
        mainFrame.setMinimumSize(new Dimension(1024, 768));
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void addMenuButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setBackground(new Color(51, 51, 51));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.addActionListener(listener);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(75, 75, 75));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(51, 51, 51));
            }
        });
        menuPanel.add(button);
        menuPanel.add(Box.createVerticalStrut(5));
    }

    private void showOverview() {
        showContent(new OverviewPanel());
    }

    private void showProjects() {
        showContent(new ProjectListPanel(mainFrame, true)); // true = hiện tất cả dự án
    }

    private void showTasks() {
        showContent(new TaskListPanel(mainFrame, true)); // true = hiện tất cả công việc
    }

    private void showUserManagement() {
        showContent(new UserManagementPanel(mainFrame));
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            mainFrame,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            userService.logout();
            mainFrame.getContentPane().removeAll();
            mainFrame.add(new LoginPanel(mainFrame));
            mainFrame.pack();
            mainFrame.setLocationRelativeTo(null);
            mainFrame.revalidate();
            mainFrame.repaint();
        }
    }

    private void showMyProjects() {
        showContent(new ProjectListPanel(mainFrame, false)); // false = chỉ hiện dự án của thành viên
    }

    private void showMyTasks() {
        showContent(new TaskListPanel(mainFrame, false)); // false = chỉ hiện công việc của thành viên
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(mainFrame, "Đổi mật khẩu", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(mainFrame);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Mật khẩu hiện tại
        panel.add(new JLabel("Mật khẩu hiện tại:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPasswordField currentPasswordField = new JPasswordField(20);
        panel.add(currentPasswordField, gbc);

        // Mật khẩu mới
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Mật khẩu mới:"), gbc);
        gbc.gridx = 1;
        JPasswordField newPasswordField = new JPasswordField(20);
        panel.add(newPasswordField, gbc);

        // Xác nhận mật khẩu mới
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Xác nhận mật khẩu:"), gbc);
        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField(20);
        panel.add(confirmPasswordField, gbc);

        // Panel chứa nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(e -> {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Kiểm tra mật khẩu hiện tại
            if (!userService.authenticate(currentUser.getUsername(), currentPassword)) {
                JOptionPane.showMessageDialog(dialog,
                    "Mật khẩu hiện tại không đúng",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra mật khẩu mới
            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập mật khẩu mới",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra xác nhận mật khẩu
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog,
                    "Mật khẩu xác nhận không khớp",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cập nhật mật khẩu
            if (userService.resetPassword(currentUser.getId(), newPassword)) {
                JOptionPane.showMessageDialog(dialog,
                    "Đổi mật khẩu thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Không thể đổi mật khẩu. Vui lòng thử lại sau",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void showContent(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
} 