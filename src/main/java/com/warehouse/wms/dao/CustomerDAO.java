package com.warehouse.wms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.warehouse.wms.model.Customer;
import com.warehouse.wms.util.DatabaseConnection;

public class CustomerDAO {
    
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT CustomerID, CustomerName, Email, Address FROM dbo.Customer_Master ORDER BY CustomerName";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("CustomerID"));
                customer.setCustomerName(rs.getString("CustomerName"));
                customer.setEmail(rs.getString("Email"));
                customer.setAddress(rs.getString("Address"));
                customers.add(customer);
            }
        }
        return customers;
    }
    
    public Customer getCustomerById(int id) throws SQLException {
        String sql = "SELECT CustomerID, CustomerName, Email, Address FROM dbo.Customer_Master WHERE CustomerID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("CustomerID"));
                    customer.setCustomerName(rs.getString("CustomerName"));
                    customer.setEmail(rs.getString("Email"));
                    customer.setAddress(rs.getString("Address"));
                    return customer;
                }
            }
        }
        return null;
    }
    
    public void createCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO dbo.Customer_Master (CustomerName, Email, Address) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getAddress());
            stmt.executeUpdate();
        }
    }
    
    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE dbo.Customer_Master SET CustomerName = ?, Email = ?, Address = ? WHERE CustomerID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getAddress());
            stmt.setInt(4, customer.getCustomerId());
            stmt.executeUpdate();
        }
    }
    
    public void deleteCustomer(int id) throws SQLException {
        String sql = "DELETE FROM dbo.Customer_Master WHERE CustomerID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public boolean isCustomerNameExists(String customerName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dbo.Customer_Master WHERE CustomerName = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public boolean isCustomerEmailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dbo.Customer_Master WHERE Email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public boolean isCustomerEmailExistsExcludeId(String email, int customerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dbo.Customer_Master WHERE Email = ? AND CustomerID != ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setInt(2, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public List<Customer> searchCustomers(String searchTerm) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = """
            SELECT CustomerID, CustomerName, Email, Address 
            FROM dbo.Customer_Master 
            WHERE CustomerName LIKE ? OR Email LIKE ? OR Address LIKE ?
            ORDER BY CustomerName
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("CustomerID"));
                    customer.setCustomerName(rs.getString("CustomerName"));
                    customer.setEmail(rs.getString("Email"));
                    customer.setAddress(rs.getString("Address"));
                    customers.add(customer);
                }
            }
        }
        return customers;
    }
}
