package com.dinidu.lk.pmt.utils.permissionTypes;

public enum Permission {
    // Project Permissions
    CREATE_PROJECT(1),
    READ_PROJECT(2),
    UPDATE_PROJECT(3),
    DELETE_PROJECT(4),

    // Task Permissions
    CREATE_TASK(5),
    READ_TASK(6),
    UPDATE_TASK(7),
    DELETE_TASK(8),

    // Report Permissions
    CREATE_REPORTS(9),
    READ_REPORTS(10),
    UPDATE_REPORTS(11),
    DELETE_REPORTS(12),
    VIEW_REPORTS(13),

    // Timesheet Permissions
    CREATE_TIMESHEETS(14),
    UPDATE_TIMESHEETS(15),
    READ_TIMESHEETS(16),
    DELETE_TIMESHEETS(17),
    APPROVE_TIMESHEETS(18),

    // User Management Permissions
    MANAGE_USERS(19),
    MANAGE_ROLES(20);

    private final int id;

    Permission(int id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
