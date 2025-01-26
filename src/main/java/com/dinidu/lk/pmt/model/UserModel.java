package com.dinidu.lk.pmt.model;

import com.dinidu.lk.pmt.db.DBConnection;
import com.dinidu.lk.pmt.dto.UserDTO;
import com.dinidu.lk.pmt.utils.CrudUtil;
import com.dinidu.lk.pmt.utils.permissionTypes.Permission;
import java.sql.Connection;
import java.sql.SQLException;

public class UserModel {
    /*
    public static boolean saveUser(UserDTO userDTO) {
        String hashedPassword = BCrypt.hashpw(userDTO.getPassword(), BCrypt.gensalt(12));
        return Boolean.TRUE.equals(CrudUtil.execute("INSERT INTO users (username, full_name, password, email, phone_number) VALUES (?, ?, ?, ?, ?)",
                userDTO.getUsername(),
                userDTO.getFull_name(),
                hashedPassword,
                userDTO.getEmail(),
                userDTO.getPhoneNumber()));
    }
*/ // Working

    /*
    public static boolean updateUser(UserDTO userDTO) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE users SET ");
        List<String> fieldsToUpdate = new ArrayList<>();

        if (userDTO.getFull_name() != null) {
            fieldsToUpdate.add("full_name = ?");
        }
        if (userDTO.getEmail() != null) {
            fieldsToUpdate.add("email = ?");
        }
        if (userDTO.getPhoneNumber() != null) {
            fieldsToUpdate.add("phone_number = ?");
        }

        if (fieldsToUpdate.isEmpty()) {
            return false;
        }

        queryBuilder.append(String.join(", ", fieldsToUpdate));
        queryBuilder.append(" WHERE username = ?");

        PreparedStatement pstm = null;

        try {
            Connection connection = DBConnection.getInstance().getConnection();

            if (connection == null || connection.isClosed()) {
                System.out.println("Database connection is closed.");
                return false;
            }

            pstm = connection.prepareStatement(queryBuilder.toString());
            int index = 1;

            if (userDTO.getFull_name() != null) {
                pstm.setString(index++, userDTO.getFull_name());
            }
            if (userDTO.getEmail() != null) {
                pstm.setString(index++, userDTO.getEmail());
            }
            if (userDTO.getPhoneNumber() != null) {
                pstm.setString(index++, userDTO.getPhoneNumber());
            }
            pstm.setString(index, userDTO.getUsername());

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
            CustomErrorAlert.showAlert("ERROR", "Error updating user");
            return false;
        } finally {
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (SQLException ignored) {
                }
            }
        }
    } // Working
*/ // Working

    /*
    public static String verifyUser(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
        Connection connection;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getInstance().getConnection();

            if (connection == null || connection.isClosed()) {
                System.out.println("Database connection is closed.");
                return "ERROR";
            }

            pst = connection.prepareStatement(query);
            pst.setString(1, username);

            rs = pst.executeQuery();
            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");

                if (BCrypt.checkpw(password, storedHashedPassword)) {
                    return "SUCCESS";
                } else {
                    return "INVALID_PASSWORD";
                }
            } else {
                return "INVALID_USERNAME";
            }
        } catch (SQLException e) {
            System.out.println("Error verifying user: " + e.getMessage());
            CustomErrorAlert.showAlert("ERROR", "Error verifying user: ");
            return "ERROR";
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException ignored) {
            }
            if (pst != null) try {
                pst.close();
            } catch (SQLException ignored) {
            }
        }
    }
*/ // Working

    /*
    public static UserRole getUserRoleByUsername(String username) {
        UserRole userRole = null;
        String query = "SELECT r.role_name " +
                "FROM users u " +
                "LEFT JOIN roles r ON u.role_id = r.id " +
                "WHERE u.username = ?";

        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String roleName = rs.getString("role_name");

                if (roleName != null) {
                    userRole = UserRole.valueOf(roleName);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user role: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid role name: " + e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignored) {
                }
            }
        }

        return userRole;
    }
*/ // Working

    /*
    public static UserDTO getUserDetailsByUsername(String loggedInUsername) {
        UserDTO userDTO = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        String query = "SELECT full_name, email, phone_number FROM users WHERE username = ?";

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            pst = connection.prepareStatement(query);
            pst.setString(1, loggedInUsername);
            rs = pst.executeQuery();

            if (rs.next()) {
                userDTO = new UserDTO();
                userDTO.setFull_name(rs.getString("full_name"));
                userDTO.setEmail(rs.getString("email"));
                userDTO.setPhoneNumber(rs.getString("phone_number"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching user details: " + e.getMessage());
            CustomErrorAlert.showAlert("ERROR", "Error fetching user details ");
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException ignored) {
            }
            if (pst != null) try {
                pst.close();
            } catch (SQLException ignored) {
            }
        }

        return userDTO;
    }*/ // Working

