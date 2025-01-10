package com.nhom6.taskmanagement.service;

import java.time.LocalDateTime;
import java.util.List;

import com.nhom6.taskmanagement.dao.TaskAssigneeDAO;
import com.nhom6.taskmanagement.model.TaskAssignee;
import com.nhom6.taskmanagement.model.TaskStatus;

public class TaskAssigneeService {
    private final TaskAssigneeDAO taskAssigneeDAO;

    public TaskAssigneeService() {
        this.taskAssigneeDAO = new TaskAssigneeDAO();
    }

    public List<TaskAssignee> getAssigneesByTask(Long taskId) {
        return taskAssigneeDAO.findByTaskId(taskId);
    }

    public TaskAssignee addAssignee(Long taskId, Long userId) {
        TaskAssignee assignee = new TaskAssignee();
        assignee.setTaskId(taskId);
        assignee.setUserId(userId);
        assignee.setCreatedBy(UserService.getCurrentUser().getId());
        assignee.setCreatedAt(LocalDateTime.now());
        assignee.setIsDeleted(false);
        
        return taskAssigneeDAO.save(assignee);
    }

    public void removeAssignee(Long taskId, Long userId) {
        taskAssigneeDAO.delete(taskId, userId);
    }

    public void updateStatus(Long taskId, Long userId, TaskStatus newStatus) {
        taskAssigneeDAO.updateStatus(taskId, userId, newStatus);
    }
} 