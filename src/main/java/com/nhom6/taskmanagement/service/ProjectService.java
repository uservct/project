package com.nhom6.taskmanagement.service;

import java.util.List;

import com.nhom6.taskmanagement.model.Project;
import com.nhom6.taskmanagement.repository.ProjectRepository;
import com.nhom6.taskmanagement.repository.impl.ProjectRepositoryImpl;

public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService() {
        this.projectRepository = new ProjectRepositoryImpl();
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public void createProject(Project project) {
        projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}