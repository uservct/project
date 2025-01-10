package com.nhom6.taskmanagement.model;

public enum ProjectStatus {
    PLANNING("Lên kế hoạch"),
    IN_PROGRESS("Đang thực hiện"),
    COMPLETED("Hoàn thành"),
    ON_HOLD("Tạm dừng"),
    CANCELLED("Đã hủy");

    private final String displayName;

    ProjectStatus(String displayName) {
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