    /*   public static boolean isEmailRegistered(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        Connection connection;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getInstance().getConnection();

            if (connection == null || connection.isClosed()) {
                System.out.println("Database connection is closed.");
                return false;
            }

            pst = connection.prepareStatement(query);
            pst.setString(1, email);

            rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking email registration: " + e.getMessage());
            CustomErrorAlert.showAlert("ERROR", "Error checking email registration ");
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException ignored) {
            }
            if (pst != null) try {
                pst.close();
            } catch (SQLException ignored) {
            }
        }

        return false;
    }*/ // Working

    /*
    public static boolean updatePasswordUsingEmail(String userEmail, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        PreparedStatement pstmt = null;

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null || conn.isClosed()) {
                System.out.println("Database connection is closed.");
                return false;
            }

            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, userEmail);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Password updated successfully.");
                return true;
            } else {
                System.out.println("Password update failed. No rows affected.");
                CustomErrorAlert.showAlert("ERROR", "Password update failed. No rows affected.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error updating password: " + e.getMessage());
            CustomErrorAlert.showAlert("ERROR", "Error updating password.");
            return false;
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }
*/ // Working

    /*
    public static boolean updatePassword(String loggedInUsername, String password) {
        String query = "UPDATE users SET password = ? WHERE username = ?";
        Connection connection;
        PreparedStatement pstm = null;

        try {
            connection = DBConnection.getInstance().getConnection();

            if (connection == null || connection.isClosed()) {
                System.out.println("Database connection is closed.");
                return false;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
            pstm = connection.prepareStatement(query);
            pstm.setString(1, hashedPassword);
            pstm.setString(2, loggedInUsername);

            int affectedRows = pstm.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Password updated successfully.");
                return true;
            } else {
                System.out.println("Password update failed. No rows affected.");
                CustomErrorAlert.showAlert("ERROR", "Password update failed. No rows affected.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error updating password: " + e.getMessage());
            CustomErrorAlert.showAlert("ERROR", "Error updating password ");
            return false;
        } finally {
            if (pstm != null) try {
                pstm.close();
            } catch (SQLException ignored) {
            }
        }

    }
*/ // Working

    /*    public static String getUserEmail(String loggedInUsername) throws SQLException {
            String query = "SELECT email FROM users WHERE username = ?";
            try(ResultSet resultSet = CrudUtil.execute(query, loggedInUsername)){
                if(resultSet.next()){
                    return resultSet.getString("email");
                }
            }
            return null;
        }*/ // Working

    /*
    public static String getProfileImagePath(String username) {
        String sql = "SELECT profile_image_path FROM users WHERE username = ?";
        Connection connection;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getInstance().getConnection();

            if (connection == null || connection.isClosed()) {
                System.out.println("Database connection is closed.");
                return null;
            }

            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("profile_image_path");
            }

        } catch (SQLException e) {
            System.out.println("Error getting profile image path: " + e.getMessage());
            throw new RuntimeException("Error getting profile image path", e);

        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException ignored) {
            }
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException ignored) {
            }
        }
        return null;
    }
*/ // Working

    /*
    public static List<UserDTO> getAllActiveMembersNames() {
        List<UserDTO> activeMembers = new ArrayList<>();
        String query = "SELECT u.username, u.full_name, u.email " +
                "FROM users u " +
                "JOIN roles r ON u.role_id = r.id " +
                "WHERE u.status = 'ACTIVE' AND (u.role_id = ? OR u.role_id = ?);";

        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            pst = connection.prepareStatement(query);
            pst.setInt(1, UserRole.TECH_LEAD.getId());
            pst.setInt(2, UserRole.DEVELOPER.getId());
            rs = pst.executeQuery();

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
        } catch (SQLException e) {
            System.out.println("Error fetching active members: " + e.getMessage());
            CustomErrorAlert.showAlert("ERROR", "Error fetching active members: ");
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException ignored) {
            }
            if (pst != null) try {
                pst.close();
            } catch (SQLException ignored) {
            }
        }

        return activeMembers;
    }
*/ // Working

    /*
    public static Set<String> getUserPermissionsByRole(UserRole userRole) {
        Set<String> permissions = new HashSet<>();
        if (userRole == null) {
            System.out.println("UserRole is null, skipping permission fetch.");
            return permissions;
        }

        String query = "SELECT p.permission_name " +
                "FROM permissions p " +
                "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
                "INNER JOIN roles r ON rp.role_id = r.id " +
                "WHERE r.role_name = ?";

        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            pst = connection.prepareStatement(query);
            pst.setString(1, userRole.name());
            rs = pst.executeQuery();

            while (rs.next()) {
                permissions.add(rs.getString("permission_name"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching user permissions: " + e.getMessage());
            CustomErrorAlert.showAlert("ERROR", "Error fetching user permissions ");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException ignored) {
                }
            }
        }
        return permissions;
    }
*/ // Working

