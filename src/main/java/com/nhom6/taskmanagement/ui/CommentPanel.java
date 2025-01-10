package com.nhom6.taskmanagement.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.nhom6.taskmanagement.model.Comment;
import com.nhom6.taskmanagement.model.User;
import com.nhom6.taskmanagement.service.CommentService;
import com.nhom6.taskmanagement.service.UserService;

public class CommentPanel extends JPanel {
    private final JFrame mainFrame;
    private final Long projectId;
    private final Long taskId;
    private final CommentService commentService;
    private final UserService userService;
    private JPanel commentsPanel;
    private JTextArea commentInput;

    public CommentPanel(JFrame mainFrame, Long projectId, Long taskId) {
        this.mainFrame = mainFrame;
        this.projectId = projectId;
        this.taskId = taskId;
        this.commentService = new CommentService();
        this.userService = new UserService();
        initComponents();
        loadComments();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Comments Panel
        commentsPanel = new JPanel();
        commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(commentsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        commentInput = new JTextArea(3, 20);
        commentInput.setLineWrap(true);
        commentInput.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(commentInput);

        JButton sendButton = new JButton("Gửi");
        sendButton.addActionListener(e -> handleAddComment());

        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);
    }

    private void loadComments() {
        commentsPanel.removeAll();
        List<Comment> comments;
        
        if (taskId != null) {
            comments = commentService.getCommentsByTask(taskId);
        } else {
            comments = commentService.getCommentsByProject(projectId);
        }
        
        for (Comment comment : comments) {
            addCommentToPanel(comment);
        }
        
        commentsPanel.revalidate();
        commentsPanel.repaint();
    }

    private void addCommentToPanel(Comment comment) {
        JPanel commentPanel = new JPanel(new BorderLayout());
        commentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Header Panel (User info and timestamp)
        JPanel headerPanel = new JPanel(new BorderLayout());
        User user = userService.getUserById(comment.getCreatedBy());
        
        JLabel userLabel = new JLabel(user != null ? user.getFullName() : "Unknown");
        userLabel.setFont(new Font("Arial", Font.BOLD, 12));
        headerPanel.add(userLabel, BorderLayout.WEST);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        JLabel timeLabel = new JLabel(comment.getCreatedAt().format(formatter));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timeLabel.setForeground(Color.GRAY);
        headerPanel.add(timeLabel, BorderLayout.EAST);

        // Content
        JTextArea contentArea = new JTextArea(comment.getContent());
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(null);
        contentArea.setBorder(null);

        // Delete button (only for comment owner)
        if (comment.getCreatedBy().equals(UserService.getCurrentUser().getId())) {
            JButton deleteButton = new JButton("Xóa");
            deleteButton.addActionListener(e -> handleDeleteComment(comment.getId()));
            headerPanel.add(deleteButton, BorderLayout.CENTER);
        }

        commentPanel.add(headerPanel, BorderLayout.NORTH);
        commentPanel.add(contentArea, BorderLayout.CENTER);

        commentsPanel.add(commentPanel);
        commentsPanel.add(Box.createVerticalStrut(10));
    }

    private void handleAddComment() {
        String content = commentInput.getText().trim();
        if (!content.isEmpty()) {
            Comment newComment = commentService.addComment(content, projectId, taskId);
            if (newComment != null) {
                commentInput.setText("");
                loadComments();
            }
        }
    }

    private void handleDeleteComment(Long commentId) {
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
            "Bạn có chắc chắn muốn xóa bình luận này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            commentService.deleteComment(commentId);
            loadComments();
        }
    }
} 