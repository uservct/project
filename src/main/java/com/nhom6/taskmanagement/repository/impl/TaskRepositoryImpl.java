package com.nhom6.taskmanagement.repository.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.nhom6.taskmanagement.config.AppConfig;
import com.nhom6.taskmanagement.model.Task;
import com.nhom6.taskmanagement.repository.TaskRepository;

public class TaskRepositoryImpl implements TaskRepository {

    private static final String URL = AppConfig.getProperty("db.url");
    private static final String USER = AppConfig.getProperty("db.username");
    private static final String PASSWORD = AppConfig.getProperty("db.password");

    @Override
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM tasks")) {

            while (resultSet.next()) {
                Task task = new Task();
                task.setId(resultSet.getLong("id"));
                task.setName(resultSet.getString("name"));
                task.setStatus(resultSet.getString("status"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    @Override
    public Task findById(Long id) {
        Task task = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM tasks WHERE id = ?")) {

            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    task = new Task();
                    task.setId(resultSet.getLong("id"));
                    task.setName(resultSet.getString("name"));
                    task.setStatus(resultSet.getString("status"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return task;
    }

    @Override
    public void save(Task task) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO tasks (name, status) VALUES (?, ?)")) {

            preparedStatement.setString(1, task.getName());
            preparedStatement.setString(2, task.getStatus());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM tasks WHERE id = ?")) {

            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}