package com.warehouse.wms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import com.warehouse.wms.model.User;
import com.warehouse.wms.util.DatabaseConnection;

public class UserDAO {
    
    // Đăng ký người dùng mới
    public boolean registerUser(String staffName, String email, String address, String username, String password) {
        Connection conn = null;
        PreparedStatement pstmtStaff = null;
        PreparedStatement pstmtUser = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction
            
            // 1. Kiểm tra username đã tồn tại chưa
            if (isUsernameExists(username)) {
                return false;
            }
            
            // 2. Thêm staff vào Purchase_Staff_Master
            String staffSql = "INSERT INTO Purchase_Staff_Master (StaffName, Email, Address) VALUES (?, ?, ?)";
            pstmtStaff = conn.prepareStatement(staffSql, Statement.RETURN_GENERATED_KEYS);
            pstmtStaff.setString(1, staffName);
            pstmtStaff.setString(2, email);
            pstmtStaff.setString(3, address);
            
            int staffRows = pstmtStaff.executeUpdate();
            if (staffRows == 0) {
                conn.rollback();
                return false;
            }
            
            // Lấy PurchaseStaffID vừa tạo
            rs = pstmtStaff.getGeneratedKeys();
            int staffId = 0;
            if (rs.next()) {
                staffId = rs.getInt(1);
            }
            
            // 3. Hash mật khẩu và thêm user
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
            String userSql = "INSERT INTO Users (PurchaseStaffID, Username, PasswordHash, Status) VALUES (?, ?, ?, 'ACTIVE')";
            pstmtUser = conn.prepareStatement(userSql);
            pstmtUser.setInt(1, staffId);
            pstmtUser.setString(2, username);
            pstmtUser.setString(3, hashedPassword);
            
            int userRows = pstmtUser.executeUpdate();
            if (userRows == 0) {
                conn.rollback();
                return false;
            }
            
            conn.commit(); // Commit transaction
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmtStaff != null) pstmtStaff.close();
                if (pstmtUser != null) pstmtUser.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Đăng nhập
    public User loginUser(String username, String password) {
        String sql = "SELECT u.UserID, u.PurchaseStaffID, u.Username, u.PasswordHash, u.Status, u.CreatedDate, " +
                    "s.StaffName, s.Email, s.Address " +
                    "FROM Users u " +
                    "INNER JOIN Purchase_Staff_Master s ON u.PurchaseStaffID = s.PurchaseStaffID " +
                    "WHERE u.Username = ? AND u.Status = 'ACTIVE'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("PasswordHash");
                // Kiểm tra mật khẩu với BCrypt
                if (BCrypt.checkpw(password, storedHash)) {
                    User user = new User();
                    user.setUserId(rs.getInt("UserID"));
                    user.setPurchaseStaffId(rs.getInt("PurchaseStaffID"));
                    user.setUsername(rs.getString("Username"));
                    user.setPasswordHash(rs.getString("PasswordHash"));
                    user.setStatus(rs.getString("Status"));
                    user.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                    user.setStaffName(rs.getString("StaffName"));
                    user.setEmail(rs.getString("Email"));
                    user.setAddress(rs.getString("Address"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Kiểm tra username đã tồn tại
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Kiểm tra email đã tồn tại
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Purchase_Staff_Master WHERE Email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Lấy user theo ID
    public User getUserById(int userId) {
        String sql = "SELECT u.UserID, u.PurchaseStaffID, u.Username, u.PasswordHash, u.Status, u.CreatedDate, " +
                    "s.StaffName, s.Email, s.Address " +
                    "FROM Users u " +
                    "INNER JOIN Purchase_Staff_Master s ON u.PurchaseStaffID = s.PurchaseStaffID " +
                    "WHERE u.UserID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setPurchaseStaffId(rs.getInt("PurchaseStaffID"));
                user.setUsername(rs.getString("Username"));
                user.setPasswordHash(rs.getString("PasswordHash"));
                user.setStatus(rs.getString("Status"));
                user.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                user.setStaffName(rs.getString("StaffName"));
                user.setEmail(rs.getString("Email"));
                user.setAddress(rs.getString("Address"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Cập nhật trạng thái user
    public boolean updateUserStatus(int userId, String status) {
        String sql = "UPDATE Users SET Status = ? WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Lấy tất cả users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.UserID, u.PurchaseStaffID, u.Username, u.PasswordHash, u.Status, u.CreatedDate, " +
                    "s.StaffName, s.Email, s.Address " +
                    "FROM Users u " +
                    "INNER JOIN Purchase_Staff_Master s ON u.PurchaseStaffID = s.PurchaseStaffID " +
                    "ORDER BY u.CreatedDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setPurchaseStaffId(rs.getInt("PurchaseStaffID"));
                user.setUsername(rs.getString("Username"));
                user.setPasswordHash(rs.getString("PasswordHash"));
                user.setStatus(rs.getString("Status"));
                user.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                user.setStaffName(rs.getString("StaffName"));
                user.setEmail(rs.getString("Email"));
                user.setAddress(rs.getString("Address"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
