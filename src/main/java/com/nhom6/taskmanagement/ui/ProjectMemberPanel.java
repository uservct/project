package com.nhom6.taskmanagement.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.nhom6.taskmanagement.model.Project;
import com.nhom6.taskmanagement.model.ProjectMember;
import com.nhom6.taskmanagement.model.ProjectRole;
import com.nhom6.taskmanagement.model.User;
import com.nhom6.taskmanagement.service.ProjectMemberService;
import com.nhom6.taskmanagement.service.UserService;
import com.nhom6.taskmanagement.ui.dialog.AddProjectMemberDialog;
import com.nhom6.taskmanagement.ui.dialog.EditProjectMemberDialog;

public class ProjectMemberPanel extends JPanel {
    private final JFrame mainFrame;
    private final Project project;
    private final ProjectMemberService projectMemberService;
    private final UserService userService;
    private JTable memberTable;
    private DefaultTableModel tableModel;

    public ProjectMemberPanel(JFrame mainFrame, Project project) {
        this.mainFrame = mainFrame;
        this.project = project;
        this.projectMemberService = new ProjectMemberService();
        this.userService = new UserService();
        initComponents();
        loadMembers();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Thành viên dự án");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add member button
        JButton addButton = new JButton("Thêm thành viên");
        addButton.addActionListener(e -> showAddMemberDialog());
        headerPanel.add(addButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "Tên thành viên", "Email", "Vai trò"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Thêm menu popup khi click chuột phải
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editRoleItem = new JMenuItem("Thay đổi vai trò");
        JMenuItem removeMemberItem = new JMenuItem("Xóa khỏi dự án");

        editRoleItem.addActionListener(e -> showEditRoleDialog());
        removeMemberItem.addActionListener(e -> handleRemoveMember());

        popupMenu.add(editRoleItem);
        popupMenu.add(removeMemberItem);

        memberTable.setComponentPopupMenu(popupMenu);
        add(new JScrollPane(memberTable), BorderLayout.CENTER);
    }

    private void loadMembers() {
        tableModel.setRowCount(0);
        List<ProjectMember> members = projectMemberService.getMembersByProject(project.getId());
        
        for (ProjectMember member : members) {
            User user = userService.getUserById(member.getUserId());
            if (user != null) {
                Object[] row = {
                    member.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    member.getRole().getDisplayName(),
                    member.getJoinedAt().toLocalDate()
                };
                tableModel.addRow(row);
            }
        }
    }

    private void showAddMemberDialog() {
        AddProjectMemberDialog dialog = new AddProjectMemberDialog(mainFrame, project);
        dialog.setVisible(true);
        
        if (dialog.getSelectedUser() != null) {
            ProjectMember newMember = projectMemberService.addMember(
                project.getId(),
                dialog.getSelectedUser().getId(),
                dialog.getSelectedRole()
            );
            
            if (newMember != null) {
                loadMembers(); // Refresh table
                JOptionPane.showMessageDialog(mainFrame,
                    "Thêm thành viên thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void showEditRoleDialog() {
        Long memberId = getSelectedMemberId();
        if (memberId == null) return;

        ProjectMember member = projectMemberService.getMemberById(memberId);
        if (member != null) {
            EditProjectMemberDialog dialog = new EditProjectMemberDialog(mainFrame, member);
            dialog.setVisible(true);
            
            if (dialog.getSelectedRole() != null) {
                ProjectMember updatedMember = projectMemberService.updateMemberRole(
                    memberId,
                    dialog.getSelectedRole()
                );
                
                if (updatedMember != null) {
                    loadMembers(); // Refresh table
                    JOptionPane.showMessageDialog(mainFrame,
                        "Cập nhật vai trò thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    private void handleRemoveMember() {
        // Kiểm tra quyền xóa thành viên
        User currentUser = UserService.getCurrentUser();
        ProjectRole userRole = projectMemberService.getMemberRole(project.getId(), currentUser.getId());
        
        if (userRole != ProjectRole.MANAGER && userRole != ProjectRole.OWNER) {
            JOptionPane.showMessageDialog(this,
                "Bạn không có quyền xóa thành viên",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn thành viên cần xóa",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long memberId = (Long) tableModel.getValueAt(selectedRow, 0);
        String memberName = (String) tableModel.getValueAt(selectedRow, 1);

        // Kiểm tra không cho phép xóa chủ dự án
        ProjectMember member = projectMemberService.getMemberById(memberId);
        if (member != null && member.getRole() == ProjectRole.OWNER) {
            JOptionPane.showMessageDialog(this,
                "Không thể xóa chủ dự án",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(mainFrame,
            "Bạn có chắc chắn muốn xóa thành viên " + memberName + " khỏi dự án?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            projectMemberService.removeMember(memberId);
            loadMembers(); // Tải lại danh sách sau khi xóa
            JOptionPane.showMessageDialog(mainFrame,
                "Đã xóa thành viên khỏi dự án thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private Long getSelectedMemberId() {
        int selectedRow = memberTable.getSelectedRow();
        return selectedRow != -1 ? (Long) tableModel.getValueAt(selectedRow, 0) : null;
    }
} 