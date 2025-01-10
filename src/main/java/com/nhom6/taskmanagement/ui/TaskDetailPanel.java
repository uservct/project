package com.nhom6.taskmanagement.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import com.nhom6.taskmanagement.model.Project;
import com.nhom6.taskmanagement.model.Task;
import com.nhom6.taskmanagement.model.TaskAssignee;
import com.nhom6.taskmanagement.model.TaskStatus;
import com.nhom6.taskmanagement.model.User;
import com.nhom6.taskmanagement.model.UserRole;
import com.nhom6.taskmanagement.service.ProjectService;
import com.nhom6.taskmanagement.service.TaskAssigneeService;
import com.nhom6.taskmanagement.service.TaskService;
import com.nhom6.taskmanagement.service.UserService;
import com.nhom6.taskmanagement.ui.dialog.AddTaskMemberDialog;
import com.nhom6.taskmanagement.ui.dialog.EditTaskDialog;

public class TaskDetailPanel extends JPanel {
    private final JFrame mainFrame;
    private final Task task;
    private final TaskService taskService;
    private final ProjectService projectService;
    private final TaskAssigneeService taskAssigneeService;
    private final UserService userService;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TaskDetailPanel(JFrame mainFrame, Task task) {
        this.mainFrame = mainFrame;
        this.task = task;
        this.taskService = new TaskService();
        this.projectService = new ProjectService();
        this.taskAssigneeService = new TaskAssigneeService();
        this.userService = new UserService();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        // Back button
        JButton backButton = new JButton("← Quay lại");
        backButton.setFont(BUTTON_FONT);
        backButton.addActionListener(e -> showTaskList());
        headerPanel.add(backButton, BorderLayout.WEST);

        // Title
        JLabel titleLabel = new JLabel(task.getName());
        titleLabel.setFont(TITLE_FONT);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Action Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Chỉ admin mới có quyền sửa/xóa
        if (UserService.getCurrentUser().getRole() == UserRole.ADMIN) {
            JButton editButton = new JButton("Chỉnh sửa");
            editButton.setFont(BUTTON_FONT);
            editButton.addActionListener(e -> showEditTaskDialog());
            actionPanel.add(editButton);

            JButton deleteButton = new JButton("Xóa");
            deleteButton.setFont(BUTTON_FONT);
            deleteButton.addActionListener(e -> handleDeleteTask());
            actionPanel.add(deleteButton);
        }

        // Thành viên chỉ có thể thay đổi trạng thái công việc được giao
        if (taskService.isTaskAssignee(task.getId(), UserService.getCurrentUser().getId())) {
            JComboBox<TaskStatus> statusComboBox = new JComboBox<>(TaskStatus.values());
            TaskAssignee currentAssignee = taskAssigneeService.getAssigneesByTask(task.getId()).stream()
                .filter(a -> a.getUserId().equals(UserService.getCurrentUser().getId()))
                .findFirst()
                .orElse(null);
                
            if (currentAssignee != null) {
                statusComboBox.setSelectedItem(currentAssignee.getStatus());
                statusComboBox.addActionListener(e -> {
                    TaskStatus newStatus = (TaskStatus) statusComboBox.getSelectedItem();
                    taskAssigneeService.updateStatus(task.getId(), UserService.getCurrentUser().getId(), newStatus);
                    refreshPanel();
                });
                actionPanel.add(statusComboBox);
            }
        }

        headerPanel.add(actionPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Task Info Panel với chiều rộng giới hạn
        JPanel infoWrapperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        infoPanel.setPreferredSize(new Dimension(400, 150)); // Giới hạn kích thước panel
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 10);
        
        int row = 0;
        addInfoField(infoPanel, "Dự án:", getProjectName(task.getProjectId()), gbc, row++);
        addInfoField(infoPanel, "Trạng thái:", formatStatus(task.getStatus()), gbc, row++);
        addInfoField(infoPanel, "Ngày bắt đầu:", task.getStartDate().format(DATE_FORMATTER), gbc, row++);
        addInfoField(infoPanel, "Ngày kết thúc:", task.getDueDate().format(DATE_FORMATTER), gbc, row++);
        
        infoWrapperPanel.add(infoPanel);
        contentPanel.add(infoWrapperPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Description
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBorder(BorderFactory.createTitledBorder("Mô tả"));
        JTextArea descArea = new JTextArea(task.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descPanel.add(new JScrollPane(descArea), BorderLayout.CENTER);
        contentPanel.add(descPanel);

        // Tạo tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab Thông tin
        tabbedPane.addTab("Thông tin", new JScrollPane(contentPanel));
        
        // Tab Thành viên
        JPanel membersPanel = createMembersPanel();
        tabbedPane.addTab("Thành viên", membersPanel);
        
        // Tab File đính kèm
        AttachmentPanel attachmentPanel = new AttachmentPanel(mainFrame, null, task.getId());
        tabbedPane.addTab("File đính kèm", attachmentPanel);
        
        // Tab Bình luận
        CommentPanel commentPanel = new CommentPanel(mainFrame, null, task.getId());
        tabbedPane.addTab("Bình luận", commentPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Bảng thành viên
        String[] columnNames = {"ID", "Họ tên", "Email", "Trạng thái công việc"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable memberTable = new JTable(tableModel);
        
        // Panel chức năng
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        if (UserService.getCurrentUser().getRole() == UserRole.ADMIN) {
            JButton addButton = new JButton("Thêm thành viên");
            JButton removeButton = new JButton("Xóa thành viên");
            
            addButton.addActionListener(e -> showAddMemberDialog());
            removeButton.addActionListener(e -> handleRemoveMember(memberTable));
            
            actionPanel.add(addButton);
            actionPanel.add(removeButton);
        }

        panel.add(actionPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(memberTable), BorderLayout.CENTER);

        // Load danh sách thành viên
        loadMembers(tableModel);

        return panel;
    }

    private void loadMembers(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        List<TaskAssignee> assignees = taskAssigneeService.getAssigneesByTask(task.getId());
        
        for (TaskAssignee assignee : assignees) {
            User user = userService.getUserById(assignee.getUserId());
            if (user != null) {
                Object[] row = {
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    formatStatus(assignee.getStatus())
                };
                tableModel.addRow(row);
            }
        }
    }

    private String formatStatus(TaskStatus status) {
        if (status == null) return "";
        
        switch (status) {
            case TODO:
                return "Chưa thực hiện";
            case IN_PROGRESS:
                return "Đang thực hiện";
            case REVIEW:
                return "Đang xem xét";
            case DONE:
                return "Hoàn thành";
            default:
                return "";
        }
    }

    private String getProjectName(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        return project != null ? project.getName() : "";
    }

    private void showAddMemberDialog() {
        AddTaskMemberDialog dialog = new AddTaskMemberDialog(mainFrame, task);
        dialog.setVisible(true);
        
        User selectedUser = dialog.getSelectedUser();
        if (selectedUser != null) {
            TaskAssignee assignee = taskAssigneeService.addAssignee(task.getId(), selectedUser.getId());
            if (assignee != null) {
                // Refresh panel
                Container parent = getParent();
                while (parent != null && !(parent instanceof DashboardPanel)) {
                    parent = parent.getParent();
                }
                
                if (parent instanceof DashboardPanel) {
                    DashboardPanel dashboard = (DashboardPanel) parent;
                    dashboard.showContent(new TaskDetailPanel(mainFrame, task));
                }
                
                JOptionPane.showMessageDialog(mainFrame,
                    "Thêm thành viên thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void handleRemoveMember(JTable memberTable) {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow == -1) return;

        Long userId = (Long) memberTable.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
            "Bạn có chắc chắn muốn xóa thành viên này khỏi công việc?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            taskAssigneeService.removeAssignee(task.getId(), userId);
            
            // Refresh panel
            Container parent = getParent();
            while (parent != null && !(parent instanceof DashboardPanel)) {
                parent = parent.getParent();
            }
            
            if (parent instanceof DashboardPanel) {
                DashboardPanel dashboard = (DashboardPanel) parent;
                dashboard.showContent(new TaskDetailPanel(mainFrame, task));
            }
            
            JOptionPane.showMessageDialog(mainFrame,
                "Xóa thành viên thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addInfoField(JPanel panel, String label, String value, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        
        // Label
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(LABEL_FONT);
        panel.add(labelComponent, gbc);
        
        // Value - giới hạn chiều rộng của phần giá trị
        gbc.gridx = 1;
        gbc.weightx = 1;
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(CONTENT_FONT);
        valueComponent.setPreferredSize(new Dimension(250, 25)); // Giới hạn chiều rộng của text
        panel.add(valueComponent, gbc);
    }

    private void showTaskList() {
        // Tìm DashboardPanel cha
        Container parent = getParent();
        while (parent != null && !(parent instanceof DashboardPanel)) {
            parent = parent.getParent();
        }
        
        // Cập nhật content thông qua DashboardPanel
        if (parent instanceof DashboardPanel) {
            DashboardPanel dashboard = (DashboardPanel) parent;
            dashboard.showContent(new TaskListPanel(mainFrame, 
                UserService.getCurrentUser().getRole() == UserRole.ADMIN));
        }
    }

    private void showEditTaskDialog() {
        EditTaskDialog dialog = new EditTaskDialog(mainFrame, task);
        dialog.setVisible(true);
        
        Task updatedTask = dialog.getUpdatedTask();
        if (updatedTask != null) {
            Task savedTask = taskService.updateTask(updatedTask);
            if (savedTask != null) {
                // Tìm DashboardPanel cha
                Container parent = getParent();
                while (parent != null && !(parent instanceof DashboardPanel)) {
                    parent = parent.getParent();
                }
                
                // Cập nhật content thông qua DashboardPanel
                if (parent instanceof DashboardPanel) {
                    DashboardPanel dashboard = (DashboardPanel) parent;
                    dashboard.showContent(new TaskDetailPanel(mainFrame, savedTask));
                }
                
                JOptionPane.showMessageDialog(mainFrame,
                    "Cập nhật công việc thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void handleDeleteTask() {
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
            "Bạn có chắc chắn muốn xóa công việc này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            taskService.deleteTask(task.getId());
            showTaskList();
            JOptionPane.showMessageDialog(mainFrame,
                "Xóa công việc thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateTaskStatus(TaskStatus newStatus) {
        task.setStatus(newStatus);
        task.setUpdatedAt(LocalDateTime.now());
        Task updatedTask = taskService.updateTask(task);
        
        if (updatedTask != null) {
            // Tìm DashboardPanel cha
            Container parent = getParent();
            while (parent != null && !(parent instanceof DashboardPanel)) {
                parent = parent.getParent();
            }
            
            // Cập nhật content thông qua DashboardPanel
            if (parent instanceof DashboardPanel) {
                DashboardPanel dashboard = (DashboardPanel) parent;
                dashboard.showContent(new TaskDetailPanel(mainFrame, updatedTask));
            }
            
            JOptionPane.showMessageDialog(mainFrame,
                "Cập nhật trạng thái công việc thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshPanel() {
        // Tìm DashboardPanel cha
        Container parent = getParent();
        while (parent != null && !(parent instanceof DashboardPanel)) {
            parent = parent.getParent();
        }
        
        // Cập nhật content thông qua DashboardPanel
        if (parent instanceof DashboardPanel) {
            DashboardPanel dashboard = (DashboardPanel) parent;
            dashboard.showContent(new TaskDetailPanel(mainFrame, task));
        }
    }
} 