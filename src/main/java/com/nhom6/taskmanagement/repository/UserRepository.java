package com.nhom6.taskmanagement.repository;

import java.util.List;

import com.nhom6.taskmanagement.model.User;

public interface UserRepository {
    List<User> findAll();
    User findById(Long id);
    void save(User user);
    void deleteById(Long id);
}