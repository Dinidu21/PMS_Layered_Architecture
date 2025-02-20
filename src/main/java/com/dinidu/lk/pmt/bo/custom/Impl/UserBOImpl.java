package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.UserDAO;
import com.dinidu.lk.pmt.db.DBConnection;
import com.dinidu.lk.pmt.dto.UserDTO;
import com.dinidu.lk.pmt.entity.User;
import java.sql.Connection;

import com.dinidu.lk.pmt.utils.EntityDTOMapper;
import com.dinidu.lk.pmt.utils.permissionTypes.Permission;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.sql.SQLException;

public class UserBOImpl implements UserBO {

    UserDAO userDAO =
            (UserDAO) DAOFactory.getDaoFactory().
                    getDAO(DAOFactory.DAOTypes.USER);

    @Override
    public boolean isEmailRegistered(String email) throws SQLException, ClassNotFoundException {
        return userDAO.isEmailRegistered(email);
    }

    @Override
    public boolean updatePassword(String loggedInUsername, String password) throws SQLException, ClassNotFoundException {
        return userDAO.updatePassword(loggedInUsername, password);
    }

    @Override
    public boolean updatePasswordUsingEmail(String userEmail, String newPassword) throws SQLException, ClassNotFoundException {
        return userDAO.updatePasswordUsingEmail(userEmail, newPassword);
    }

    @Override
    public UserDTO getUserDetailsByUsername(String loggedInUsername) throws SQLException, ClassNotFoundException {
        return EntityDTOMapper.mapEntityToDTO(userDAO.getUserDetailsByUsername(loggedInUsername), UserDTO.class);
    }

    @Override
    public String verifyUser(String username, String password) throws SQLException, ClassNotFoundException {
        return userDAO.verifyUser(username, password);
    }

    @Override
    public boolean saveUser(UserDTO dto) throws SQLException, ClassNotFoundException {
        return userDAO.save(new User(
                dto.getUsername(),
                dto.getFull_name(),
                dto.getPassword(),
                dto.getEmail(),
                dto.getPhoneNumber()));
    }

    @Override
    public boolean updateUser(UserDTO dto) throws SQLException, ClassNotFoundException {
        return userDAO.update(new User(
                dto.getUsername(),
                dto.getFull_name(),
                dto.getPassword(),
                dto.getEmail(),
                dto.getPhoneNumber()));
    }

    @Override
    public String getUserEmail(String loggedInUsername) throws SQLException, ClassNotFoundException {
        return userDAO.getUserEmail(loggedInUsername);
    }

    @Override
    public void updateProfileImagePath(String username, String imagePath) throws SQLException, ClassNotFoundException {
        userDAO.updateProfileImagePath(username, imagePath);
    }

    @Override
    public Long getUserIdByUsername(String username) throws SQLException, ClassNotFoundException {
        return userDAO.getUserIdByUsername(username);
    }

    @Override
    public String getUserFullNameById(Long userId) throws SQLException, ClassNotFoundException {
        return userDAO.getUserFullNameById(userId);
    }

    @Override
    public Image getUserProfilePicByUserId(Long createdBy) throws SQLException, ClassNotFoundException, FileNotFoundException {
        return userDAO.getUserProfilePicByUserId(createdBy);
    }

    @Override
    public boolean updateUserStatus(String username, String status) throws SQLException, ClassNotFoundException {
        return userDAO.updateUserStatus(username, status);
    }

    @Override
    public Long getUserIdByFullName(String selectedMemberName) throws SQLException, ClassNotFoundException {
        return userDAO.getUserIdByFullName(selectedMemberName);
    }

    @Override
    public String getUserEmailById(Long id) throws SQLException, ClassNotFoundException {
        return userDAO.getUserEmailById(id);
    }

    @Override
    public String getUserEmailByFullName(String selectedUser) throws SQLException, ClassNotFoundException {
        return userDAO.getUserEmailByFullName(selectedUser);
    }

    @Override
    public String getUserNameByEmail(String email) throws SQLException, ClassNotFoundException {
        return userDAO.getUserNameByEmail(email);
    }

    @Override
    public String getProfileImagePath(String username) throws SQLException, ClassNotFoundException {
        return userDAO.getProfileImagePath(username);
    }

    // ============= TRANSACTION ==============
    @Override
    public boolean updateUserRoleAndPermissions(String username, UserDTO user) throws SQLException, ClassNotFoundException {
        /*Transaction*/
        Connection connection;
        connection = DBConnection.getInstance().getConnection();

        connection.setAutoCommit(false);
        try{        boolean isRoleUpdated =  userDAO.updateUserRole(user.getRole().getId(), username);
            System.out.println("IsRoleUpdated : "+isRoleUpdated);

            if (!isRoleUpdated){
                connection.rollback();
                System.out.println("Role is Not Updated ... Connection Has Been RollBack.");
                connection.setAutoCommit(true);
                return false;
            }

            System.out.println("Role updated successfully for user: " + username + ", New Role ID: " + user.getRole().getId());

            boolean permissionsDeleted = userDAO.deletePermissionsInCurrentRole(username);

            if (!permissionsDeleted) {
                System.out.println("No existing permissions to delete for user: " + username);
            } else {
                System.out.println("Old permissions deleted successfully for user: " + username);
            }

            for (Permission permission : user.getPermissions()) {
                boolean permissionInserted = userDAO.insertPermissionsInCurrentRole(user.getRole().getId(), permission.getId());
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
        }finally {
            connection.setAutoCommit(true);
        }
    }

    /////////////////// no need to override
    @Override
    public void insert(UserDTO t) throws SQLException, ClassNotFoundException {
    }
    @Override
    public void delete(String idOrName) throws SQLException, ClassNotFoundException {
    }
}
