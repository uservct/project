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
import com.nhom6.taskmanagement.model.Comment;
import com.nhom6.taskmanagement.repository.CommentRepository;

public class CommentRepositoryImpl implements CommentRepository {

    private static final String URL = AppConfig.getProperty("db.url");
    private static final String USER = AppConfig.getProperty("db.username");
    private static final String PASSWORD = AppConfig.getProperty("db.password");

    @Override
    public List<Comment> findAll() {
        List<Comment> comments = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM comments")) {

            while (resultSet.next()) {
                Comment comment = new Comment();
                comment.setId(resultSet.getLong("id"));
                comment.setContent(resultSet.getString("content"));
                // Set user if needed
                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    @Override
    public Comment findById(Long id) {
        Comment comment = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM comments WHERE id = ?")) {

            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    comment = new Comment();
                    comment.setId(resultSet.getLong("id"));
                    comment.setContent(resultSet.getString("content"));
                    // Set user if needed
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comment;
    }

    @Override
    public void save(Comment comment) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO comments (content) VALUES (?)")) {

            preparedStatement.setString(1, comment.getContent());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM comments WHERE id = ?")) {

            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}