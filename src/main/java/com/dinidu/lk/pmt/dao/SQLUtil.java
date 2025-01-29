package com.dinidu.lk.pmt.dao;

import com.dinidu.lk.pmt.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUtil {
    public static <T> T execute(String sql, Object... obj) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pst = connection.prepareStatement(sql);

        // Set parameters for the prepared statement
        for (int i = 0; i < obj.length; i++) {
            pst.setObject((i + 1), obj[i]);
        }

        // Check if the query is a SELECT query
        if (sql.trim().toLowerCase().startsWith("select")) {
            // Execute the query and return the ResultSet
            ResultSet rs = pst.executeQuery();
            return (T) rs;
        } else {
            // For non-SELECT queries, executeUpdate and return the result (true/false)
            boolean isSuccess = pst.executeUpdate() > 0;
            return (T) Boolean.valueOf(isSuccess);
        }
    }
}
