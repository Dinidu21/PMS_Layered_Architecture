package com.dinidu.lk.pmt.dao;

import com.dinidu.lk.pmt.utils.userTypes.UserRole;

import java.sql.SQLException;

public interface QueryDAO extends SuperDAO{
    UserRole getUserRoleByUsername(String username) throws SQLException,ClassNotFoundException;

}
