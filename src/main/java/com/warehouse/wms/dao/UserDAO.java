package com.warehouse.wms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import com.warehouse.wms.model.Role;
import com.warehouse.wms.model.User;
import com.warehouse.wms.model.UserImage;
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
    
    // Lấy tất cả người dùng
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.UserID, u.PurchaseStaffID, u.Username, u.Status, u.CreatedDate, " +
                     "s.StaffName, s.Email, s.Address " +
                     "FROM Users u " +
                     "INNER JOIN Purchase_Staff_Master s ON u.PurchaseStaffID = s.PurchaseStaffID";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setPurchaseStaffId(rs.getInt("PurchaseStaffID"));
                user.setUsername(rs.getString("Username"));
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
    
    // Cập nhật thông tin người dùng
    public boolean updateUser(User user) {
        Connection conn = null;
        PreparedStatement pstmtStaff = null;
        PreparedStatement pstmtUser = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            // Cập nhật thông tin staff
            String staffSql = "UPDATE Purchase_Staff_Master SET StaffName = ?, Email = ?, Address = ? WHERE PurchaseStaffID = ?";
            pstmtStaff = conn.prepareStatement(staffSql);
            pstmtStaff.setString(1, user.getStaffName());
            pstmtStaff.setString(2, user.getEmail());
            pstmtStaff.setString(3, user.getAddress());
            pstmtStaff.setInt(4, user.getPurchaseStaffId());
            pstmtStaff.executeUpdate();
            // Cập nhật thông tin user
            String userSql = "UPDATE Users SET Username = ?, Status = ? WHERE UserID = ?";
            pstmtUser = conn.prepareStatement(userSql);
            pstmtUser.setString(1, user.getUsername());
            pstmtUser.setString(2, user.getStatus());
            pstmtUser.setInt(3, user.getUserId());
            pstmtUser.executeUpdate();
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
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
    
    // Vô hiệu hóa người dùng (deactivate)
    public boolean deactivateUser(int userId) {
        String sql = "UPDATE Users SET Status = 'INACTIVE' WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật mật khẩu mới cho user dựa trên email
     * @param email email của user
     * @param newPasswordHash mật khẩu đã được hash
     * @return true nếu cập nhật thành công
     */
    public boolean updatePasswordByEmail(String email, String newPasswordHash) {
        String sql = "UPDATE Users SET PasswordHash = ? WHERE PurchaseStaffID = " +
                     "(SELECT PurchaseStaffID FROM Purchase_Staff_Master WHERE Email = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPasswordHash);
            pstmt.setString(2, email);
            int updated = pstmt.executeUpdate();
            return updated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật mật khẩu theo UserID
     * @param userId ID của user
     * @param newPasswordHash mật khẩu đã hash
     * @return true nếu cập nhật thành công
     */
    public boolean updatePasswordById(int userId, String newPasswordHash) {
        String sql = "UPDATE Users SET PasswordHash = ? WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPasswordHash);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lưu ảnh đại diện cho user
    public boolean saveUserAvatar(int userId, String fileName, String contentType, byte[] imageData) {
        String deactivateSql = "UPDATE User_Images SET IsAvatar = 0 WHERE UserID = ?";
        String insertSql = "INSERT INTO User_Images (UserID, FileName, FilePath, ContentType, ImageData, IsAvatar, CreatedBy) " +
                           "VALUES (?, ?, NULL, ?, ?, 1, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(deactivateSql);
                 PreparedStatement ps2 = conn.prepareStatement(insertSql)) {
                ps1.setInt(1, userId);
                ps1.executeUpdate();

                ps2.setInt(1, userId);
                ps2.setString(2, fileName);
                ps2.setString(3, contentType);
                ps2.setBytes(4, imageData);
                ps2.setString(5, null);
                ps2.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy avatar của user
    public UserImage getUserAvatar(int userId) {
        String sql = "SELECT ContentType, ImageData, FileName FROM User_Images WHERE UserID = ? AND IsAvatar = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String type = rs.getString("ContentType");
                byte[] data = rs.getBytes("ImageData");
                String name = rs.getString("FileName");
                return new UserImage(data, type, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy danh sách roles đã gán cho user
     */
    public List<Role> getRolesByUserId(int userId) throws SQLException {
        String sql = "SELECT r.RoleID, r.RoleName, r.Description, r.CreatedDate " +
                     "FROM Role_Master r " +
                     "INNER JOIN User_Roles ur ON r.RoleID = ur.RoleID " +
                     "WHERE ur.UserID = ?";
        List<Role> roles = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Role role = new Role();
                    role.setRoleId(rs.getInt("RoleID"));
                    role.setRoleName(rs.getString("RoleName"));
                    role.setDescription(rs.getString("Description"));
                    role.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                    roles.add(role);
                }
            }
        }
        return roles;
    }

    /**
     * Cập nhật roles cho user (xóa roles cũ, thêm roles mới)
     */
    public boolean updateUserRoles(int userId, List<Integer> roleIds) throws SQLException {
        String deleteSql = "DELETE FROM User_Roles WHERE UserID = ?";
        String insertSql = "INSERT INTO User_Roles (UserID, RoleID) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement delStmt = conn.prepareStatement(deleteSql)) {
                delStmt.setInt(1, userId);
                delStmt.executeUpdate();
            }
            if (roleIds != null) {
                try (PreparedStatement insStmt = conn.prepareStatement(insertSql)) {
                    for (Integer roleId : roleIds) {
                        insStmt.setInt(1, userId);
                        insStmt.setInt(2, roleId);
                        insStmt.addBatch();
                    }
                    insStmt.executeBatch();
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            throw e;
        }
    }
}
