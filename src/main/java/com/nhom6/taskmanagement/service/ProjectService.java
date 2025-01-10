package com.nhom6.taskmanagement.service;

import java.time.LocalDateTime;
import java.util.List;

import com.nhom6.taskmanagement.dao.ProjectDAO;
import com.nhom6.taskmanagement.model.Project;
import com.nhom6.taskmanagement.model.ProjectStatus;

public class ProjectService {
    private final ProjectDAO projectDAO;

    public ProjectService() {
        this.projectDAO = new ProjectDAO();
    }

    public List<Project> getAllProjects() {
        return projectDAO.findAll();
    }

    public Project getProjectById(Long id) {
        return projectDAO.findById(id);
    }

    public Project createProject(Project project) {
        // Set default values
        project.setCreatedBy(UserService.getCurrentUser().getId());
        project.setCreatedAt(LocalDateTime.now());
        project.setStatus(ProjectStatus.PLANNING);
        project.setIsDeleted(false);
        
        return projectDAO.save(project);
    }

    public void updateProjectStatus(Long projectId, ProjectStatus newStatus) {
        Project project = projectDAO.findById(projectId);
        if (project != null) {
            project.setStatus(newStatus);
            project.setUpdatedAt(LocalDateTime.now());
            projectDAO.save(project);
        }
    }

    public Project updateProject(Project project) {
        project.setUpdatedAt(LocalDateTime.now());
        return projectDAO.update(project);
    }

    public void deleteProject(Long projectId) {
        Project project = projectDAO.findById(projectId);
        if (project != null) {
            project.setIsDeleted(true);
            project.setDeletedAt(LocalDateTime.now());
            project.setDeletedBy(UserService.getCurrentUser().getId());
            projectDAO.update(project);
        }
    }

    public List<Project> getProjectsByMember(Long userId) {
        return projectDAO.findByMemberId(userId);
    }
} 