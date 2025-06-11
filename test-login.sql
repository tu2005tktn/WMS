-- Test script to create a test user with proper BCrypt hashed password
-- Password for admin: "admin123"
-- Password for staff: "staff123"

USE [InventoryManagementDB];
GO

-- Delete existing test users
DELETE FROM dbo.User_Roles WHERE UserID IN (1, 2);
DELETE FROM dbo.Users WHERE UserID IN (1, 2);
DELETE FROM dbo.Purchase_Staff_Master WHERE PurchaseStaffID IN (1, 2);
GO

-- Insert test staff with proper IDs
SET IDENTITY_INSERT dbo.Purchase_Staff_Master ON;
INSERT INTO dbo.Purchase_Staff_Master (PurchaseStaffID, StaffName, Email, Address)
VALUES 
    (1, 'Admin User', 'admin@company.com', '123 Admin Street'),
    (2, 'Staff User', 'staff@company.com', '456 Staff Street');
SET IDENTITY_INSERT dbo.Purchase_Staff_Master OFF;
GO

-- Insert test users with BCrypt hashed passwords
-- admin123 -> $2a$12$Rv.8YWCjHF5/X2eGNiKzaOsN4/ZpK5gXl.6E8KpZ0qN7vL3WsPRte
-- staff123 -> $2a$12$5wCK4ZXZFZ1K1VTvPJJJ8OqJYUdBjLEGQrZXPCnLgU8a8.VzWOFV2
SET IDENTITY_INSERT dbo.Users ON;
INSERT INTO dbo.Users (UserID, PurchaseStaffID, Username, PasswordHash, Status)
VALUES 
    (1, 1, 'admin', '$2a$12$Rv.8YWCjHF5/X2eGNiKzaOsN4/ZpK5gXl.6E8KpZ0qN7vL3WsPRte', 'ACTIVE'),
    (2, 2, 'staff', '$2a$12$5wCK4ZXZFZ1K1VTvPJJJ8OqJYUdBjLEGQrZXPCnLgU8a8.VzWOFV2', 'ACTIVE');
SET IDENTITY_INSERT dbo.Users OFF;
GO

-- Insert user roles
INSERT INTO dbo.User_Roles (UserID, RoleID)
VALUES 
    (1, 1), -- admin with Admin role (assuming RoleID 1 is Admin)
    (2, 3); -- staff with Purchase Staff role (assuming RoleID 3 is Purchase Staff)
GO
