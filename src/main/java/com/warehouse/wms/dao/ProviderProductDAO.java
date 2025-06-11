package com.warehouse.wms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.warehouse.wms.model.Product;
import com.warehouse.wms.model.Provider;
import com.warehouse.wms.model.ProviderProduct;
import com.warehouse.wms.util.DatabaseConnection;

public class ProviderProductDAO {
    
    /**
     * Get all provider-product relationships with detailed information
     */
    public List<ProviderProduct> getAllProviderProducts() throws SQLException {
        List<ProviderProduct> providerProducts = new ArrayList<>();
        String sql = """
            SELECT pp.ProviderID, pp.ProductID, pp.DeliveryDuration, pp.EstimatedPrice, pp.Policies,
                   pr.ProviderName, p.ProductName, p.ProductCode, p.ProductCategory, p.SalePrice
            FROM dbo.Provider_Product pp
            INNER JOIN dbo.Provider_Master pr ON pp.ProviderID = pr.ProviderID
            INNER JOIN dbo.Product_Master p ON pp.ProductID = p.ProductID
            ORDER BY pr.ProviderName, p.ProductName
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ProviderProduct pp = mapRowToProviderProduct(rs);
                providerProducts.add(pp);
            }
        }
        return providerProducts;
    }
    
    /**
     * Get provider-product relationships by provider ID
     */
    public List<ProviderProduct> getProviderProductsByProviderId(int providerId) throws SQLException {
        List<ProviderProduct> providerProducts = new ArrayList<>();
        String sql = """
            SELECT pp.ProviderID, pp.ProductID, pp.DeliveryDuration, pp.EstimatedPrice, pp.Policies,
                   pr.ProviderName, p.ProductName, p.ProductCode, p.ProductCategory, p.SalePrice
            FROM dbo.Provider_Product pp
            INNER JOIN dbo.Provider_Master pr ON pp.ProviderID = pr.ProviderID
            INNER JOIN dbo.Product_Master p ON pp.ProductID = p.ProductID
            WHERE pp.ProviderID = ?
            ORDER BY p.ProductName
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, providerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProviderProduct pp = mapRowToProviderProduct(rs);
                    providerProducts.add(pp);
                }
            }
        }
        return providerProducts;
    }
    
    /**
     * Get provider-product relationships by product ID
     */
    public List<ProviderProduct> getProviderProductsByProductId(int productId) throws SQLException {
        List<ProviderProduct> providerProducts = new ArrayList<>();
        String sql = """
            SELECT pp.ProviderID, pp.ProductID, pp.DeliveryDuration, pp.EstimatedPrice, pp.Policies,
                   pr.ProviderName, p.ProductName, p.ProductCode, p.ProductCategory, p.SalePrice
            FROM dbo.Provider_Product pp
            INNER JOIN dbo.Provider_Master pr ON pp.ProviderID = pr.ProviderID
            INNER JOIN dbo.Product_Master p ON pp.ProductID = p.ProductID
            WHERE pp.ProductID = ?
            ORDER BY pr.ProviderName
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProviderProduct pp = mapRowToProviderProduct(rs);
                    providerProducts.add(pp);
                }
            }
        }
        return providerProducts;
    }
    
    /**
     * Get specific provider-product relationship
     */
    public ProviderProduct getProviderProduct(int providerId, int productId) throws SQLException {
        String sql = """
            SELECT pp.ProviderID, pp.ProductID, pp.DeliveryDuration, pp.EstimatedPrice, pp.Policies,
                   pr.ProviderName, p.ProductName, p.ProductCode, p.ProductCategory, p.SalePrice
            FROM dbo.Provider_Product pp
            INNER JOIN dbo.Provider_Master pr ON pp.ProviderID = pr.ProviderID
            INNER JOIN dbo.Product_Master p ON pp.ProductID = p.ProductID
            WHERE pp.ProviderID = ? AND pp.ProductID = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, providerId);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToProviderProduct(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Create new provider-product relationship
     */
    public boolean createProviderProduct(ProviderProduct providerProduct) throws SQLException {
        String sql = """
            INSERT INTO dbo.Provider_Product (ProviderID, ProductID, DeliveryDuration, EstimatedPrice, Policies)
            VALUES (?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, providerProduct.getProviderId());
            stmt.setInt(2, providerProduct.getProductId());
            
            if (providerProduct.getDeliveryDuration() != null) {
                stmt.setInt(3, providerProduct.getDeliveryDuration());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            
            if (providerProduct.getEstimatedPrice() != null) {
                stmt.setBigDecimal(4, providerProduct.getEstimatedPrice());
            } else {
                stmt.setNull(4, java.sql.Types.DECIMAL);
            }
            
            stmt.setString(5, providerProduct.getPolicies());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Update provider-product relationship
     */
    public boolean updateProviderProduct(ProviderProduct providerProduct) throws SQLException {
        String sql = """
            UPDATE dbo.Provider_Product 
            SET DeliveryDuration = ?, EstimatedPrice = ?, Policies = ?
            WHERE ProviderID = ? AND ProductID = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (providerProduct.getDeliveryDuration() != null) {
                stmt.setInt(1, providerProduct.getDeliveryDuration());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            
            if (providerProduct.getEstimatedPrice() != null) {
                stmt.setBigDecimal(2, providerProduct.getEstimatedPrice());
            } else {
                stmt.setNull(2, java.sql.Types.DECIMAL);
            }
            
            stmt.setString(3, providerProduct.getPolicies());
            stmt.setInt(4, providerProduct.getProviderId());
            stmt.setInt(5, providerProduct.getProductId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Delete provider-product relationship
     */
    public boolean deleteProviderProduct(int providerId, int productId) throws SQLException {
        String sql = "DELETE FROM dbo.Provider_Product WHERE ProviderID = ? AND ProductID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, providerId);
            stmt.setInt(2, productId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Check if provider-product relationship exists
     */
    public boolean existsProviderProduct(int providerId, int productId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dbo.Provider_Product WHERE ProviderID = ? AND ProductID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, providerId);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * Get all providers for a specific product (useful for purchase order creation)
     */
    public List<Provider> getProvidersForProduct(int productId) throws SQLException {
        List<Provider> providers = new ArrayList<>();
        String sql = """
            SELECT DISTINCT pr.ProviderID, pr.ProviderName, pr.Email, pr.Address
            FROM dbo.Provider_Product pp
            INNER JOIN dbo.Provider_Master pr ON pp.ProviderID = pr.ProviderID
            WHERE pp.ProductID = ?
            ORDER BY pr.ProviderName
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Provider provider = new Provider();
                    provider.setProviderId(rs.getInt("ProviderID"));
                    provider.setProviderName(rs.getString("ProviderName"));
                    provider.setEmail(rs.getString("Email"));
                    provider.setAddress(rs.getString("Address"));
                    providers.add(provider);
                }
            }
        }
        return providers;
    }
    
    /**
     * Get all products for a specific provider (useful for purchase order creation)
     */
    public List<Product> getProductsForProvider(int providerId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.ProductID, p.ProductCode, p.ProductName, p.SalePrice, p.Cost,
                   p.ProductDescription, p.ProductCategory, p.Attributes
            FROM dbo.Provider_Product pp
            INNER JOIN dbo.Product_Master p ON pp.ProductID = p.ProductID
            WHERE pp.ProviderID = ?
            ORDER BY p.ProductName
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, providerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setProductId(rs.getInt("ProductID"));
                    product.setProductCode(rs.getString("ProductCode"));
                    product.setProductName(rs.getString("ProductName"));
                    product.setSalePrice(rs.getBigDecimal("SalePrice"));
                    product.setCost(rs.getBigDecimal("Cost"));
                    product.setProductDescription(rs.getString("ProductDescription"));
                    product.setProductCategory(rs.getString("ProductCategory"));
                    product.setAttributes(rs.getString("Attributes"));
                    products.add(product);
                }
            }
        }
        return products;
    }
    
    /**
     * Get products not yet associated with a specific provider
     */
    public List<Product> getProductsNotInProvider(int providerId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.ProductID, p.ProductCode, p.ProductName, p.SalePrice, p.Cost,
                   p.ProductDescription, p.ProductCategory, p.Attributes
            FROM dbo.Product_Master p
            WHERE p.ProductID NOT IN (
                SELECT pp.ProductID FROM dbo.Provider_Product pp WHERE pp.ProviderID = ?
            )
            ORDER BY p.ProductName
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, providerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setProductId(rs.getInt("ProductID"));
                    product.setProductCode(rs.getString("ProductCode"));
                    product.setProductName(rs.getString("ProductName"));
                    product.setSalePrice(rs.getBigDecimal("SalePrice"));
                    product.setCost(rs.getBigDecimal("Cost"));
                    product.setProductDescription(rs.getString("ProductDescription"));
                    product.setProductCategory(rs.getString("ProductCategory"));
                    product.setAttributes(rs.getString("Attributes"));
                    products.add(product);
                }
            }
        }
        return products;
    }
    
    /**
     * Search provider-products by provider name or product name
     */
    public List<ProviderProduct> searchProviderProducts(String searchTerm) throws SQLException {
        List<ProviderProduct> providerProducts = new ArrayList<>();
        String sql = """
            SELECT pp.ProviderID, pp.ProductID, pp.DeliveryDuration, pp.EstimatedPrice, pp.Policies,
                   pr.ProviderName, p.ProductName, p.ProductCode, p.ProductCategory, p.SalePrice
            FROM dbo.Provider_Product pp
            INNER JOIN dbo.Provider_Master pr ON pp.ProviderID = pr.ProviderID
            INNER JOIN dbo.Product_Master p ON pp.ProductID = p.ProductID
            WHERE pr.ProviderName LIKE ? OR p.ProductName LIKE ? OR p.ProductCode LIKE ?
            ORDER BY pr.ProviderName, p.ProductName
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProviderProduct pp = mapRowToProviderProduct(rs);
                    providerProducts.add(pp);
                }
            }
        }
        return providerProducts;
    }
    
    /**
     * Get provider-product relationships suitable for purchase order creation
     * Returns only relationships with pricing and delivery information
     */
    public List<ProviderProduct> getProviderProductsForPurchaseOrder() throws SQLException {
        List<ProviderProduct> providerProducts = new ArrayList<>();
        String sql = """
            SELECT pp.ProviderID, pp.ProductID, pp.DeliveryDuration, pp.EstimatedPrice, pp.Policies,
                   pr.ProviderName, p.ProductName, p.ProductCode, p.ProductCategory, p.SalePrice
            FROM dbo.Provider_Product pp
            INNER JOIN dbo.Provider_Master pr ON pp.ProviderID = pr.ProviderID
            INNER JOIN dbo.Product_Master p ON pp.ProductID = p.ProductID
            WHERE pp.EstimatedPrice IS NOT NULL AND pp.EstimatedPrice > 0
            ORDER BY pr.ProviderName, p.ProductName
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ProviderProduct pp = mapRowToProviderProduct(rs);
                providerProducts.add(pp);
            }
        }
        return providerProducts;
    }
    
    /**
     * Get total count of provider-product relationships
     */
    public int getTotalProviderProductCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM dbo.Provider_Product";
        
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
     * Get count of provider-product relationships with complete information
     */
    public int getCompleteProviderProductCount() throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM dbo.Provider_Product 
            WHERE EstimatedPrice IS NOT NULL AND DeliveryDuration IS NOT NULL
            """;
        
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
     * Map ResultSet row to ProviderProduct object
     */
    private ProviderProduct mapRowToProviderProduct(ResultSet rs) throws SQLException {
        ProviderProduct providerProduct = new ProviderProduct();

        providerProduct.setProviderId(rs.getInt("ProviderID"));
        providerProduct.setProductId(rs.getInt("ProductID"));

        // Handle nullable Integer for DeliveryDuration
        int deliveryDurationVal = rs.getInt("DeliveryDuration");
        if (rs.wasNull()) {
            providerProduct.setDeliveryDuration(null);
        } else {
            providerProduct.setDeliveryDuration(deliveryDurationVal);
        }

        // Handle nullable BigDecimal for EstimatedPrice
        // Assumes EstimatedPrice column in DB is DECIMAL or NUMERIC
        providerProduct.setEstimatedPrice(rs.getBigDecimal("EstimatedPrice"));

        providerProduct.setPolicies(rs.getString("Policies"));
        providerProduct.setProviderName(rs.getString("ProviderName"));
        providerProduct.setProductName(rs.getString("ProductName"));
        providerProduct.setProductCode(rs.getString("ProductCode"));
        providerProduct.setProductCategory(rs.getString("ProductCategory"));
        // Note: SalePrice from Product_Master is available in ResultSet (p.SalePrice)
        // but ProviderProduct model doesn't have a field for it. 
        // If needed, add a field to ProviderProduct model and set it here.

        return providerProduct;
    }
}
