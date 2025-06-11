package com.warehouse.wms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.warehouse.wms.model.Provider;
import com.warehouse.wms.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProviderDAO {
    private static final Logger logger = LoggerFactory.getLogger(ProviderDAO.class);
    
    public List<Provider> getAllProviders() throws SQLException {
        List<Provider> providers = new ArrayList<>();
        String sql = "SELECT ProviderID, ProviderName, Email, Address FROM Provider_Master";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Provider provider = new Provider();
                provider.setProviderId(rs.getInt("ProviderID"));
                provider.setProviderName(rs.getString("ProviderName"));
                provider.setEmail(rs.getString("Email"));
                provider.setAddress(rs.getString("Address"));
                providers.add(provider);
            }
            logger.info("Retrieved {} providers", providers.size());
        } catch (SQLException e) {
            logger.error("Error getting all providers: {}", e.getMessage(), e);
            throw e;
        }
        return providers;
    }
    
    public Provider getProviderById(int id) throws SQLException {
        String sql = "SELECT ProviderID, ProviderName, Email, Address FROM Provider_Master WHERE ProviderID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Provider provider = new Provider();
                    provider.setProviderId(rs.getInt("ProviderID"));
                    provider.setProviderName(rs.getString("ProviderName"));
                    provider.setEmail(rs.getString("Email"));
                    provider.setAddress(rs.getString("Address"));
                    logger.info("Retrieved provider with ID: {}", id);
                    return provider;
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting provider with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
        logger.warn("Provider with ID {} not found", id);
        return null;
    }
    
    public void createProvider(Provider provider) throws SQLException {
        String sql = "INSERT INTO Provider_Master (ProviderName, Email, Address) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, provider.getProviderName());
            stmt.setString(2, provider.getEmail());
            stmt.setString(3, provider.getAddress());
            stmt.executeUpdate();
            logger.info("Created new provider: {}", provider.getProviderName());
        } catch (SQLException e) {
            logger.error("Error creating provider: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    public void updateProvider(Provider provider) throws SQLException {
        String sql = "UPDATE Provider_Master SET ProviderName = ?, Email = ?, Address = ? WHERE ProviderID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, provider.getProviderName());
            stmt.setString(2, provider.getEmail());
            stmt.setString(3, provider.getAddress());
            stmt.setInt(4, provider.getProviderId());
            stmt.executeUpdate();
            logger.info("Updated provider with ID: {}", provider.getProviderId());
        } catch (SQLException e) {
            logger.error("Error updating provider with ID {}: {}", provider.getProviderId(), e.getMessage(), e);
            throw e;
        }
    }
    
    public void deleteProvider(int id) throws SQLException {
        String sql = "DELETE FROM Provider_Master WHERE ProviderID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            logger.info("Deleted provider with ID: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting provider with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
    
    public boolean isProviderNameExists(String providerName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Provider_Master WHERE ProviderName = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, providerName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean exists = rs.getInt(1) > 0;
                    logger.debug("Provider name '{}' exists: {}", providerName, exists);
                    return exists;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking provider name existence: {}", e.getMessage(), e);
            throw e;
        }
        return false;
    }
    
    public boolean isProviderEmailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Provider_Master WHERE Email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean exists = rs.getInt(1) > 0;
                    logger.debug("Provider email '{}' exists: {}", email, exists);
                    return exists;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking provider email existence: {}", e.getMessage(), e);
            throw e;
        }
        return false;
    }
    
    public boolean isProviderEmailExistsExcludeId(String email, int providerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Provider_Master WHERE Email = ? AND ProviderID != ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setInt(2, providerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean exists = rs.getInt(1) > 0;
                    logger.debug("Provider email '{}' exists (excluding ID {}): {}", email, providerId, exists);
                    return exists;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking provider email existence: {}", e.getMessage(), e);
            throw e;
        }
        return false;
    }
}
