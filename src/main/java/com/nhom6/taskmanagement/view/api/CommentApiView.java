package com.nhom6.taskmanagement.view.api;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.nhom6.taskmanagement.config.ServiceManager;
import com.nhom6.taskmanagement.model.Comment;
import com.nhom6.taskmanagement.service.CommentService;

public class CommentApiView extends JFrame {
    private CommentService commentService;

    public CommentApiView() {
        this.commentService = ServiceManager.getCommentService();
        initUI();
    }

    private void initUI() {
        setTitle("API Bình luận");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        List<Comment> comments = commentService.getAllComments();
        String[] columnNames = {"ID", "Nội dung", "Người dùng"};
        Object[][] data = new Object[comments.size()][3];

        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            data[i][0] = comment.getId();
            data[i][1] = comment.getContent();
            data[i][2] = comment.getUser().getName();
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CommentApiView commentApiView = new CommentApiView();
            commentApiView.setVisible(true);
        });
    }
}