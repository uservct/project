package com.nhom6.taskmanagement.model;

public enum UserRole {
    ADMIN("Quản trị viên"),
    MANAGER("Quản lý"),
    USER("Người dùng");

    private final String displayName;

    UserRole(String displayName) {
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