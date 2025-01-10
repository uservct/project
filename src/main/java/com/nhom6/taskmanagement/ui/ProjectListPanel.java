package com.nhom6.taskmanagement.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.nhom6.taskmanagement.model.Project;
import com.nhom6.taskmanagement.model.ProjectPriority;
import com.nhom6.taskmanagement.model.ProjectStatus;
import com.nhom6.taskmanagement.service.ProjectService;
import com.nhom6.taskmanagement.service.UserService;
import com.nhom6.taskmanagement.ui.dialog.AddProjectDialog;

public class ProjectListPanel extends JPanel {
    private final JFrame mainFrame;
    private final ProjectService projectService;
    private JTable projectTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<ProjectStatus> statusFilter;
    private JComboBox<ProjectPriority> priorityFilter;
    private List<Project> allProjects;
    private final boolean isAdmin;
    private final Long currentUserId;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font TABLE_HEADER_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font TABLE_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    public ProjectListPanel(JFrame mainFrame, boolean isAdmin) {
        this.mainFrame = mainFrame;
        this.projectService = new ProjectService();
        this.isAdmin = isAdmin;
        this.currentUserId = UserService.getCurrentUser().getId();
        initComponents();
        loadProjects();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tạo panel chứa cả header và filter
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Header
        JLabel titleLabel = new JLabel("Danh sách dự án");
        titleLabel.setFont(HEADER_FONT);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Add project button - chỉ hiện khi là admin
        if (isAdmin) {
            JButton addButton = new JButton("Thêm dự án mới");
            addButton.addActionListener(e -> showAddProjectDialog());
            topPanel.add(addButton, BorderLayout.EAST);
        }

        // Search and Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Search field
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterProjects(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterProjects(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterProjects(); }
        });
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);

        // Status filter
        JLabel statusLabel = new JLabel("Trạng thái:");
        statusFilter = new JComboBox<>(ProjectStatus.values());
        statusFilter.insertItemAt(null, 0);
        statusFilter.setSelectedIndex(0);
        statusFilter.addActionListener(e -> filterProjects());
        filterPanel.add(statusLabel);
        filterPanel.add(statusFilter);

        // Priority filter
        JLabel priorityLabel = new JLabel("Mức độ ưu tiên:");
        priorityFilter = new JComboBox<>(ProjectPriority.values());
        priorityFilter.insertItemAt(null, 0);
        priorityFilter.setSelectedIndex(0);
        priorityFilter.addActionListener(e -> filterProjects());
        filterPanel.add(priorityLabel);
        filterPanel.add(priorityFilter);

        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        // Thêm topPanel vào vị trí NORTH thay vì CENTER
        add(topPanel, BorderLayout.NORTH);
        
        // Project table
        String[] columnNames = {"ID", "Tên dự án", "Trạng thái", "Ngày bắt đầu", "Ngày kết thúc", "Độ ưu tiên"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        projectTable = new JTable(tableModel);
        projectTable.setFont(TABLE_FONT);
        projectTable.getTableHeader().setFont(TABLE_HEADER_FONT);
        projectTable.setRowHeight(35);
        
        projectTable.getColumnModel().getColumns().asIterator().forEachRemaining(column -> {
            column.setPreferredWidth(150);
        });

        projectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Thêm renderer cho cột priority
        projectTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(TABLE_FONT);
                
                if (value != null) {
                    switch (value.toString()) {
                        case "Khẩn cấp":
                            c.setForeground(Color.RED);
                            break;
                        case "Cao":
                            c.setForeground(new Color(255, 69, 0)); // Orange Red
                            break;
                        case "Trung bình":
                            c.setForeground(new Color(255, 140, 0)); // Dark Orange
                            break;
                        case "Thấp":
                            c.setForeground(new Color(0, 128, 0)); // Green
                            break;
                        default:
                            c.setForeground(table.getForeground());
                    }
                }
                return c;
            }
        });

        projectTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = projectTable.getSelectedRow();
                    if (row != -1) {
                        Long projectId = (Long) tableModel.getValueAt(row, 0);
                        showProjectDetails(projectId);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        // Chuyển table vào vị trí CENTER thay vì SOUTH
        add(new JScrollPane(projectTable), BorderLayout.CENTER);
    }

    private void loadProjects() {
        if (isAdmin) {
            allProjects = projectService.getAllProjects();
        } else {
            allProjects = projectService.getProjectsByMember(currentUserId);
        }
        filterProjects();
    }

    private void filterProjects() {
        String searchText = searchField.getText().toLowerCase().trim();
        ProjectStatus selectedStatus = (ProjectStatus) statusFilter.getSelectedItem();
        ProjectPriority selectedPriority = (ProjectPriority) priorityFilter.getSelectedItem();

        tableModel.setRowCount(0);
        
        for (Project project : allProjects) {
            // Kiểm tra điều kiện lọc
            boolean matchesSearch = project.getName().toLowerCase().contains(searchText);
            boolean matchesStatus = selectedStatus == null || project.getStatus() == selectedStatus;
            boolean matchesPriority = selectedPriority == null || project.getPriority() == selectedPriority;

            if (matchesSearch && matchesStatus && matchesPriority) {
                Object[] row = {
                    project.getId(),
                    project.getName(),
                    project.getStatus().toString(),
                    project.getStartDate().format(DATE_FORMATTER),
                    project.getDueDate().format(DATE_FORMATTER),
                    formatPriority(project.getPriority())
                };
                tableModel.addRow(row);
            }
        }
    }

    private void showAddProjectDialog() {
        AddProjectDialog dialog = new AddProjectDialog(mainFrame);
        dialog.setVisible(true);
        
        Project newProject = dialog.getProject();
        if (newProject != null) {
            Project savedProject = projectService.createProject(newProject);
            if (savedProject != null) {
                loadProjects(); // Refresh table
                JOptionPane.showMessageDialog(mainFrame,
                    "Thêm dự án thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void showProjectDetails(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        if (project != null) {
            // Tìm DashboardPanel cha
            Container parent = getParent();
            while (parent != null && !(parent instanceof DashboardPanel)) {
                parent = parent.getParent();
            }
            
            // Cập nhật content thông qua DashboardPanel
            if (parent instanceof DashboardPanel) {
                DashboardPanel dashboard = (DashboardPanel) parent;
                dashboard.showContent(new ProjectDetailPanel(mainFrame, project));
            }
        }
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