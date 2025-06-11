package com.warehouse.wms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.warehouse.wms.model.SaleCondition;
import com.warehouse.wms.util.DatabaseConnection;

public class SaleConditionDAO {

    /**
     * Get all sale conditions with pagination
     */
    public List<SaleCondition> getAllSaleConditions(int offset, int limit) throws SQLException {
        List<SaleCondition> saleConditions = new ArrayList<>();
        String sql = """
            SELECT SaleConditionID, ConditionCode, Amount, Type, EffectiveDate, 
                   ExpiredDate, CreatedBy, CreatedDate
            FROM dbo.Sale_Condition_Master
            ORDER BY CreatedDate DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offset);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    saleConditions.add(mapRowToSaleCondition(rs));
                }
            }
        }
        return saleConditions;
    }

    /**
     * Get all sale conditions without pagination
     */
    public List<SaleCondition> getAllSaleConditions() throws SQLException {
        List<SaleCondition> saleConditions = new ArrayList<>();
        String sql = """
            SELECT SaleConditionID, ConditionCode, Amount, Type, EffectiveDate, 
                   ExpiredDate, CreatedBy, CreatedDate
            FROM dbo.Sale_Condition_Master
            ORDER BY CreatedDate DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                saleConditions.add(mapRowToSaleCondition(rs));
            }
        }
        return saleConditions;
    }

    /**
     * Get sale condition by ID
     */
    public SaleCondition getSaleConditionById(int id) throws SQLException {
        String sql = """
            SELECT SaleConditionID, ConditionCode, Amount, Type, EffectiveDate, 
                   ExpiredDate, CreatedBy, CreatedDate
            FROM dbo.Sale_Condition_Master
            WHERE SaleConditionID = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToSaleCondition(rs);
                }
            }
        }
        return null;
    }

    /**
     * Get sale condition by condition code
     */
    public SaleCondition getSaleConditionByCode(String conditionCode) throws SQLException {
        String sql = """
            SELECT SaleConditionID, ConditionCode, Amount, Type, EffectiveDate, 
                   ExpiredDate, CreatedBy, CreatedDate
            FROM dbo.Sale_Condition_Master
            WHERE ConditionCode = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, conditionCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToSaleCondition(rs);
                }
            }
        }
        return null;
    }

    /**
     * Create new sale condition
     */
    public boolean createSaleCondition(SaleCondition saleCondition) throws SQLException {
        String sql = """
            INSERT INTO dbo.Sale_Condition_Master (ConditionCode, Amount, Type, EffectiveDate, 
                                                   ExpiredDate, CreatedBy)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, saleCondition.getConditionCode());
            stmt.setBigDecimal(2, saleCondition.getAmount());
            stmt.setString(3, saleCondition.getType());
            stmt.setTimestamp(4, Timestamp.valueOf(saleCondition.getEffectiveDate()));
            
            if (saleCondition.getExpiredDate() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(saleCondition.getExpiredDate()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            
            stmt.setString(6, saleCondition.getCreatedBy());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Update sale condition
     */
    public boolean updateSaleCondition(SaleCondition saleCondition) throws SQLException {
        String sql = """
            UPDATE dbo.Sale_Condition_Master 
            SET ConditionCode = ?, Amount = ?, Type = ?, EffectiveDate = ?, ExpiredDate = ?
            WHERE SaleConditionID = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, saleCondition.getConditionCode());
            stmt.setBigDecimal(2, saleCondition.getAmount());
            stmt.setString(3, saleCondition.getType());
            stmt.setTimestamp(4, Timestamp.valueOf(saleCondition.getEffectiveDate()));
            
            if (saleCondition.getExpiredDate() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(saleCondition.getExpiredDate()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            
            stmt.setInt(6, saleCondition.getSaleConditionId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Delete sale condition
     */
    public boolean deleteSaleCondition(int id) throws SQLException {
        String sql = "DELETE FROM dbo.Sale_Condition_Master WHERE SaleConditionID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Check if condition code exists
     */
    public boolean conditionCodeExists(String conditionCode) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dbo.Sale_Condition_Master WHERE ConditionCode = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, conditionCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Check if condition code exists excluding current ID
     */
    public boolean conditionCodeExists(String conditionCode, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dbo.Sale_Condition_Master WHERE ConditionCode = ? AND SaleConditionID != ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, conditionCode);
            stmt.setInt(2, excludeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Get total count of sale conditions
     */
    public int getTotalSaleConditionCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM dbo.Sale_Condition_Master";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Get active sale conditions
     */
    public List<SaleCondition> getActiveSaleConditions() throws SQLException {
        List<SaleCondition> saleConditions = new ArrayList<>();
        String sql = """
            SELECT SaleConditionID, ConditionCode, Amount, Type, EffectiveDate, 
                   ExpiredDate, CreatedBy, CreatedDate
            FROM dbo.Sale_Condition_Master
            WHERE EffectiveDate <= GETDATE() 
            AND (ExpiredDate IS NULL OR ExpiredDate > GETDATE())
            ORDER BY CreatedDate DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                saleConditions.add(mapRowToSaleCondition(rs));
            }
        }
        return saleConditions;
    }

    /**
     * Search sale conditions by condition code or type
     */
    public List<SaleCondition> searchSaleConditions(String searchTerm) throws SQLException {
        List<SaleCondition> saleConditions = new ArrayList<>();
        String sql = """
            SELECT SaleConditionID, ConditionCode, Amount, Type, EffectiveDate, 
                   ExpiredDate, CreatedBy, CreatedDate
            FROM dbo.Sale_Condition_Master
            WHERE ConditionCode LIKE ? OR Type LIKE ? OR CreatedBy LIKE ?
            ORDER BY CreatedDate DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    saleConditions.add(mapRowToSaleCondition(rs));
                }
            }
        }
        return saleConditions;
    }

    /**
     * Map ResultSet row to SaleCondition object
     */
    private SaleCondition mapRowToSaleCondition(ResultSet rs) throws SQLException {
        SaleCondition sc = new SaleCondition();
        sc.setSaleConditionId(rs.getInt("SaleConditionID"));
        sc.setConditionCode(rs.getString("ConditionCode"));
        sc.setAmount(rs.getBigDecimal("Amount"));
        sc.setType(rs.getString("Type"));
        
        Timestamp effectiveDate = rs.getTimestamp("EffectiveDate");
        if (effectiveDate != null) {
            sc.setEffectiveDate(effectiveDate.toLocalDateTime());
        }
        
        Timestamp expiredDate = rs.getTimestamp("ExpiredDate");
        if (expiredDate != null) {
            sc.setExpiredDate(expiredDate.toLocalDateTime());
        }
        
        sc.setCreatedBy(rs.getString("CreatedBy"));
        
        Timestamp createdDate = rs.getTimestamp("CreatedDate");
        if (createdDate != null) {
            sc.setCreatedDate(createdDate.toLocalDateTime());
        }
        
        return sc;
    }
}
