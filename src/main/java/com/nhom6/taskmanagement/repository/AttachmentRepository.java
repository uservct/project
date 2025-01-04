package com.nhom6.taskmanagement.repository;

import java.util.List;

import com.nhom6.taskmanagement.model.Attachment;

public interface AttachmentRepository {
    List<Attachment> findAll();
    Attachment findById(Long id);
    void save(Attachment attachment);
    void deleteById(Long id);
}