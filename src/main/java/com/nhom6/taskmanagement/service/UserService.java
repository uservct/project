package com.nhom6.taskmanagement.service;

import java.util.List;

import com.nhom6.taskmanagement.model.User;
import com.nhom6.taskmanagement.repository.UserRepository;
import com.nhom6.taskmanagement.repository.impl.UserRepositoryImpl;

public class UserService {
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepositoryImpl();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}