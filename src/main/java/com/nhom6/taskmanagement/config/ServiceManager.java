package com.nhom6.taskmanagement.config;

import com.nhom6.taskmanagement.service.AttachmentService;
import com.nhom6.taskmanagement.service.CommentService;
import com.nhom6.taskmanagement.service.ProjectService;
import com.nhom6.taskmanagement.service.TaskService;
import com.nhom6.taskmanagement.service.UserService;

public class ServiceManager {
    private static final UserService userService = new UserService();
    private static final ProjectService projectService = new ProjectService();
    private static final TaskService taskService = new TaskService();
    private static final CommentService commentService = new CommentService();
    private static final AttachmentService attachmentService = new AttachmentService();

    public static UserService getUserService() {
        return userService;
    }

    public static ProjectService getProjectService() {
        return projectService;
    }

    public static TaskService getTaskService() {
        return taskService;
    }

    public static CommentService getCommentService() {
        return commentService;
    }

    public static AttachmentService getAttachmentService() {
        return attachmentService;
    }

}
