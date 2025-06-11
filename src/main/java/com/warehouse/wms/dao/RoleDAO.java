package com.warehouse.wms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.warehouse.wms.model.Role;
import com.warehouse.wms.util.DatabaseConnection;

public class RoleDAO {
    public List<Role> getAllRoles() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT RoleID, RoleName, Description, CreatedDate FROM Role_Master";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Role role = new Role();
                role.setRoleId(rs.getInt("RoleID"));
                role.setRoleName(rs.getString("RoleName"));
                role.setDescription(rs.getString("Description"));
                role.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                roles.add(role);
            }
        }
        return roles;
    }

    public Role getRoleById(int id) throws SQLException {
        String sql = "SELECT RoleID, RoleName, Description, CreatedDate FROM Role_Master WHERE RoleID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role();
                    role.setRoleId(rs.getInt("RoleID"));
                    role.setRoleName(rs.getString("RoleName"));
                    role.setDescription(rs.getString("Description"));
                    role.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                    return role;
                }
            }
        }
        return null;
    }

    public void createRole(Role role) throws SQLException {
        String sql = "INSERT INTO Role_Master (RoleName, Description) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role.getRoleName());
            stmt.setString(2, role.getDescription());
            stmt.executeUpdate();
        }
    }

    public void updateRole(Role role) throws SQLException {
        String sql = "UPDATE Role_Master SET RoleName = ?, Description = ? WHERE RoleID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role.getRoleName());
            stmt.setString(2, role.getDescription());
            stmt.setInt(3, role.getRoleId());
            stmt.executeUpdate();
        }
    }

    public void deleteRole(int id) throws SQLException {
        String sql = "DELETE FROM Role_Master WHERE RoleID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
