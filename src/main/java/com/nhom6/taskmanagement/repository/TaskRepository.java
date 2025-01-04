package com.nhom6.taskmanagement.repository;

import java.util.List;

import com.nhom6.taskmanagement.model.Task;

public interface TaskRepository {
    List<Task> findAll();
    Task findById(Long id);
    void save(Task task);
    void deleteById(Long id);
}