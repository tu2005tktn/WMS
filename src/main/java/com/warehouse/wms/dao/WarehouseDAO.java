package com.warehouse.wms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.warehouse.wms.model.Warehouse;
import com.warehouse.wms.util.DatabaseConnection;

public class WarehouseDAO {
    
    public List<Warehouse> getAllWarehouses() throws SQLException {
        List<Warehouse> warehouses = new ArrayList<>();
        String sql = "SELECT WarehouseID, Name, Location, Description FROM Warehouse_Master";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Warehouse warehouse = new Warehouse();
                warehouse.setWarehouseId(rs.getInt("WarehouseID"));
                warehouse.setName(rs.getString("Name"));
                warehouse.setLocation(rs.getString("Location"));
                warehouse.setDescription(rs.getString("Description"));
                warehouses.add(warehouse);
            }
        }
        return warehouses;
    }
    
    public Warehouse getWarehouseById(int id) throws SQLException {
        String sql = "SELECT WarehouseID, Name, Location, Description FROM Warehouse_Master WHERE WarehouseID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Warehouse warehouse = new Warehouse();
                    warehouse.setWarehouseId(rs.getInt("WarehouseID"));
                    warehouse.setName(rs.getString("Name"));
                    warehouse.setLocation(rs.getString("Location"));
                    warehouse.setDescription(rs.getString("Description"));
                    return warehouse;
                }
            }
        }
        return null;
    }
    
    public void createWarehouse(Warehouse warehouse) throws SQLException {
        String sql = "INSERT INTO Warehouse_Master (Name, Location, Description) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, warehouse.getName());
            stmt.setString(2, warehouse.getLocation());
            stmt.setString(3, warehouse.getDescription());
            stmt.executeUpdate();
        }
    }
    
    public void updateWarehouse(Warehouse warehouse) throws SQLException {
        String sql = "UPDATE Warehouse_Master SET Name = ?, Location = ?, Description = ? WHERE WarehouseID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, warehouse.getName());
            stmt.setString(2, warehouse.getLocation());
            stmt.setString(3, warehouse.getDescription());
            stmt.setInt(4, warehouse.getWarehouseId());
            stmt.executeUpdate();
        }
    }
    
    public void deleteWarehouse(int id) throws SQLException {
        String sql = "DELETE FROM Warehouse_Master WHERE WarehouseID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public boolean isWarehouseNameExists(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Warehouse_Master WHERE Name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public boolean isWarehouseNameExistsExcludingId(String name, int warehouseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Warehouse_Master WHERE Name = ? AND WarehouseID != ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, warehouseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public int getTotalWarehouseCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Warehouse_Master";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    public List<Warehouse> searchWarehouses(String searchTerm) throws SQLException {
        List<Warehouse> warehouses = new ArrayList<>();
        String sql = "SELECT WarehouseID, Name, Location, Description FROM Warehouse_Master " +
                    "WHERE Name LIKE ? OR Location LIKE ? OR Description LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Warehouse warehouse = new Warehouse();
                    warehouse.setWarehouseId(rs.getInt("WarehouseID"));
                    warehouse.setName(rs.getString("Name"));
                    warehouse.setLocation(rs.getString("Location"));
                    warehouse.setDescription(rs.getString("Description"));
                    warehouses.add(warehouse);
                }
            }
        }
        return warehouses;
    }
}
