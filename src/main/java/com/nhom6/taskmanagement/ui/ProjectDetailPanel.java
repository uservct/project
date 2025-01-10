package com.nhom6.taskmanagement.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.nhom6.taskmanagement.model.Project;
import com.nhom6.taskmanagement.model.ProjectPriority;
import com.nhom6.taskmanagement.model.Task;
import com.nhom6.taskmanagement.model.TaskStatus;
import com.nhom6.taskmanagement.model.UserRole;
import com.nhom6.taskmanagement.service.ProjectService;
import com.nhom6.taskmanagement.service.TaskService;
import com.nhom6.taskmanagement.service.UserService;
import com.nhom6.taskmanagement.ui.dialog.AddTaskDialog;
import com.nhom6.taskmanagement.ui.dialog.EditProjectDialog;

public class ProjectDetailPanel extends JPanel {
    private final JFrame mainFrame;
    private final Project project;
    private final ProjectService projectService;
    private final TaskService taskService;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ProjectDetailPanel(JFrame mainFrame, Project project) {
        this.mainFrame = mainFrame;
        this.project = project;
        this.projectService = new ProjectService();
        this.taskService = new TaskService();
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
        backButton.addActionListener(e -> showProjectList());
        headerPanel.add(backButton, BorderLayout.WEST);

        // Title
        JLabel titleLabel = new JLabel(project.getName());
        titleLabel.setFont(TITLE_FONT);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (UserService.getCurrentUser().getRole() == UserRole.ADMIN) {
            JButton editButton = new JButton("Chỉnh sửa");
            editButton.setFont(BUTTON_FONT);
            editButton.addActionListener(e -> showEditProjectDialog());
            actionPanel.add(editButton);

            JButton deleteButton = new JButton("Xóa");
            deleteButton.setFont(BUTTON_FONT);
            deleteButton.addActionListener(e -> handleDeleteProject());
            actionPanel.add(deleteButton);
        }

        headerPanel.add(actionPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Project Info
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 10);
        
        int row = 0;
        addInfoField(infoPanel, "Trạng thái:", project.getStatus().toString(), gbc, row++);
        addInfoField(infoPanel, "Độ ưu tiên:", formatPriority(project.getPriority()), gbc, row++);
        addInfoField(infoPanel, "Ngày bắt đầu:", project.getStartDate().format(DATE_FORMATTER), gbc, row++);
        addInfoField(infoPanel, "Ngày kết thúc:", project.getDueDate().format(DATE_FORMATTER), gbc, row++);
        
        contentPanel.add(infoPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Description
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBorder(BorderFactory.createTitledBorder("Mô tả"));
        JTextArea descArea = new JTextArea(project.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descPanel.add(new JScrollPane(descArea), BorderLayout.CENTER);
        contentPanel.add(descPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Tasks
        JPanel tasksPanel = new JPanel(new BorderLayout());
        tasksPanel.setBorder(BorderFactory.createTitledBorder("Danh sách công việc"));
        
        // Add task button (chỉ hiện khi là admin)
        if (UserService.getCurrentUser().getRole() == UserRole.ADMIN) {
            JButton addTaskButton = new JButton("Thêm công việc");
            addTaskButton.setFont(BUTTON_FONT);
            addTaskButton.addActionListener(e -> showAddTaskDialog());
            tasksPanel.add(addTaskButton, BorderLayout.NORTH);
        }

        // Tasks table
        initTaskTable(); // Gọi phương thức khởi tạo table
        tasksPanel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        
        // Load tasks
        loadTasks();
        
        contentPanel.add(tasksPanel);

        // Tạo tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab Thông tin
        tabbedPane.addTab("Thông tin", new JScrollPane(contentPanel));
        
        // Tab Thành viên
        JPanel membersPanel = new ProjectMemberPanel(mainFrame, project);
        tabbedPane.addTab("Thành viên", membersPanel);
        
        // Tab File đính kèm
        AttachmentPanel attachmentPanel = new AttachmentPanel(mainFrame, project.getId(), null);
        tabbedPane.addTab("File đính kèm", attachmentPanel);
        
        // Tab Bình luận
        CommentPanel commentPanel = new CommentPanel(mainFrame, project.getId(), null);
        tabbedPane.addTab("Bình luận", commentPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void addInfoField(JPanel panel, String label, String value, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        
        // Label
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(LABEL_FONT);
        panel.add(labelComponent, gbc);
        
        // Value
        gbc.gridx = 1;
        gbc.weightx = 1;
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(CONTENT_FONT);
        panel.add(valueComponent, gbc);
    }

    private void showProjectList() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof DashboardPanel)) {
            parent = parent.getParent();
        }
        
        if (parent instanceof DashboardPanel) {
            DashboardPanel dashboard = (DashboardPanel) parent;
            dashboard.showContent(new ProjectListPanel(mainFrame, 
                UserService.getCurrentUser().getRole() == UserRole.ADMIN));
        }
    }

    private void showAddTaskDialog() {
        AddTaskDialog dialog = new AddTaskDialog(mainFrame, project.getId());
        dialog.setVisible(true);
        
        Task newTask = dialog.getTask();
        if (newTask != null) {
            Task savedTask = taskService.createTask(newTask);
            if (savedTask != null) {
                loadTasks(); // Refresh table
                JOptionPane.showMessageDialog(mainFrame,
                    "Thêm công việc thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void showEditProjectDialog() {
        EditProjectDialog dialog = new EditProjectDialog(mainFrame, project);
        dialog.setVisible(true);
        
        Project updatedProject = dialog.getUpdatedProject();
        if (updatedProject != null) {
            Project savedProject = projectService.updateProject(updatedProject);
            if (savedProject != null) {
                // Tìm DashboardPanel cha
                Container parent = getParent();
                while (parent != null && !(parent instanceof DashboardPanel)) {
                    parent = parent.getParent();
                }
                
                // Cập nhật content thông qua DashboardPanel
                if (parent instanceof DashboardPanel) {
                    DashboardPanel dashboard = (DashboardPanel) parent;
                    dashboard.showContent(new ProjectDetailPanel(mainFrame, savedProject));
                }
                
                JOptionPane.showMessageDialog(mainFrame,
                    "Cập nhật dự án thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void handleDeleteProject() {
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
            "Bạn có chắc chắn muốn xóa dự án này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            projectService.deleteProject(project.getId());
            showProjectList();
            JOptionPane.showMessageDialog(mainFrame,
                "Xóa dự án thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadTasks() {
        tableModel.setRowCount(0);
        List<Task> tasks = taskService.getTasksByProject(project.getId());
        
        for (Task task : tasks) {
            Object[] row = {
                task.getId(),
                task.getName(),
                formatStatus(task.getStatus()),
                task.getStartDate().format(DATE_FORMATTER),
                task.getDueDate().format(DATE_FORMATTER)
            };
            tableModel.addRow(row);
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

    private void initTaskTable() {
        String[] columnNames = {"ID", "Tên công việc", "Trạng thái", "Ngày bắt đầu", "Ngày kết thúc"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taskTable = new JTable(tableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Thêm sự kiện double click để mở TaskDetailPanel
        taskTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Long taskId = getSelectedTaskId();
                    if (taskId != null) {
                        showTaskDetails(taskId);
                    }
                }
            }
        });
    }

    private void showTaskDetails(Long taskId) {
        Task task = taskService.getTaskById(taskId);
        if (task != null) {
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

    private Long getSelectedTaskId() {
        int selectedRow = taskTable.getSelectedRow();
        return selectedRow != -1 ? (Long) tableModel.getValueAt(selectedRow, 0) : null;
    }

    private String formatPriority(ProjectPriority priority) {
        switch (priority) {
            case LOW:
                return "Thấp";
            case MEDIUM:
                return "Trung bình";
            case HIGH:
                return "Cao";
            case URGENT:
                return "Khẩn cấp";
            default:
                return "";
        }
    }
} 