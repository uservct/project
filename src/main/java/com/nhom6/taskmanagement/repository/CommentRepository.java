package com.nhom6.taskmanagement.repository;

import java.util.List;

import com.nhom6.taskmanagement.model.Comment;

public interface CommentRepository {
    List<Comment> findAll();
    Comment findById(Long id);
    void save(Comment comment);
    void deleteById(Long id);
}