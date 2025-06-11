package com.warehouse.wms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=InventoryManagementDB;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "123456";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            logger.debug("Database connection established");
            return conn;
        } catch (ClassNotFoundException e) {
            logger.error("SQL Server JDBC Driver not found: {}", e.getMessage(), e);
            throw new SQLException("SQL Server JDBC Driver not found", e);
        } catch (SQLException e) {
            logger.error("Database connection error: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }
}
