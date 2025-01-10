package com.nhom6.taskmanagement.model;

public enum ProjectRole {
    OWNER("Chủ dự án"),
    MANAGER("Quản lý"),
    MEMBER("Thành viên");

    private final String displayName;

    ProjectRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 