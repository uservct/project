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
import com.nhom6.taskmanagement.model.Attachment;
import com.nhom6.taskmanagement.repository.AttachmentRepository;

public class AttachmentRepositoryImpl implements AttachmentRepository {

    private static final String URL = AppConfig.getProperty("db.url");
    private static final String USER = AppConfig.getProperty("db.username");
    private static final String PASSWORD = AppConfig.getProperty("db.password");

    @Override
    public List<Attachment> findAll() {
        List<Attachment> attachments = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM attachments")) {

            while (resultSet.next()) {
                Attachment attachment = new Attachment();
                attachment.setId(resultSet.getLong("id"));
                attachment.setFileName(resultSet.getString("file_name"));
                attachment.setFileType(resultSet.getString("file_type"));
                // Set user if needed
                attachments.add(attachment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attachments;
    }

    @Override
    public Attachment findById(Long id) {
        Attachment attachment = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM attachments WHERE id = ?")) {

            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    attachment = new Attachment();
                    attachment.setId(resultSet.getLong("id"));
                    attachment.setFileName(resultSet.getString("file_name"));
                    attachment.setFileType(resultSet.getString("file_type"));
                    // Set user if needed
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attachment;
    }

    @Override
    public void save(Attachment attachment) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO attachments (file_name, file_type) VALUES (?, ?)")) {

            preparedStatement.setString(1, attachment.getFileName());
            preparedStatement.setString(2, attachment.getFileType());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM attachments WHERE id = ?")) {

            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}