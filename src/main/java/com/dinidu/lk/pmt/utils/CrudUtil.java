package com.dinidu.lk.pmt.utils;

import com.dinidu.lk.pmt.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CrudUtil {

    // This class contains utility methods for executing CRUD operations (Create, Read, Update, Delete) with the database.
    public static <T> T execute(String sql, Object... obj) {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Set parameters in the prepared statement
        for (int i = 0; i < obj.length; i++) {
            try {
                pst.setObject((i + 1), obj[i]);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (sql.trim().toLowerCase().startsWith("select")) {
            // Handle SELECT queries by executing query and returning a ResultSet
            ResultSet resultSet = null;
            try {
                resultSet = pst.executeQuery();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return (T) resultSet; // Return ResultSet for SELECT
        } else {
            // Handle non-SELECT queries by executing update
            int affectedRows = 0;
            try {
                affectedRows = pst.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            boolean isSaved = affectedRows > 0;
            return (T) Boolean.valueOf(isSaved); // Return success status for INSERT, UPDATE, DELETE
        }
    }
}