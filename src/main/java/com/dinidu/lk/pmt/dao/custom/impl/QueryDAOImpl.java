package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.SQLUtil;
import com.dinidu.lk.pmt.dto.UserDTO;
import com.dinidu.lk.pmt.utils.permissionTypes.Permission;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