    /*    public static List<UserDTO> getAllUsersWithRolesAndPermissions() {
            List<UserDTO> users = new ArrayList<>();
            String query = "SELECT u.username, u.full_name, u.email, r.role_name, GROUP_CONCAT(DISTINCT p.permission_name SEPARATOR ', ') AS permissions " +
                    "FROM users u " +
                    "LEFT JOIN roles r ON u.role_id = r.id " +
                    "LEFT JOIN role_permissions rp ON r.id = rp.role_id " +
                    "LEFT JOIN permissions p ON rp.permission_id = p.id " +
                    "WHERE u.status = 'ACTIVE' " +
                    "GROUP BY u.id;";

            PreparedStatement pst = null;
            ResultSet rs = null;

            try {
                Connection connection = DBConnection.getInstance().getConnection();
                pst = connection.prepareStatement(query);
                rs = pst.executeQuery();

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
            } catch (SQLException e) {
                System.out.println("Error fetching users: " + e.getMessage());
                CustomErrorAlert.showAlert("ERROR", "Error fetching users ");
            } finally {
                if (rs != null) try {
                    rs.close();
                } catch (SQLException ignored) {
                }
                if (pst != null) try {
                    pst.close();
                } catch (SQLException ignored) {
                }
            }
            return users;
        }*/ // Working

    /*
        public static void updateProfileImagePath(String username, String imagePath) {
            String sql = "UPDATE users SET profile_image_path = ? WHERE username = ?";
            Connection connection;
            PreparedStatement pstmt = null;

            try {
                connection = DBConnection.getInstance().getConnection();

                if (connection == null || connection.isClosed()) {
                    System.out.println("Database connection is closed.");
                    return;
                }

                pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, imagePath);
                pstmt.setString(2, username);
                pstmt.executeUpdate();

            } catch (SQLException e) {
                System.out.println("Error updating profile image path: " + e.getMessage());
                throw new RuntimeException("Error updating profile image path", e);

            } finally {
                if (pstmt != null) try {
                    pstmt.close();
                } catch (SQLException ignored) {
                }
            }

        }
    */ // Working

    /*
        public static Long getUserIdByUsername(String username) {
            String sql = "SELECT id FROM users WHERE username = ?";
            Connection connection;
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {
                connection = DBConnection.getInstance().getConnection();

                if (connection == null || connection.isClosed()) {
                    System.out.println("Database connection is closed.");
                    return null;
                }

                pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, username);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    return rs.getLong("id");
                }

            } catch (SQLException e) {
                System.out.println("Error getting profile image path: " + e.getMessage());
                throw new RuntimeException("Error getting profile image path", e);

            } finally {
                if (rs != null) try {
                    rs.close();
                } catch (SQLException ignored) {
                }
                if (pstmt != null) try {
                    pstmt.close();
                } catch (SQLException ignored) {
                }
            }
            return null;
        }
    */ // Working

    /*
        public static String getUserFullNameById(Long userId) {
            String sql = "SELECT full_name FROM users WHERE id = ?";
            Connection conn;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            String fullName = null;

            try {
                conn = DBConnection.getInstance().getConnection();
                pstmt = conn.prepareStatement(sql);
                pstmt.setLong(1, userId);

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    fullName = rs.getString("full_name");
                }
            } catch (SQLException e) {
                System.err.println("Error fetching user full name: " + e.getMessage());
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException ignored) {
                    }
                }
                if (pstmt != null) {
                    try {
                        pstmt.close();
                    } catch (SQLException ignored) {
                    }
                }
            }
            return fullName;
        }
    */ // Working

    /*
        public static Image getUserProfilePicByUserId(Long createdBy) {
            String sql = "SELECT profile_image_path FROM users WHERE id = ?";
            Connection conn;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            Image profilePic = null;
            String defaultImagePath = "src/main/resources/asserts/icons/noPic.png";

            try {
                conn = DBConnection.getInstance().getConnection();
                pstmt = conn.prepareStatement(sql);
                pstmt.setLong(1, createdBy);

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    String imagePath = rs.getString("profile_image_path");

                    if (imagePath != null) {
                        profilePic = new Image(new FileInputStream(imagePath));
                    }
                }

                if (profilePic == null) {
                    System.out.println("No profile image found. Using default image.");
                    profilePic = new Image(new FileInputStream(defaultImagePath));
                }

            } catch (SQLException | FileNotFoundException e) {
                System.err.println("Error fetching user profile image: " + e.getMessage());

                try {
                    profilePic = new Image(new FileInputStream(defaultImagePath));
                } catch (FileNotFoundException ex) {
                    System.err.println("Default image not found: " + ex.getMessage());
                }
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException ignored) {
                    }
                }
                if (pstmt != null) {
                    try {
                        pstmt.close();
                    } catch (SQLException ignored) {
                    }
                }
            }
            return profilePic;
        }
    */ // Working

