package com.nhom6.taskmanagement.service;

import java.util.List;

import com.nhom6.taskmanagement.model.Task;
import com.nhom6.taskmanagement.repository.TaskRepository;
import com.nhom6.taskmanagement.repository.impl.TaskRepositoryImpl;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService() {
        this.taskRepository = new TaskRepositoryImpl();
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public void createTask(Task task) {
        taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}