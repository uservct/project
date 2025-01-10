package com.nhom6.taskmanagement.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

import com.nhom6.taskmanagement.dao.AttachmentDAO;
import com.nhom6.taskmanagement.model.Attachment;

public class AttachmentService {
    private final AttachmentDAO attachmentDAO;
    private final String uploadDirectory = "uploads";

    public AttachmentService() {
        this.attachmentDAO = new AttachmentDAO();
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        File directory = new File(uploadDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public List<Attachment> getAttachmentsByProject(Long projectId) {
        return attachmentDAO.findByProjectId(projectId);
    }

    public List<Attachment> getAttachmentsByTask(Long taskId) {
        return attachmentDAO.findByTaskId(taskId);
    }

    public Attachment uploadFile(File file, Long projectId, Long taskId) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("File không tồn tại");
        }

        if (file.length() > 100 * 1024 * 1024) { // Giới hạn 100MB
            throw new IOException("File quá lớn. Kích thước tối đa là 100MB");
        }

        // Tạo tên file duy nhất để tránh trùng lặp
        String originalFileName = file.getName();
        String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;
        Path targetPath = Paths.get(uploadDirectory, uniqueFileName);

        // Copy file vào thư mục uploads
        try {
            Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Không thể copy file: " + e.getMessage());
        }

        // Tạo record trong database
        Attachment attachment = new Attachment();
        attachment.setProjectId(projectId);
        attachment.setTaskId(taskId);
        attachment.setFileName(originalFileName);
        attachment.setFilePath(targetPath.toString());
        attachment.setFileSize(file.length());
        attachment.setCreatedBy(UserService.getCurrentUser().getId());
        attachment.setCreatedAt(LocalDateTime.now());
        attachment.setIsDeleted(false);

        return attachmentDAO.save(attachment);
    }

    public void downloadFile(Attachment attachment, File targetFile) throws IOException {
        if (attachment == null) {
            throw new IOException("Attachment không tồn tại");
        }

        Path sourcePath = Paths.get(attachment.getFilePath());
        if (!Files.exists(sourcePath)) {
            throw new IOException("File không tồn tại trong hệ thống");
        }

        try {
            Files.copy(sourcePath, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Không thể tải xuống file: " + e.getMessage());
        }
    }

    public void deleteAttachment(Long attachmentId) {
        if (attachmentId == null) {
            return;
        }
        
        Attachment attachment = attachmentDAO.findById(attachmentId);
        if (attachment != null) {
            // Xóa file vật lý
            try {
                Files.deleteIfExists(Paths.get(attachment.getFilePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Xóa record trong database
            attachmentDAO.delete(attachmentId);
        }
    }
} 