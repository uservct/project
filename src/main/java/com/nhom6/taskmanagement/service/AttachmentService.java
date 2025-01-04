package com.nhom6.taskmanagement.service;

import java.util.List;

import com.nhom6.taskmanagement.model.Attachment;
import com.nhom6.taskmanagement.repository.AttachmentRepository;
import com.nhom6.taskmanagement.repository.impl.AttachmentRepositoryImpl;

public class AttachmentService {
    private final AttachmentRepository attachmentRepository;

    public AttachmentService() {
        this.attachmentRepository = new AttachmentRepositoryImpl();
    }

    public List<Attachment> getAllAttachments() {
        return attachmentRepository.findAll();
    }

    public Attachment getAttachmentById(Long id) {
        return attachmentRepository.findById(id);
    }

    public void createAttachment(Attachment attachment) {
        attachmentRepository.save(attachment);
    }

    public void deleteAttachment(Long id) {
        attachmentRepository.deleteById(id);
    }
}