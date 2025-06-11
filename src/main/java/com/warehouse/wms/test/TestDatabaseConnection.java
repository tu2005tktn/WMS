package com.warehouse.wms.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.warehouse.wms.util.DatabaseConnection;

public class TestDatabaseConnection {
    
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("✓ Database connection successful!");
            
            // Test providers table
            String sql = "SELECT COUNT(*) as count FROM Provider_Master";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    int count = rs.getInt("count");
                    System.out.println("✓ Provider_Master table has " + count + " records");
                }
            }
            
            // Test products table
            sql = "SELECT COUNT(*) as count FROM Product_Master";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    int count = rs.getInt("count");
                    System.out.println("✓ Product_Master table has " + count + " records");
                }
            }
            
            // Test purchase order table
            sql = "SELECT COUNT(*) as count FROM PO_Header_Trans";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    int count = rs.getInt("count");
                    System.out.println("✓ PO_Header_Trans table has " + count + " records");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Database connection failed!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
