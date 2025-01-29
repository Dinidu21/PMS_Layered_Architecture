package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.SQLUtil;
import com.dinidu.lk.pmt.dto.TaskReportData;
import com.dinidu.lk.pmt.dto.UserDTO;
import com.dinidu.lk.pmt.entity.TeamAssignment;
import com.dinidu.lk.pmt.utils.permissionTypes.Permission;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class QueryDAOImpl implements QueryDAO {
    // Working
    @Override
    public UserRole getUserRoleByUsername(String username) throws SQLException, ClassNotFoundException {
        UserRole userRole = null;
        String query = "SELECT r.role_name " +
                "FROM users u " +
                "LEFT JOIN roles r ON u.role_id = r.id " +
                "WHERE u.username = ?";
        ResultSet rs = SQLUtil.execute(query, username);
        if (rs.next()) {
            String roleName = rs.getString("role_name");

            if (roleName != null) {
                userRole = UserRole.valueOf(roleName);
            }
        }
        return userRole;
    }
    // Working
    @Override
    public List<UserDTO> getAllActiveMembersNames() throws SQLException, ClassNotFoundException {
        List<UserDTO> activeMembers = new ArrayList<>();
        String query = "SELECT u.username, u.full_name, u.email " +
                "FROM users u " +
                "JOIN roles r ON u.role_id = r.id " +
                "WHERE u.status = 'ACTIVE' AND (u.role_id = ? OR u.role_id = ?);";

        ResultSet rs = SQLUtil.execute(query, UserRole.TECH_LEAD.getId(), UserRole.DEVELOPER.getId());
        while (rs.next()) {
            String username = rs.getString("username");
            String fullName = rs.getString("full_name");
            String email = rs.getString("email");

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setFull_name(fullName);
            userDTO.setEmail(email);

            activeMembers.add(userDTO);
        }
        return activeMembers;
    }
    // Working
    @Override
    public Set<String> getUserPermissionsByRole(UserRole userRole) throws SQLException, ClassNotFoundException {
        Set<String> permissions = new HashSet<>();
        String query = "SELECT p.permission_name " +
                "FROM permissions p " +
                "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
                "INNER JOIN roles r ON rp.role_id = r.id " +
                "WHERE r.role_name = ?";

        ResultSet rs = SQLUtil.execute(query, userRole.name());

        while (rs.next()) {
            String permissionName = rs.getString("permission_name");
            permissions.add(permissionName);
        }

        return permissions;

    }
    // Working
    @Override
    public List<UserDTO> getAllUsersWithRolesAndPermissions() throws SQLException, ClassNotFoundException {
        List<UserDTO> users = new ArrayList<>();
        String query = "SELECT u.username, u.full_name, u.email, r.role_name, GROUP_CONCAT(DISTINCT p.permission_name SEPARATOR ', ') AS permissions " +
                "FROM users u " +
                "LEFT JOIN roles r ON u.role_id = r.id " +
                "LEFT JOIN role_permissions rp ON r.id = rp.role_id " +
                "LEFT JOIN permissions p ON rp.permission_id = p.id " +
                "WHERE u.status = 'ACTIVE' " +
                "GROUP BY u.id;";

        ResultSet rs = SQLUtil.execute(query);

        while (rs.next()) {
            String username = rs.getString("username");
            String fullName = rs.getString("full_name");
            String email = rs.getString("email");
            String roleName = rs.getString("role_name");
            String permissionsString = rs.getString("permissions");

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setFull_name(fullName);
            userDTO.setEmail(email);

            if (roleName != null) {
                userDTO.setRole(UserRole.valueOf(roleName));
            } else {
                userDTO.setRole(null);
            }

            Set<Permission> permissions = new HashSet<>();
            if (permissionsString != null) {
                String[] permissionsArray = permissionsString.split(", ");
                for (String permissionName : permissionsArray) {
                    try {
                        Permission permission = Permission.valueOf(permissionName.trim());
                        permissions.add(permission);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid permission name: " + permissionName);
                    }
                }
            }
            userDTO.setPermissions(permissions);

            users.add(userDTO);
        }

        return users;
    }
    // Working
    @Override
    public List<String> getAllTeamMembersNamesByTask(String taskName) throws SQLException, ClassNotFoundException {
        List<String> teamMembersNames = new ArrayList<>();

        String sql = "SELECT u.full_name FROM users u " +
                "JOIN team_assignments ta ON u.id = ta.user_id " +
                "JOIN tasks t ON ta.task_id = t.id " +
                "WHERE t.name = ?";

        ResultSet resultSet = SQLUtil.execute(sql, taskName);
        while (resultSet.next()) {
            String fullName = resultSet.getString("full_name");
            teamMembersNames.add(fullName);
        }
        System.out.println("\nTeam members names: " + teamMembersNames+"\n");
        return teamMembersNames;
    }
    // Working
    @Override
    public List<String> getTeamMemberEmailsByTask(long taskId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT u.email FROM users u " +
                "JOIN team_assignments ta ON u.id = ta.user_id " +
                "JOIN tasks t ON ta.task_id = t.id " +
                "WHERE t.id = ?";

        List<String> emails = new ArrayList<>();

        ResultSet resultSet = SQLUtil.execute(sql, taskId);
        while (resultSet.next()) {
            String email = resultSet.getString("email");
            emails.add(email);
        }

        System.out.println("\nEmails: " + emails+"\n");
        return emails;
    }
    // Working
    @Override
    public List<TeamAssignment> getAssignmentsByTaskId(Long taskId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM team_assignments ta " +
                "JOIN users u ON ta.user_id = u.id " +
                "WHERE ta.task_id = ? AND u.status = 'ACTIVE'";

        ResultSet rs = SQLUtil.execute(sql, taskId);

        List<TeamAssignment> assignments = new ArrayList<>();
        while (rs.next()) {
            TeamAssignment assignment = new TeamAssignment();
            assignment.setId(rs.getLong("id"));
            assignment.setTaskId(rs.getLong("task_id"));
            assignment.setUserId(rs.getLong("user_id"));
            assignment.setAssignedAt(rs.getTimestamp("assigned_at"));
            assignments.add(assignment);
        }
        return assignments;
    }
    // Working
    @Override
    public Map<Long, TaskReportData> getAllTaskReportData() throws SQLException, ClassNotFoundException {
        Map<Long, TaskReportData> reportDataMap = new LinkedHashMap<>();
        String query = """
                SELECT u.full_name, r.role_name, t.id AS task_id, t.name AS task_name,
                       t.status AS task_status, p.name AS project_name, t.due_date,
                       ta.assigned_at, u.id AS user_id
                FROM users u
                LEFT JOIN team_assignments ta ON ta.user_id = u.id
                LEFT JOIN tasks t ON t.id = ta.task_id
                LEFT JOIN projects p ON p.id = t.project_id
                LEFT JOIN roles r ON r.id = u.role_id
                WHERE u.status = 'ACTIVE' 
                  AND r.id NOT IN (1, 2, 3, 7)
                """;

        try (ResultSet rs = SQLUtil.execute(query)) {
            while (rs.next()) {
                long userId = rs.getLong("user_id");
                String assigneeName = rs.getString("full_name");
                String role = rs.getString("role_name");
                long taskId = rs.getLong("task_id");
                String taskName = rs.getString("task_name");
                String taskStatus = rs.getString("task_status");
                String projectName = rs.getString("project_name");
                String dueDate = rs.getString("due_date");
                String assignedDate = rs.getString("assigned_at");

                int taskCount = getTaskCountForUser(userId);

                TaskReportData data = reportDataMap.getOrDefault(userId,
                        new TaskReportData(assigneeName, role, taskId, taskName, taskStatus, projectName, dueDate, assignedDate, taskCount));

                data.setTaskCount(taskCount);
                reportDataMap.put(userId, data);
            }
        }
        return reportDataMap;
    }
    // Working
    private int getTaskCountForUser(long userId) throws SQLException, ClassNotFoundException {
        String countQuery = """
                SELECT COUNT(DISTINCT ta.task_id) AS total_task_count
                FROM team_assignments ta
                WHERE ta.user_id = ?
                """;

        try (ResultSet rs = SQLUtil.execute(countQuery, userId)) {
            if (rs.next()) {
                return rs.getInt("total_task_count");
            }
        }
        return 0;
    }
}
