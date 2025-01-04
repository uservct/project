package com.nhom6.taskmanagement.view.api;

import com.nhom6.taskmanagement.config.AppConfig;
import com.nhom6.taskmanagement.model.Attachment;
import com.nhom6.taskmanagement.service.AttachmentService;

import javax.swing.*;

import java.awt.*;
import java.util.List;

import com.nhom6.taskmanagement.config.ServiceManager;

public class AttachmentApiView extends JFrame {
    private AttachmentService attachmentService;

    public AttachmentApiView() {
        this.attachmentService = ServiceManager.getAttachmentService();
        initUI();
    }

    private void initUI() {
        setTitle("API Tệp đính kèm");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        List<Attachment> attachments = attachmentService.getAllAttachments();
        String[] columnNames = {"ID", "Tên tệp", "Người dùng"};
        Object[][] data = new Object[attachments.size()][3];

        for (int i = 0; i < attachments.size(); i++) {
            Attachment attachment = attachments.get(i);
            data[i][0] = attachment.getId();
            data[i][1] = attachment.getFileName();
            data[i][2] = attachment.getUser().getName();
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AttachmentApiView attachmentApiView = new AttachmentApiView();
            attachmentApiView.setVisible(true);
        });
    }
}