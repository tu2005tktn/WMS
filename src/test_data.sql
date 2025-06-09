-- Test data insertion script for WMS authentication system
-- Run this after creating the database with db.sql

USE [InventoryManagementDB];
GO

-- Insert sample roles
INSERT INTO Role_Master (RoleName, Description) VALUES 
('ADMIN', 'Quản trị viên hệ thống'),
('MANAGER', 'Quản lý kho'),
('STAFF', 'Nhân viên kho'),
('VIEWER', 'Người xem báo cáo');

-- Insert sample staff (will be used when registering users)
INSERT INTO Purchase_Staff_Master (StaffName, Email, Address) VALUES 
('Nguyễn Văn Admin', 'admin@wms.com', 'Hà Nội'),
('Trần Thị Manager', 'manager@wms.com', 'Hồ Chí Minh'),
('Lê Văn Staff', 'staff@wms.com', 'Đà Nẵng');

-- Insert sample users (passwords are hashed with BCrypt)
-- Default password for all users: "123456"
INSERT INTO Users (PurchaseStaffID, Username, PasswordHash, Status) VALUES 
(1, 'admin', '$2a$12$8YvVXqKgZXrGVOd3zwJoN.YrKqx4GvH4zK9O5xVjZK5J7xI9aYvLe', 'ACTIVE'),
(2, 'manager', '$2a$12$8YvVXqKgZXrGVOd3zwJoN.YrKqx4GvH4zK9O5xVjZK5J7xI9aYvLe', 'ACTIVE'),
(3, 'staff', '$2a$12$8YvVXqKgZXrGVOd3zwJoN.YrKqx4GvH4zK9O5xVjZK5J7xI9aYvLe', 'ACTIVE');

-- Assign roles to users
INSERT INTO User_Roles (UserID, RoleID) VALUES 
(1, 1), -- admin có role ADMIN
(2, 2), -- manager có role MANAGER  
(3, 3); -- staff có role STAFF

-- Verify the data
SELECT 
    u.UserID,
    u.Username,
    s.StaffName,
    s.Email,
    u.Status,
    r.RoleName
FROM Users u
INNER JOIN Purchase_Staff_Master s ON u.PurchaseStaffID = s.PurchaseStaffID
LEFT JOIN User_Roles ur ON u.UserID = ur.UserID
LEFT JOIN Role_Master r ON ur.RoleID = r.RoleID
ORDER BY u.UserID;

-- Note: Mật khẩu mặc định cho tất cả test users là "123456"
-- Bạn có thể đăng nhập với:
-- Username: admin, Password: 123456
-- Username: manager, Password: 123456  
-- Username: staff, Password: 123456
