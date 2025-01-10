package com.nhom6.taskmanagement.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.nhom6.taskmanagement.model.Project;
import com.nhom6.taskmanagement.model.Task;
import com.nhom6.taskmanagement.model.TaskStatus;
import com.nhom6.taskmanagement.service.ProjectService;
import com.nhom6.taskmanagement.service.TaskService;
import com.nhom6.taskmanagement.service.UserService;
import com.nhom6.taskmanagement.ui.dialog.AddTaskDialog;

public class TaskListPanel extends JPanel {
    private final JFrame mainFrame;
    private final TaskService taskService;
    private final ProjectService projectService;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JComboBox<Project> projectComboBox;
    private final boolean isAdmin;
    private final Long currentUserId;
    private List<Task> tasks;
    private JTextField searchField;
    private JComboBox<TaskStatus> statusFilter;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font TABLE_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    public TaskListPanel(JFrame mainFrame, boolean isAdmin) {
        this.mainFrame = mainFrame;
        this.taskService = new TaskService();
        this.projectService = new ProjectService();
        this.isAdmin = isAdmin;
        this.currentUserId = UserService.getCurrentUser().getId();
        initComponents();
        loadTasks();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tạo panel chứa cả header và filter
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Header
        JLabel titleLabel = new JLabel("Danh sách công việc");
        titleLabel.setFont(HEADER_FONT);
        topPanel.add(titleLabel, BorderLayout.NORTH);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Tìm kiếm theo tên
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) { filterTasks(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterTasks(); }
            @Override
            public void insertUpdate(DocumentEvent e) { filterTasks(); }
        });
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);

        // Lọc theo trạng thái
        JLabel statusLabel = new JLabel("Trạng thái:");
        statusFilter = new JComboBox<>(TaskStatus.values());
        statusFilter.insertItemAt(null, 0);
        statusFilter.setSelectedIndex(0);
        statusFilter.addActionListener(e -> filterTasks());
        filterPanel.add(statusLabel);
        filterPanel.add(statusFilter);

        // Lọc theo dự án
        JLabel projectLabel = new JLabel("Dự án:");
        projectComboBox = new JComboBox<>();
        projectComboBox.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Tất cả dự án");
                } else if (value instanceof Project) {
                    setText(((Project) value).getName());
                }
                return this;
            }
        });
        projectComboBox.addActionListener(e -> filterTasks());
        filterPanel.add(projectLabel);
        filterPanel.add(projectComboBox);

        // Add Task Button - chỉ hiện khi là admin
        if (isAdmin) {
            JButton addButton = new JButton("Thêm công việc mới");
            addButton.addActionListener(e -> showAddTaskDialog());
            filterPanel.add(addButton);
        }

        topPanel.add(filterPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "Tên công việc", "Dự án", "Trạng thái", "Ngày bắt đầu", "Ngày kết thúc"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        taskTable = new JTable(tableModel);
        taskTable.setFont(TABLE_FONT);
        taskTable.getTableHeader().setFont(TABLE_FONT);
        taskTable.setRowHeight(30); // Tăng chiều cao hàng
        
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

        add(new JScrollPane(taskTable), BorderLayout.CENTER);

        // Load projects for combobox
        loadProjects();
    }

    private void loadProjects() {
        projectComboBox.removeAllItems();
        projectComboBox.addItem(null); // Thêm lựa chọn "Tất cả dự án"
        List<Project> projects = projectService.getAllProjects();
        for (Project project : projects) {
            projectComboBox.addItem(project);
        }
    }

    private void loadTasks() {
        if (isAdmin) {
            tasks = taskService.getAllTasks();
        } else {
            tasks = taskService.getTasksByAssignee(currentUserId);
        }
        filterTasks();
    }

    private void filterTasks() {
        tableModel.setRowCount(0);
        String searchText = searchField.getText().toLowerCase();
        TaskStatus selectedStatus = (TaskStatus) statusFilter.getSelectedItem();
        Project selectedProject = (Project) projectComboBox.getSelectedItem();
        
        for (Task task : tasks) {
            boolean matchesSearch = task.getName().toLowerCase().contains(searchText);
            boolean matchesStatus = selectedStatus == null || task.getStatus() == selectedStatus;
            boolean matchesProject = selectedProject == null || task.getProjectId().equals(selectedProject.getId());

            if (matchesSearch && matchesStatus && matchesProject) {
                Object[] row = {
                    task.getId(),
                    task.getName(),
                    getProjectName(task.getProjectId()),
                    formatStatus(task.getStatus()),
                    task.getStartDate().format(DATE_FORMATTER),
                    task.getDueDate().format(DATE_FORMATTER)
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

    private void showAddTaskDialog() {
        Project selectedProject = (Project) projectComboBox.getSelectedItem();
        Long projectId = selectedProject != null ? selectedProject.getId() : null;
        
        AddTaskDialog dialog = new AddTaskDialog(mainFrame, projectId);
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
} 