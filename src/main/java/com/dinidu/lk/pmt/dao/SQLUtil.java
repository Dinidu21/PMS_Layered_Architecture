package com.dinidu.lk.pmt.dao;

import com.dinidu.lk.pmt.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLUtil {
    public static <T>T execute (String sql, Object... obj) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pst = connection.prepareStatement(sql);

        for(int i = 0; i < obj.length; i++) {
            pst.setObject((i + 1), obj[i]);
        }

        if(sql.startsWith("select") || sql.startsWith("SELECT")) {
            return (T) pst.executeQuery();
        } else {
            return (T)(Boolean)(pst.executeUpdate() > 0);
        }
    }
}
