package com.warehouse.wms.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.warehouse.wms.model.PurchaseOrder;
import com.warehouse.wms.model.PurchaseOrderDetail;
import com.warehouse.wms.util.DatabaseConnection;

public class PurchaseOrderDAO {

    /**
     * Get all purchase orders with provider information
     */
    public List<PurchaseOrder> getAllPurchaseOrders() throws SQLException {
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        String sql = """
            SELECT po.POID, po.ProviderID, po.[Date], po.DeliveryDate, po.Status, 
                   po.CreatedBy, po.CreatedDate, po.Notes,
                   p.ProviderName, p.Email, p.Address
            FROM dbo.PO_Header_Trans po
            INNER JOIN dbo.Provider_Master p ON po.ProviderID = p.ProviderID
            ORDER BY po.CreatedDate DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                PurchaseOrder po = mapRowToPurchaseOrder(rs);
                // Calculate totals for this purchase order
                calculatePurchaseOrderTotals(po);
                purchaseOrders.add(po);
            }
        }
        return purchaseOrders;
    }

    /**
     * Get purchase order by ID with full details
     */
    public PurchaseOrder getPurchaseOrderById(int poId) throws SQLException {
        String sql = """
            SELECT po.POID, po.ProviderID, po.[Date], po.DeliveryDate, po.Status, 
                   po.CreatedBy, po.CreatedDate, po.Notes,
                   p.ProviderName, p.Email, p.Address
            FROM dbo.PO_Header_Trans po
            INNER JOIN dbo.Provider_Master p ON po.ProviderID = p.ProviderID
            WHERE po.POID = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, poId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PurchaseOrder po = mapRowToPurchaseOrder(rs);
                    // Load details
                    po.setDetails(getPurchaseOrderDetails(poId));
                    po.calculateTotals();
                    return po;
                }
            }
        }
        return null;
    }

    /**
     * Create new purchase order with details
     */
    public int createPurchaseOrder(PurchaseOrder purchaseOrder) throws SQLException {
        String headerSql = """
            INSERT INTO dbo.PO_Header_Trans (ProviderID, DeliveryDate, Status, CreatedBy, Notes)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Insert header
                int poId;
                try (PreparedStatement stmt = conn.prepareStatement(headerSql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, purchaseOrder.getProviderId());
                    
                    if (purchaseOrder.getDeliveryDate() != null) {
                        stmt.setTimestamp(2, Timestamp.valueOf(purchaseOrder.getDeliveryDate()));
                    } else {
                        stmt.setNull(2, Types.TIMESTAMP);
                    }
                    
                    stmt.setString(3, purchaseOrder.getStatus() != null ? purchaseOrder.getStatus() : "PENDING");
                    stmt.setString(4, purchaseOrder.getCreatedBy());
                    stmt.setString(5, purchaseOrder.getNotes());
                    
                    int rows = stmt.executeUpdate();
                    if (rows == 0) {
                        throw new SQLException("Creating purchase order failed, no rows affected.");
                    }
                    
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            poId = generatedKeys.getInt(1);
                        } else {
                            throw new SQLException("Creating purchase order failed, no ID obtained.");
                        }
                    }
                }
                
                // Insert details if provided
                if (purchaseOrder.getDetails() != null && !purchaseOrder.getDetails().isEmpty()) {
                    for (PurchaseOrderDetail detail : purchaseOrder.getDetails()) {
                        detail.setPoId(poId);
                        createPurchaseOrderDetail(conn, detail);
                    }
                }
                
                conn.commit();
                return poId;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Update purchase order header
     */
    public boolean updatePurchaseOrder(PurchaseOrder purchaseOrder) throws SQLException {
        String sql = """
            UPDATE dbo.PO_Header_Trans 
            SET ProviderID = ?, DeliveryDate = ?, Status = ?, Notes = ?
            WHERE POID = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, purchaseOrder.getProviderId());
            
            if (purchaseOrder.getDeliveryDate() != null) {
                stmt.setTimestamp(2, Timestamp.valueOf(purchaseOrder.getDeliveryDate()));
            } else {
                stmt.setNull(2, Types.TIMESTAMP);
            }
            
            stmt.setString(3, purchaseOrder.getStatus());
            stmt.setString(4, purchaseOrder.getNotes());
            stmt.setInt(5, purchaseOrder.getPoId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete purchase order and its details
     */
    public boolean deletePurchaseOrder(int poId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Delete details first
                String deleteDetailsSql = "DELETE FROM dbo.PO_Detail_Trans WHERE POID = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteDetailsSql)) {
                    stmt.setInt(1, poId);
                    stmt.executeUpdate();
                }
                
                // Delete header
                String deleteHeaderSql = "DELETE FROM dbo.PO_Header_Trans WHERE POID = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteHeaderSql)) {
                    stmt.setInt(1, poId);
                    int rows = stmt.executeUpdate();
                    
                    conn.commit();
                    return rows > 0;
                }
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Get purchase order details
     */
    public List<PurchaseOrderDetail> getPurchaseOrderDetails(int poId) throws SQLException {
        List<PurchaseOrderDetail> details = new ArrayList<>();
        String sql = """
            SELECT pod.PODetailID, pod.POID, pod.ProductID, pod.Quantity, pod.Price,
                   pod.DeliveryDate, pod.ActualDelivery, pod.Status,
                   p.ProductCode, p.ProductName, p.ProductCategory, p.Cost
            FROM dbo.PO_Detail_Trans pod
            INNER JOIN dbo.Product_Master p ON pod.ProductID = p.ProductID
            WHERE pod.POID = ?
            ORDER BY pod.PODetailID
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, poId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrderDetail detail = mapRowToPurchaseOrderDetail(rs);
                    detail.calculateLineTotal();
                    details.add(detail);
                }
            }
        }
        return details;
    }

    /**
     * Create purchase order detail (internal method)
     */
    private void createPurchaseOrderDetail(Connection conn, PurchaseOrderDetail detail) throws SQLException {
        String sql = """
            INSERT INTO dbo.PO_Detail_Trans (POID, ProductID, Quantity, Price, DeliveryDate, Status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detail.getPoId());
            stmt.setInt(2, detail.getProductId());
            stmt.setInt(3, detail.getQuantity());
            stmt.setBigDecimal(4, detail.getPrice());
            
            if (detail.getDeliveryDate() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(detail.getDeliveryDate()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            
            stmt.setString(6, detail.getStatus() != null ? detail.getStatus() : "PENDING");
            
            stmt.executeUpdate();
        }
    }

    /**
     * Search purchase orders
     */
    public List<PurchaseOrder> searchPurchaseOrders(String searchTerm) throws SQLException {
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        String sql = """
            SELECT po.POID, po.ProviderID, po.[Date], po.DeliveryDate, po.Status, 
                   po.CreatedBy, po.CreatedDate, po.Notes,
                   p.ProviderName, p.Email, p.Address
            FROM dbo.PO_Header_Trans po
            INNER JOIN dbo.Provider_Master p ON po.ProviderID = p.ProviderID
            WHERE p.ProviderName LIKE ? OR po.Status LIKE ? OR po.CreatedBy LIKE ?
            ORDER BY po.CreatedDate DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrder po = mapRowToPurchaseOrder(rs);
                    calculatePurchaseOrderTotals(po);
                    purchaseOrders.add(po);
                }
            }
        }
        return purchaseOrders;
    }

    /**
     * Get purchase orders by status
     */
    public List<PurchaseOrder> getPurchaseOrdersByStatus(String status) throws SQLException {
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        String sql = """
            SELECT po.POID, po.ProviderID, po.[Date], po.DeliveryDate, po.Status, 
                   po.CreatedBy, po.CreatedDate, po.Notes,
                   p.ProviderName, p.Email, p.Address
            FROM dbo.PO_Header_Trans po
            INNER JOIN dbo.Provider_Master p ON po.ProviderID = p.ProviderID
            WHERE po.Status = ?
            ORDER BY po.CreatedDate DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrder po = mapRowToPurchaseOrder(rs);
                    calculatePurchaseOrderTotals(po);
                    purchaseOrders.add(po);
                }
            }
        }
        return purchaseOrders;
    }

    /**
     * Get total count of purchase orders
     */
    public int getTotalPurchaseOrderCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM dbo.PO_Header_Trans";
        
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
     * Calculate totals for a purchase order
     */
    private void calculatePurchaseOrderTotals(PurchaseOrder purchaseOrder) throws SQLException {
        String sql = """
            SELECT SUM(pod.Quantity * pod.Price) as TotalAmount, SUM(pod.Quantity) as TotalQuantity
            FROM dbo.PO_Detail_Trans pod
            WHERE pod.POID = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, purchaseOrder.getPoId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal totalAmount = rs.getBigDecimal("TotalAmount");
                    int totalQuantity = rs.getInt("TotalQuantity");
                    
                    purchaseOrder.setTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);
                    purchaseOrder.setTotalQuantity(totalQuantity);
                }
            }
        }
    }

    /**
     * Map ResultSet row to PurchaseOrder object
     */
    private PurchaseOrder mapRowToPurchaseOrder(ResultSet rs) throws SQLException {
        PurchaseOrder po = new PurchaseOrder();
        po.setPoId(rs.getInt("POID"));
        po.setProviderId(rs.getInt("ProviderID"));
        
        Timestamp date = rs.getTimestamp("Date");
        if (date != null) {
            po.setDate(date.toLocalDateTime());
        }
        
        Timestamp deliveryDate = rs.getTimestamp("DeliveryDate");
        if (deliveryDate != null) {
            po.setDeliveryDate(deliveryDate.toLocalDateTime());
        }
        
        po.setStatus(rs.getString("Status"));
        po.setCreatedBy(rs.getString("CreatedBy"));
        
        Timestamp createdDate = rs.getTimestamp("CreatedDate");
        if (createdDate != null) {
            po.setCreatedDate(createdDate.toLocalDateTime());
        }
        
        po.setNotes(rs.getString("Notes"));
        po.setProviderName(rs.getString("ProviderName"));
        po.setProviderEmail(rs.getString("Email"));
        po.setProviderAddress(rs.getString("Address"));
        
        return po;
    }

    /**
     * Map ResultSet row to PurchaseOrderDetail object
     */
    private PurchaseOrderDetail mapRowToPurchaseOrderDetail(ResultSet rs) throws SQLException {
        PurchaseOrderDetail detail = new PurchaseOrderDetail();
        detail.setPoDetailId(rs.getInt("PODetailID"));
        detail.setPoId(rs.getInt("POID"));
        detail.setProductId(rs.getInt("ProductID"));
        detail.setQuantity(rs.getInt("Quantity"));
        detail.setPrice(rs.getBigDecimal("Price"));
        
        Timestamp deliveryDate = rs.getTimestamp("DeliveryDate");
        if (deliveryDate != null) {
            detail.setDeliveryDate(deliveryDate.toLocalDateTime());
        }
        
        Timestamp actualDelivery = rs.getTimestamp("ActualDelivery");
        if (actualDelivery != null) {
            detail.setActualDelivery(actualDelivery.toLocalDateTime());
        }
        
        detail.setStatus(rs.getString("Status"));
        detail.setProductCode(rs.getString("ProductCode"));
        detail.setProductName(rs.getString("ProductName"));
        detail.setProductCategory(rs.getString("ProductCategory"));
        detail.setProductCost(rs.getBigDecimal("Cost"));
        
        return detail;
    }
}
