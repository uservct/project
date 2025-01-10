package com.nhom6.taskmanagement.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.nhom6.taskmanagement.dao.TaskDAO;
import com.nhom6.taskmanagement.model.Project;
import com.nhom6.taskmanagement.model.Task;
import com.nhom6.taskmanagement.model.TaskStatus;

public class TaskService {
    private final TaskDAO taskDAO;
    private final ProjectService projectService;

    public TaskService() {
        this.taskDAO = new TaskDAO();
        this.projectService = new ProjectService();
    }

    public List<Task> getTasksByProject(Long projectId) {
        return taskDAO.findByProjectId(projectId);
    }

    public Task getTaskById(Long id) {
        return taskDAO.findById(id);
    }

    public Task createTask(Task task) {
        // Set default values
        task.setCreatedBy(UserService.getCurrentUser().getId());
        task.setCreatedAt(LocalDateTime.now());
        task.setStatus(TaskStatus.TODO);
        task.setIsDeleted(false);
        
        return taskDAO.save(task);
    }

    public void updateTaskStatus(Long taskId, TaskStatus newStatus) {
        Task task = taskDAO.findById(taskId);
        if (task != null) {
            task.setStatus(newStatus);
            task.setUpdatedAt(LocalDateTime.now());
            taskDAO.save(task);
        }
    }

    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        List<Project> projects = projectService.getAllProjects();
        for (Project project : projects) {
            allTasks.addAll(taskDAO.findByProjectId(project.getId()));
        }
        return allTasks;
    }

    public Task updateTask(Task task) {
        task.setUpdatedAt(LocalDateTime.now());
        return taskDAO.update(task);
    }

    public void deleteTask(Long taskId) {
        Task task = taskDAO.findById(taskId);
        if (task != null) {
            task.setIsDeleted(true);
            task.setDeletedAt(LocalDateTime.now());
            task.setDeletedBy(UserService.getCurrentUser().getId());
            taskDAO.update(task);
        }
    }

    public List<Task> getTasksByAssignee(Long userId) {
        return taskDAO.findByAssigneeId(userId);
    }

    public boolean isTaskAssignee(Long taskId, Long userId) {
        return taskDAO.isTaskAssignee(taskId, userId);
    }
} 