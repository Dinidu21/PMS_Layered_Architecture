package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.SQLUtil;
import com.dinidu.lk.pmt.dao.custom.UserDAO;
import com.dinidu.lk.pmt.db.DBConnection;
import com.dinidu.lk.pmt.entity.User;
import javafx.scene.image.Image;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserDAOImpl implements UserDAO {
    //Working
    @Override
    public boolean save(User entity) throws SQLException, ClassNotFoundException {
        String hashedPassword = BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt(12));
        return SQLUtil.execute("INSERT INTO users (username, full_name, password, email, phone_number) VALUES (?, ?, ?, ?, ?)",
                entity.getUsername(),
                entity.getFull_name(),
                hashedPassword,
                entity.getEmail(),
                entity.getPhoneNumber());
    }  //Working
    //Working
    @Override
    public String verifyUser(String username, String password) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT password FROM users WHERE username = ?",username);
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
    } //Working
    //Working
    @Override
    public boolean isEmailRegistered(String email) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT COUNT(*) FROM users WHERE email = ?", email);
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }
    //Working
    @Override
    public User getUserDetailsByUsername(String loggedInUsername) throws SQLException, ClassNotFoundException {
        User userDTO = null;
        ResultSet rs = SQLUtil.execute("SELECT full_name, email, phone_number FROM users WHERE username = ?", loggedInUsername);
        if (rs.next()) {
            userDTO = new User();
            userDTO.setFull_name(rs.getString("full_name"));
            userDTO.setEmail(rs.getString("email"));
            userDTO.setPhoneNumber(rs.getString("phone_number"));
        }
        return userDTO;
    }
    //Working
    @Override
    public boolean update(User entity) throws SQLException, ClassNotFoundException {
        StringBuilder queryBuilder = new StringBuilder("UPDATE users SET ");
        List<String> fieldsToUpdate = new ArrayList<>();

        if (entity.getFull_name() != null) {
            fieldsToUpdate.add("full_name = ?");
        }
        if (entity.getEmail() != null) {
            fieldsToUpdate.add("email = ?");
        }
        if (entity.getPhoneNumber() != null) {
            fieldsToUpdate.add("phone_number = ?");
        }

        if (fieldsToUpdate.isEmpty()) {
            return false;
        }

        Connection connection = DBConnection.getInstance().getConnection();

        if (connection == null || connection.isClosed()) {
            System.out.println("Database connection is closed.");
            return false;
        }

        PreparedStatement pstm;

        queryBuilder.append(String.join(", ", fieldsToUpdate));
        queryBuilder.append(" WHERE username = ?");
        pstm = connection.prepareStatement(queryBuilder.toString());
        int index = 1;

        if (entity.getFull_name() != null) {
            pstm.setString(index++, entity.getFull_name());
        }
        if (entity.getEmail() != null) {
            pstm.setString(index++, entity.getEmail());
        }
        if (entity.getPhoneNumber() != null) {
            pstm.setString(index++, entity.getPhoneNumber());
        }

        pstm.setString(index, entity.getUsername());

        return pstm.executeUpdate() > 0;
    }
    //Working
    @Override
    public boolean updatePasswordUsingEmail(String userEmail, String newPassword) throws SQLException, ClassNotFoundException {
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
        return SQLUtil.execute("UPDATE users SET password = ? WHERE email = ?",  hashedPassword,userEmail);
    }
    //Working
    @Override
    public boolean updatePassword(String loggedInUsername, String password) throws SQLException, ClassNotFoundException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        return SQLUtil.execute("UPDATE users SET password = ? WHERE username = ?", hashedPassword, loggedInUsername);
    }
    //Working
    @Override
    public String getProfileImagePath(String username) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT profile_image_path FROM users WHERE username = ?", username);
        if (rs.next()) {
            return rs.getString("profile_image_path");
        }
        return null;
    }
    //Working
    @Override
    public String getUserEmail(String loggedInUsername) throws SQLException, ClassNotFoundException {
        try(ResultSet resultSet = SQLUtil.execute("SELECT email FROM users WHERE username = ?", loggedInUsername)){
            if(resultSet.next()){
                return resultSet.getString("email");
            }
        }
        return null;
    }
    //Working
    @Override
    public void updateProfileImagePath(String username, String imagePath) throws SQLException, ClassNotFoundException {
        SQLUtil.execute("UPDATE users SET profile_image_path = ? WHERE username = ?", imagePath, username);
    }
    //Working Checked 1 Usage
    @Override
    public Long getUserIdByUsername(String username) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT id FROM users WHERE username = ?", username);
        if (rs.next()) {
            return rs.getLong("id");
        }
        return null;
    }
    //Working Checked 2 Usage
    @Override
    public String getUserFullNameById(Long userId) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT full_name FROM users WHERE id = ?", userId);
        if (rs.next()) {
            return rs.getString("full_name");
        }
        return null;
    }
    // Working Checked 2 Usage
    @Override
    public Image getUserProfilePicByUserId(Long createdBy) throws SQLException, ClassNotFoundException, FileNotFoundException {
        System.out.println("===Inside getUserProfilePicByUserId method=== ");
        Image profilePic;
        String defaultImagePath = "D:/PL/Java/GDSE/PMS_Layered_Architecture/src/main/resources/asserts/icons/noPic.png";
        String imagePath = null;

        try {
            ResultSet rs = SQLUtil.execute("SELECT profile_image_path FROM users WHERE id = ?", createdBy);

            if (rs.next()) {
                imagePath = rs.getString("profile_image_path");
            }

            if (imagePath != null && new File(imagePath).exists()) {
                profilePic = new Image(new FileInputStream(imagePath));
            } else {
                System.out.println("No profile image found or invalid path. Using default image.");
                if (defaultImagePath != null) {
                    profilePic = new Image(defaultImagePath);
                } else {
                    throw new FileNotFoundException("Default image not found in resources.");
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error loading image: " + e.getMessage());
            throw e;
        }

        return profilePic;
    }
    // Working
    @Override
    public boolean updateUserStatus(String username, String status) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("UPDATE users SET status = ? WHERE username = ?", status, username);
    }
    // Working
    @Override
    public Long getUserIdByFullName(String selectedMemberName) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT id FROM users WHERE full_name = ?", selectedMemberName);
        if (rs.next()) {
            return rs.getLong("id");
        }
        return null;
    }
    // Working
    @Override
    public String getUserEmailById(Long id) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT email FROM users WHERE id = ?", id);
        if (rs.next()) {
            return rs.getString("email");
        }
        return null;
    }
    // Working
    @Override
    public String getUserEmailByFullName(String selectedUser) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT email FROM users WHERE full_name = ?", selectedUser);
        if (rs.next()) {
            return rs.getString("email");
        }
        return null;
    }
    // Working
    @Override
    public String getUserNameByEmail(String email) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT full_name FROM users WHERE email = ?", email);
        if (rs.next()) {
            return rs.getString("full_name");
        }
        return null;
    }
    // Working
    @Override
    public boolean updateUserRole(int id, String username) throws SQLException, ClassNotFoundException {
       return SQLUtil.execute("UPDATE users SET role_id = ? WHERE username = ?",id,username);
    }
    // Working
    @Override
    public boolean deletePermissionsInCurrentRole(String username) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("DELETE FROM role_permissions WHERE role_id = (SELECT role_id FROM users WHERE username = ?)",username);
    }
    // Working
    @Override
    public boolean insertPermissionsInCurrentRole(int id, long id1) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("INSERT INTO role_permissions (role_id, permission_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE permission_id = permission_id",id,id1);
    }


    /////////////////// no need to Impl
    @Override
    public boolean insert(User user) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String idOrName) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<User> fetchAll() throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Map<String, String> getAllNames() throws SQLException, ClassNotFoundException {
        return Map.of();
    }

    @Override
    public List<User> searchByName(String searchQuery) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }
}
