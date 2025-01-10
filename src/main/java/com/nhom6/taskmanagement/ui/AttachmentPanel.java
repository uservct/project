package com.nhom6.taskmanagement.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.nhom6.taskmanagement.model.Attachment;
import com.nhom6.taskmanagement.model.User;
import com.nhom6.taskmanagement.service.AttachmentService;
import com.nhom6.taskmanagement.service.UserService;

public class AttachmentPanel extends JPanel {
    private final JFrame mainFrame;
    private final Long projectId;
    private final Long taskId;
    private final AttachmentService attachmentService;
    private JTable attachmentTable;
    private DefaultTableModel tableModel;

    public AttachmentPanel(JFrame mainFrame, Long projectId, Long taskId) {
        this.mainFrame = mainFrame;
        this.projectId = projectId;
        this.taskId = taskId;
        this.attachmentService = new AttachmentService();
        initComponents();
        loadAttachments();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Action Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton uploadButton = new JButton("Tải lên");
        JButton downloadButton = new JButton("Tải xuống");
        JButton deleteButton = new JButton("Xóa");

        uploadButton.addActionListener(e -> handleUpload());
        downloadButton.addActionListener(e -> handleDownload());
        deleteButton.addActionListener(e -> handleDelete());

        actionPanel.add(uploadButton);
        actionPanel.add(downloadButton);
        actionPanel.add(deleteButton);

        add(actionPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "Tên file", "Kích thước", "Người tải lên", "Ngày tải"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        attachmentTable = new JTable(tableModel);
        attachmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(attachmentTable), BorderLayout.CENTER);

        // Load attachments
        loadAttachments();
    }

    private void loadAttachments() {
        tableModel.setRowCount(0);
        List<Attachment> attachments;
        UserService userService = new UserService();
        
        if (taskId != null) {
            attachments = attachmentService.getAttachmentsByTask(taskId);
        } else {
            attachments = attachmentService.getAttachmentsByProject(projectId);
        }
        
        for (Attachment attachment : attachments) {
            User uploader = userService.getUserById(attachment.getCreatedBy());
            String uploaderName = uploader != null ? uploader.getFullName() : "Unknown";
            
            Object[] row = {
                attachment.getId(),
                attachment.getFileName(),
                formatFileSize(attachment.getFileSize()),
                uploaderName,
                attachment.getCreatedAt()
            };
            tableModel.addRow(row);
        }
    }

    private String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return String.format("%.1f %s", size/Math.pow(1024, digitGroups), units[digitGroups]);
    }

    private void handleUpload() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                Attachment attachment = attachmentService.uploadFile(selectedFile, projectId, taskId);
                if (attachment != null) {
                    loadAttachments(); // Refresh table
                    JOptionPane.showMessageDialog(mainFrame,
                        "Tải lên file thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Lỗi khi tải lên file: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDownload() {
        int selectedRow = attachmentTable.getSelectedRow();
        if (selectedRow == -1) return;

        Long attachmentId = (Long) tableModel.getValueAt(selectedRow, 0);
        String fileName = (String) tableModel.getValueAt(selectedRow, 1);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(fileName));
        int result = fileChooser.showSaveDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File targetFile = fileChooser.getSelectedFile();
            try {
                List<Attachment> attachments = taskId != null ? 
                    attachmentService.getAttachmentsByTask(taskId) :
                    attachmentService.getAttachmentsByProject(projectId);
                
                Attachment attachment = attachments.stream()
                    .filter(a -> a.getId().equals(attachmentId))
                    .findFirst()
                    .orElse(null);
                
                if (attachment != null) {
                    attachmentService.downloadFile(attachment, targetFile);
                    JOptionPane.showMessageDialog(mainFrame,
                        "Tải xuống file thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Lỗi khi tải xuống file: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDelete() {
        int selectedRow = attachmentTable.getSelectedRow();
        if (selectedRow == -1) return;

        Long attachmentId = (Long) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
            "Bạn có chắc chắn muốn xóa file này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            attachmentService.deleteAttachment(attachmentId);
            loadAttachments(); // Refresh table
            JOptionPane.showMessageDialog(mainFrame,
                "Xóa file thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
} 