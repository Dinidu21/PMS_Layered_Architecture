package com.dinidu.lk.pmt.utils.permissionTypes;

import java.util.EnumSet;
import java.util.Set;

public enum PermissionLevel {
    READ_ONLY("Read Only") {
        @Override
        public Set<Permission> getPermissions() {
            return EnumSet.of(
                    Permission.READ_PROJECT,
                    Permission.READ_TASK,
                    Permission.READ_REPORTS,
                    Permission.VIEW_REPORTS,
                    Permission.READ_TIMESHEETS
            );
        }
    },
    EDITOR("Editor") {
        @Override
        public Set<Permission> getPermissions() {
            return EnumSet.of(
                    Permission.CREATE_PROJECT,
                    Permission.READ_PROJECT,
                    Permission.UPDATE_PROJECT,
                    Permission.CREATE_TASK,
                    Permission.READ_TASK,
                    Permission.UPDATE_TASK,
                    Permission.CREATE_REPORTS,
                    Permission.READ_REPORTS,
                    Permission.UPDATE_REPORTS,
                    Permission.CREATE_TIMESHEETS,
                    Permission.UPDATE_TIMESHEETS,
                    Permission.READ_TIMESHEETS
            );
        }
    },
    ALL("All") {
        @Override
        public Set<Permission> getPermissions() {
            return EnumSet.allOf(Permission.class);
        }
    };

    PermissionLevel(String displayName) {
        this.displayName = displayName;
    }

    // Abstract method to be implemented by each enum constant

    public abstract Set<Permission> getPermissions();

    private final String displayName;

    public static PermissionLevel getLevelFromPermissions(Set<Permission> permissions) {
        for (PermissionLevel level : PermissionLevel.values()) {
            if (level.getPermissions().equals(permissions)) {
                return level;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }

    // Get permissions by selected level
    public static Set<Permission> getPermissionsByLevel(PermissionLevel level) {
        return level.getPermissions();
    }
}
