package com.nhom6.taskmanagement.model;

public enum UserStatus {
    ACTIVE("Hoạt động"),
    INACTIVE("Không hoạt động"),
    BLOCKED("Bị khóa");

    private final String displayName;

    UserStatus(String displayName) {
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