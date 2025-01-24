package com.dinidu.lk.pmt.db;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DBConnection {
    private static DBConnection dbConnection = null;
    private Connection connection;

    private DBConnection() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("env.properties")) {
            if (input == null) {
                new Alert(AlertType.ERROR, "Error: Unable to find env.properties file.").showAndWait();
                return;
            }
            properties.load(input);

            String url = properties.getProperty("DB_URL");
            String user = properties.getProperty("DB_USER");
            String password = properties.getProperty("DB_PASSWORD");

            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            new Alert(AlertType.ERROR, "Database connection failed: " + e.getMessage()).showAndWait();
        } catch (IOException e) {
            new Alert(AlertType.ERROR, "Error reading env.properties file: " + e.getMessage()).showAndWait();
        }
    }

    public static DBConnection getInstance() {
        return (null == dbConnection) ? dbConnection = new DBConnection() : dbConnection;
    }

    public Connection getConnection() {
        return connection;
    }
}