    /*
        public static boolean updateUserStatus(String username, String status) {
            String sql = "UPDATE users SET status = ? WHERE username = ?";
            PreparedStatement pstmt = null;
            try {
                Connection conn = DBConnection.getInstance().getConnection();

                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, status);
                pstmt.setString(2, username);

                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (pstmt != null) {
                    try {
                        pstmt.close();
                    } catch (SQLException ignored) {
                    }
                }
            }
        }
    */ // Working

    /*
        public static Long getUserIdByFullName(String selectedMemberName) {
            String sql = "SELECT id FROM users WHERE full_name = ?";
            PreparedStatement pst = null;
            ResultSet rs = null;
            Long userId = null;

            try {
                Connection connection = DBConnection.getInstance().getConnection();
                pst = connection.prepareStatement(sql);
                pst.setString(1, selectedMemberName);
                rs = pst.executeQuery();

                if (rs.next()) {
                    userId = rs.getLong("id");
                }
            } catch (SQLException e) {
                System.out.println("Error fetching user ID: " + e.getMessage());
            } finally {
                if (rs != null) try {
                    rs.close();
                } catch (SQLException ignored) {
                }
                if (pst != null) try {
                    pst.close();
                } catch (SQLException ignored) {
                }
            }
            return userId;
        }
    */ // Working

    /*    public static String getUserEmailById(Long id) throws SQLException {
            String sql = "SELECT email FROM users WHERE id = ?";
            try (ResultSet resultSet = CrudUtil.execute(sql, id)) {
                if (resultSet.next()) {
                    return resultSet.getString("email");
                }
            }
            return null;
        }*/ // Working

    /*
        public static String getUserEmailByFullName(String selectedUser) throws SQLException {
            String sql = "SELECT email FROM users WHERE full_name = ?";
            try(ResultSet resultSet = CrudUtil.execute(sql, selectedUser)){
                if(resultSet.next()){
                    return resultSet.getString("email");
                }
            }
            return null;
        }
    */ // Working

    /*
        public static String getUserNameByEmail(String email) throws SQLException {
            String sql = "SELECT full_name FROM users WHERE email = ?";
            try(ResultSet resultSet = CrudUtil.execute(sql, email)){
                if(resultSet.next()){
                    return resultSet.getString("full_name");
                }
            }
            return null;
        }
    */ // Working

    // TRANSACTION
    public static boolean updateUserRoleAndPermissions(String username, UserDTO user) throws SQLException {
        Connection connection = null;

        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);

            String updateRoleQuery = "UPDATE users SET role_id = ? WHERE username = ?";
            boolean roleUpdated = CrudUtil.execute(updateRoleQuery, user.getRole().getId(), username);
            if (!roleUpdated) {
                System.out.println("Failed to update role for user: " + username);
                connection.rollback();
                return false;
            }
            System.out.println("Role updated successfully for user: " + username + ", New Role ID: " + user.getRole().getId());

            String deletePermissionsQuery = "DELETE FROM role_permissions WHERE role_id = (SELECT role_id FROM users WHERE username = ?)";
            boolean permissionsDeleted = CrudUtil.execute(deletePermissionsQuery, username);

            if (!permissionsDeleted) {
                System.out.println("No existing permissions to delete for user: " + username);
            } else {
                System.out.println("Old permissions deleted successfully for user: " + username);
            }

            String insertPermissionsQuery = "INSERT INTO role_permissions (role_id, permission_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE permission_id = permission_id";
            for (Permission permission : user.getPermissions()) {
                boolean permissionInserted = CrudUtil.execute(insertPermissionsQuery, user.getRole().getId(), permission.getId());
                if (!permissionInserted) {
                    System.out.println("Failed to insert permission ID: " + permission.getId() + " for Role ID: " + user.getRole().getId());
                    connection.rollback();
                    return false;
                }
                System.out.println("Permission inserted successfully: " + permission.getId() + " for Role ID: " + user.getRole().getId());
            }

            connection.commit();
            System.out.println("Role and permissions updated successfully for user: " + username);
            return true;

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                System.out.println("Error during rollback: " + rollbackException.getMessage());
            }
            System.out.println("SQL Exception: " + e.getMessage());
            throw e;

        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    System.out.println("Error resetting auto-commit: " + e.getMessage());
                }
            }
        }
    }

}