package com.nhom6.taskmanagement.repository.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.nhom6.taskmanagement.config.AppConfig;
import com.nhom6.taskmanagement.model.Project;
import com.nhom6.taskmanagement.repository.ProjectRepository;

public class ProjectRepositoryImpl implements ProjectRepository {

    private static final String URL = AppConfig.getProperty("db.url");
    private static final String USER = AppConfig.getProperty("db.username");
    private static final String PASSWORD = AppConfig.getProperty("db.password");

    @Override
    public List<Project> findAll() {
        List<Project> projects = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM projects")) {

            while (resultSet.next()) {
                Project project = new Project();
                project.setId(resultSet.getLong("id"));
                project.setName(resultSet.getString("name"));
                project.setStatus(resultSet.getString("status"));
                project.setDueDate(resultSet.getDate("due_date").toLocalDate());
                projects.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }

    @Override
    public Project findById(Long id) {
        Project project = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM projects WHERE id = ?")) {

            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    project = new Project();
                    project.setId(resultSet.getLong("id"));
                    project.setName(resultSet.getString("name"));
                    project.setStatus(resultSet.getString("status"));
                    project.setDueDate(resultSet.getDate("due_date").toLocalDate());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return project;
    }

    @Override
    public void save(Project project) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO projects (name, status, due_date) VALUES (?, ?, ?)")) {

            preparedStatement.setString(1, project.getName());
            preparedStatement.setString(2, project.getStatus());
            preparedStatement.setDate(3, Date.valueOf(project.getDueDate()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM projects WHERE id = ?")) {

            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}