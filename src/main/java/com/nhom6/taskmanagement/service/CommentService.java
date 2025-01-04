package com.nhom6.taskmanagement.service;

import java.util.List;

import com.nhom6.taskmanagement.model.Comment;
import com.nhom6.taskmanagement.repository.CommentRepository;
import com.nhom6.taskmanagement.repository.impl.CommentRepositoryImpl;

public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService() {
        this.commentRepository = new CommentRepositoryImpl();
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public void createComment(Comment comment) {
        commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}