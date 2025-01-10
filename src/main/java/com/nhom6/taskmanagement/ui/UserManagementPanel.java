package com.nhom6.taskmanagement.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.nhom6.taskmanagement.model.User;
import com.nhom6.taskmanagement.service.UserService;
import com.nhom6.taskmanagement.ui.dialog.AddUserDialog;
import com.nhom6.taskmanagement.ui.dialog.EditUserDialog;

public class UserManagementPanel extends JPanel {
    private final JFrame mainFrame;
    private final UserService userService;
    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserManagementPanel(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userService = new UserService();
        initComponents();
        loadUsers();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Quản lý thành viên");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Thêm thành viên");
        JButton editButton = new JButton("Chỉnh sửa");
        JButton deleteButton = new JButton("Xóa");
        JButton resetPasswordButton = new JButton("Reset mật khẩu");

        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        deleteButton.addActionListener(e -> handleDeleteUser());
        resetPasswordButton.addActionListener(e -> handleResetPassword());

        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(resetPasswordButton);

        headerPanel.add(actionPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Users table
        String[] columnNames = {"ID", "Tên đăng nhập", "Họ tên", "Email", "Vị trí", "Vai trò", "Ngày tham gia"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setFont(new Font("Arial", Font.PLAIN, 14));
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        userTable.setRowHeight(25);

        add(new JScrollPane(userTable), BorderLayout.CENTER);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (User user : users) {
            String roleDisplay;
            switch (user.getRole()) {
                case ADMIN:
                    roleDisplay = "Quản trị viên";
                    break;
                case USER:
                    roleDisplay = "Thành viên";
                    break;
                default:
                    roleDisplay = "Thành viên";
                    break;
            }
            
            Object[] row = {
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPosition(),
                roleDisplay,
                user.getCreatedAt().format(formatter)
            };
            tableModel.addRow(row);
        }
    }

    private void showAddUserDialog() {
        AddUserDialog dialog = new AddUserDialog(mainFrame);
        dialog.setVisible(true);
        
        User newUser = dialog.getUser();
        if (newUser != null) {
            User savedUser = userService.createUser(newUser);
            if (savedUser != null) {
                loadUsers();
                JOptionPane.showMessageDialog(mainFrame,
                    "Thêm thành viên thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void showEditUserDialog() {
        Long userId = getSelectedUserId();
        if (userId == null) {
            JOptionPane.showMessageDialog(mainFrame,
                "Vui lòng chọn thành viên cần chỉnh sửa",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userService.getUserById(userId);
        if (user != null) {
            EditUserDialog dialog = new EditUserDialog(mainFrame, user);
            dialog.setVisible(true);
            
            User updatedUser = dialog.getUpdatedUser();
            if (updatedUser != null) {
                User savedUser = userService.updateUser(updatedUser);
                if (savedUser != null) {
                    loadUsers();
                    JOptionPane.showMessageDialog(mainFrame,
                        "Cập nhật thành viên thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                        "Không thể cập nhật thông tin thành viên",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleDeleteUser() {
        Long userId = getSelectedUserId();
        if (userId == null) {
            JOptionPane.showMessageDialog(mainFrame,
                "Vui lòng chọn thành viên cần xóa",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(mainFrame,
            "Bạn có chắc chắn muốn xóa thành viên này?\nHành động này không thể hoàn tác!",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userService.deleteUser(userId);
            if (success) {
                loadUsers();
                JOptionPane.showMessageDialog(mainFrame,
                    "Xóa thành viên thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                    "Không thể xóa thành viên. Vui lòng thử lại sau.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Long getSelectedUserId() {
        int selectedRow = userTable.getSelectedRow();
        return selectedRow != -1 ? (Long) tableModel.getValueAt(selectedRow, 0) : null;
    }

    private void handleResetPassword() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainFrame,
                "Vui lòng chọn thành viên cần reset mật khẩu",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long userId = (Long) tableModel.getValueAt(selectedRow, 0);
        String userName = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(mainFrame,
            "Bạn có chắc chắn muốn reset mật khẩu của thành viên " + userName + "?",
            "Xác nhận reset mật khẩu",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userService.resetPassword(userId, "123");
            if (success) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Đã reset mật khẩu của thành viên " + userName + " về '123'",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                    "Không thể reset mật khẩu. Vui lòng thử lại sau",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 