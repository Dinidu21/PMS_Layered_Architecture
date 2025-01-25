package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.SQLUtil;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryDAOImpl implements QueryDAO {

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
    } // Working
}
