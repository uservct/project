package com.nhom6.taskmanagement.repository;

import java.util.List;

import com.nhom6.taskmanagement.model.Project;

public interface ProjectRepository {
    List<Project> findAll();
    Project findById(Long id);
    void save(Project project);
    void deleteById(Long id);
}