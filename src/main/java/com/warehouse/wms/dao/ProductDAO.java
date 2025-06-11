package com.warehouse.wms.dao;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.warehouse.wms.model.Product;
import com.warehouse.wms.model.ProductImage;
import com.warehouse.wms.util.DatabaseConnection;

import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductDAO {
    private static final Logger logger = LoggerFactory.getLogger(ProductDAO.class);

    // Updated to return generated product ID
    public int createProduct(Product p) throws SQLException {
        String sql = "INSERT INTO dbo.Product_Master (ProductCode, ProductName, SalePrice, Cost, ProductDescription, ProductCategory, Attributes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getProductCode());
            stmt.setString(2, p.getProductName());
            stmt.setBigDecimal(3, p.getSalePrice());
            stmt.setBigDecimal(4, p.getCost());
            stmt.setString(5, p.getProductDescription());
            stmt.setString(6, p.getProductCategory());
            stmt.setString(7, p.getAttributes());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    logger.info("Created new product with ID: {}", id);
                    return id;
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating product: {}", e.getMessage(), e);
            throw e;
        }
        return 0;
    }

    public void addProductImage(int productId, Part imagePart) throws SQLException, IOException {
        String sql = "INSERT INTO dbo.Product_Images (ProductID, FileName, ContentType, ImageData, IsPrimary) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setString(2, Paths.get(imagePart.getSubmittedFileName()).getFileName().toString());
            stmt.setString(3, imagePart.getContentType());
            stmt.setBinaryStream(4, imagePart.getInputStream());
            stmt.setBoolean(5, true);
            stmt.executeUpdate();
        }
    }

    public Integer getPrimaryImageId(int productId) throws SQLException {
        String sql = "SELECT ImageID FROM dbo.Product_Images WHERE ProductID = ? AND IsPrimary = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ImageID");
                }
            }
        }
        return null;
    }

    public ProductImage getProductImageById(int imageId) throws SQLException {
        String sql = "SELECT * FROM dbo.Product_Images WHERE ImageID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, imageId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ProductImage img = new ProductImage();
                    img.setImageId(rs.getInt("ImageID"));
                    img.setProductId(rs.getInt("ProductID"));
                    img.setFileName(rs.getString("FileName"));
                    img.setContentType(rs.getString("ContentType"));
                    img.setData(rs.getBytes("ImageData"));
                    img.setPrimary(rs.getBoolean("IsPrimary"));
                    return img;
                }
            }
        }
        return null;
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM dbo.Product_Master";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Product p = mapRowToProduct(rs);
                try {
                    Integer imgId = getPrimaryImageId(p.getProductId());
                    p.setImageId(imgId);
                } catch (SQLException e) {
                    logger.warn("Error getting image for product {}: {}", p.getProductId(), e.getMessage());
                }
                products.add(p);
            }
            logger.info("Retrieved {} products", products.size());
        } catch (SQLException e) {
            logger.error("Error getting all products: {}", e.getMessage(), e);
            throw e;
        }
        return products;
    }

    public Product getProductById(int id) throws SQLException {
        String sql = "SELECT * FROM dbo.Product_Master WHERE ProductID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Product p = mapRowToProduct(rs);
                    Integer imgId = getPrimaryImageId(p.getProductId());
                    p.setImageId(imgId);
                    return p;
                }
            }
        }
        return null;
    }

    public void clearPrimaryImages(int productId) throws SQLException {
        String sql = "UPDATE dbo.Product_Images SET IsPrimary = 0 WHERE ProductID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        }
    }

    public void updateProduct(Product p) throws SQLException {
        String sql = "UPDATE dbo.Product_Master SET ProductCode = ?, ProductName = ?, SalePrice = ?, Cost = ?, ProductDescription = ?, ProductCategory = ?, Attributes = ? WHERE ProductID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getProductCode());
            stmt.setString(2, p.getProductName());
            stmt.setBigDecimal(3, p.getSalePrice());
            stmt.setBigDecimal(4, p.getCost());
            stmt.setString(5, p.getProductDescription());
            stmt.setString(6, p.getProductCategory());
            stmt.setString(7, p.getAttributes());
            stmt.setInt(8, p.getProductId());
            stmt.executeUpdate();
        }
    }

    public void deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM dbo.Product_Master WHERE ProductID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("ProductID"));
        p.setProductCode(rs.getString("ProductCode"));
        p.setProductName(rs.getString("ProductName"));
        p.setSalePrice(rs.getBigDecimal("SalePrice"));
        p.setCost(rs.getBigDecimal("Cost"));
        p.setProductDescription(rs.getString("ProductDescription"));
        p.setProductCategory(rs.getString("ProductCategory"));
        p.setAttributes(rs.getString("Attributes"));
        return p;
    }
}
