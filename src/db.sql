--------------------------------------------------------------------------------
-- 0. (TÙY CHỌN) XÓA DATABASE NẾU ĐÃ TỒN TẠI
--------------------------------------------------------------------------------
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'InventoryManagementDB')
BEGIN
    ALTER DATABASE [InventoryManagementDB] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE [InventoryManagementDB];
END
GO

--------------------------------------------------------------------------------
-- 1. TẠO DATABASE MỚI VÀ CHUYỂN CONTEXT
--------------------------------------------------------------------------------
CREATE DATABASE [InventoryManagementDB];
GO
USE [InventoryManagementDB];
GO

--------------------------------------------------------------------------------
-- 2. MASTER TABLES
--------------------------------------------------------------------------------
CREATE TABLE dbo.Provider_Master (
    ProviderID     INT            IDENTITY(1,1) PRIMARY KEY,
    ProviderName   NVARCHAR(100)  NOT NULL,
    Email          NVARCHAR(255)  NULL,
    Address        NVARCHAR(255)  NULL
);

CREATE TABLE dbo.Customer_Master (
    CustomerID     INT            IDENTITY(1,1) PRIMARY KEY,
    CustomerName   NVARCHAR(100)  NOT NULL,
    Email          NVARCHAR(255)  NULL,
    Address        NVARCHAR(255)  NULL
);

CREATE TABLE dbo.Purchase_Staff_Master (
    PurchaseStaffID INT            IDENTITY(1,1) PRIMARY KEY,
    StaffName       NVARCHAR(100)  NOT NULL,
    Email           NVARCHAR(100)  NULL UNIQUE,
    Address         NVARCHAR(255)  NULL,
    CreatedDate     DATETIME2      NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE dbo.Warehouse_Master (
    WarehouseID    INT            IDENTITY(1,1) PRIMARY KEY,
    Name           NVARCHAR(100)  NOT NULL,
    Location       NVARCHAR(255)  NULL,
    Description    NVARCHAR(255)  NULL
);

CREATE TABLE dbo.Product_Master (
    ProductID          INT            IDENTITY(1,1) PRIMARY KEY,
    ProductCode        NVARCHAR(50)   NOT NULL,
    ProductName        NVARCHAR(100)  NOT NULL,
    SalePrice          DECIMAL(18,2)  NOT NULL,
    Cost               DECIMAL(18,2)  NULL,
    ProductDescription NVARCHAR(255)  NULL,
    ProductCategory    NVARCHAR(100)  NULL,
    Attributes         NVARCHAR(MAX)  NULL
);

CREATE TABLE dbo.Sale_Condition_Master (
    SaleConditionID   INT            IDENTITY(1,1) PRIMARY KEY,
    ConditionCode     NVARCHAR(50)   NOT NULL,
    Amount            DECIMAL(18,2)  NOT NULL,
    Type              NVARCHAR(50)   NOT NULL,  -- '%','fixed'
    EffectiveDate     DATETIME2      NOT NULL,
    ExpiredDate       DATETIME2      NULL,
    CreatedBy         NVARCHAR(50)   NULL,
    CreatedDate       DATETIME2      NOT NULL DEFAULT SYSUTCDATETIME()
);

--------------------------------------------------------------------------------
-- 3. USER & ROLE TABLES (liên kết với Purchase_Staff_Master)
--------------------------------------------------------------------------------
CREATE TABLE dbo.Role_Master (
    RoleID        INT           IDENTITY(1,1) PRIMARY KEY,
    RoleName      NVARCHAR(50)  NOT NULL UNIQUE,
    Description   NVARCHAR(255) NULL,
    CreatedDate   DATETIME2     NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE dbo.Users (
    UserID           INT           IDENTITY(1,1) PRIMARY KEY,
    PurchaseStaffID  INT           NOT NULL UNIQUE,
    Username         NVARCHAR(50)  NOT NULL UNIQUE,
    PasswordHash     NVARCHAR(255) NOT NULL,
    Status           NVARCHAR(50)  NULL,
    CreatedDate      DATETIME2     NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_Users_PurchaseStaff FOREIGN KEY (PurchaseStaffID)
        REFERENCES dbo.Purchase_Staff_Master(PurchaseStaffID)
);

CREATE TABLE dbo.User_Roles (
    UserID        INT           NOT NULL,
    RoleID        INT           NOT NULL,
    AssignedDate  DATETIME2     NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT PK_User_Roles PRIMARY KEY (UserID, RoleID),
    CONSTRAINT FK_UR_User      FOREIGN KEY (UserID) 
        REFERENCES dbo.Users(UserID),
    CONSTRAINT FK_UR_Role      FOREIGN KEY (RoleID) 
        REFERENCES dbo.Role_Master(RoleID)
);

--------------------------------------------------------------------------------
-- 4. LINK AND TRANSACTION TABLES
--------------------------------------------------------------------------------
CREATE TABLE dbo.Provider_Product (
    ProviderID        INT            NOT NULL,
    ProductID         INT            NOT NULL,
    DeliveryDuration  INT            NULL,
    EstimatedPrice    DECIMAL(18,2)  NULL,
    Policies          NVARCHAR(MAX)  NULL,
    CONSTRAINT PK_Provider_Product PRIMARY KEY (ProviderID, ProductID),
    CONSTRAINT FK_PP_Provider FOREIGN KEY (ProviderID)
        REFERENCES dbo.Provider_Master(ProviderID),
    CONSTRAINT FK_PP_Product  FOREIGN KEY (ProductID)
        REFERENCES dbo.Product_Master(ProductID)
);

CREATE TABLE dbo.PO_Header_Trans (
    POID           INT            IDENTITY(1,1) PRIMARY KEY,
    ProviderID     INT            NOT NULL,
    [Date]         DATETIME2      NOT NULL DEFAULT SYSUTCDATETIME(),
    DeliveryDate   DATETIME2      NULL,
    Status         NVARCHAR(50)   NULL,
    CreatedBy      NVARCHAR(50)   NULL,
    CreatedDate    DATETIME2      NOT NULL DEFAULT SYSUTCDATETIME(),
    Notes NVARCHAR(MAX) NULL,
    CONSTRAINT FK_POH_Provider FOREIGN KEY (ProviderID)
        REFERENCES dbo.Provider_Master(ProviderID)
);

CREATE TABLE dbo.PO_Detail_Trans (
    PODetailID     INT            IDENTITY(1,1) PRIMARY KEY,
    POID           INT            NOT NULL,
    ProductID      INT            NOT NULL,
    Quantity       INT            NOT NULL,
    Price          DECIMAL(18,2)  NOT NULL,
    DeliveryDate   DATETIME2      NULL,
    ActualDelivery DATETIME2      NULL,
    Status         NVARCHAR(50)   NULL,
    CONSTRAINT FK_POD_POH FOREIGN KEY (POID)
        REFERENCES dbo.PO_Header_Trans(POID),
    CONSTRAINT FK_POD_Product FOREIGN KEY (ProductID)
        REFERENCES dbo.Product_Master(ProductID)
);

CREATE TABLE dbo.Transfer_Order_Trans (
    TOID           INT            IDENTITY(1,1) PRIMARY KEY,
    SourceID       INT            NOT NULL,
    DestinationID  INT            NOT NULL,
    DocRefID       NVARCHAR(100)  NULL,
    [Date]         DATETIME2      NOT NULL DEFAULT SYSUTCDATETIME(),
    [Type]         NVARCHAR(50)   NULL,
    Status         NVARCHAR(50)   NULL,
    Description    NVARCHAR(255)  NULL,
    ApprovedBy     NVARCHAR(50)   NULL,
    CreatedBy      NVARCHAR(50)   NULL,
    CreatedDate    DATETIME2      NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_TOT_SourceWarehouse 
        FOREIGN KEY (SourceID) REFERENCES dbo.Warehouse_Master(WarehouseID),
    CONSTRAINT FK_TOT_DestWarehouse  
        FOREIGN KEY (DestinationID) REFERENCES dbo.Warehouse_Master(WarehouseID)
);

CREATE TABLE dbo.Inventory_Trans (
    InventoryID    INT            IDENTITY(1,1) PRIMARY KEY,
    WarehouseID    INT            NOT NULL,
    ProductID      INT            NOT NULL,
    Quantity       INT            NOT NULL,
    Price          DECIMAL(18,2)  NULL,
    TOID           INT            NULL,
    ExpiredDate    DATETIME2      NULL,
    LotNumber      NVARCHAR(50)   NULL,
    CONSTRAINT FK_IT_Warehouse FOREIGN KEY (WarehouseID)
        REFERENCES dbo.Warehouse_Master(WarehouseID),
    CONSTRAINT FK_IT_Product   FOREIGN KEY (ProductID)
        REFERENCES dbo.Product_Master(ProductID),
    CONSTRAINT FK_IT_Transfer  FOREIGN KEY (TOID)
        REFERENCES dbo.Transfer_Order_Trans(TOID)
);

CREATE TABLE dbo.Sale_Order_Trans (
    SID            INT            IDENTITY(1,1) PRIMARY KEY,
    CustomerID     INT            NOT NULL,
    [Date]         DATETIME2      NOT NULL DEFAULT SYSUTCDATETIME(),
    Status         NVARCHAR(50)   NULL,
    CreatedBy      NVARCHAR(50)   NULL,
    CreatedDate    DATETIME2      NOT NULL DEFAULT SYSUTCDATETIME(),
     Notes NVARCHAR(MAX) NULL,
    CONSTRAINT FK_SOT_Customer FOREIGN KEY (CustomerID)
        REFERENCES dbo.Customer_Master(CustomerID)
);

CREATE TABLE dbo.Sale_Order_Detail (
    SaleDetailID     INT            IDENTITY(1,1) PRIMARY KEY,
    SID              INT            NOT NULL,
    ProductID        INT            NOT NULL,
    Quantity         INT            NOT NULL,
    Price            DECIMAL(18,2)  NOT NULL,
    DeliveryDate     DATETIME2      NULL,
    SaleConditionID  INT            NULL,
    CONSTRAINT FK_SOD_SOT       FOREIGN KEY (SID)
        REFERENCES dbo.Sale_Order_Trans(SID),
    CONSTRAINT FK_SOD_Product   FOREIGN KEY (ProductID)
        REFERENCES dbo.Product_Master(ProductID),
    CONSTRAINT FK_SOD_Condition FOREIGN KEY (SaleConditionID)
        REFERENCES dbo.Sale_Condition_Master(SaleConditionID)
);

--------------------------------------------------------------------------------
-- 5. IMAGE TABLES
--------------------------------------------------------------------------------
CREATE TABLE dbo.Product_Images (
    ImageID       INT            IDENTITY(1,1) PRIMARY KEY,
    ProductID     INT            NOT NULL,
    FileName      NVARCHAR(255)  NOT NULL,
    FilePath      NVARCHAR(500)  NULL,
    ContentType   NVARCHAR(100)  NULL,
    ImageData     VARBINARY(MAX) NULL,
    IsPrimary     BIT            NOT NULL DEFAULT(0),
    CreatedBy     NVARCHAR(50)   NULL,
    CreatedDate   DATETIME2      NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_ProductImages_Product FOREIGN KEY (ProductID)
        REFERENCES dbo.Product_Master(ProductID)
);

CREATE TABLE dbo.User_Images (
    ImageID       INT            IDENTITY(1,1) PRIMARY KEY,
    UserID        INT            NOT NULL,
    FileName      NVARCHAR(255)  NOT NULL,
    FilePath      NVARCHAR(500)  NULL,
    ContentType   NVARCHAR(100)  NULL,
    ImageData     VARBINARY(MAX) NULL,
    IsAvatar      BIT            NOT NULL DEFAULT(0),
    CreatedBy     NVARCHAR(50)   NULL,
    CreatedDate   DATETIME2      NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_UserImages_User FOREIGN KEY (UserID)
        REFERENCES dbo.Users(UserID)
);
GO

--------------------------------------------------------------------------------
-- 6. SEED 5 DEFAULT ROLES
--------------------------------------------------------------------------------
INSERT INTO dbo.Role_Master (RoleName, Description)
SELECT r.RoleName, r.Description
FROM (VALUES
    ('Admin',           'Toàn quyền quản trị hệ thống'),
    ('Manager',         'Quản lý cấp trung, theo dõi báo cáo và phê duyệt'),
    ('Purchase Staff',  'Nhân viên mua hàng, tạo và quản lý PO'),
    ('Sales Staff',     'Nhân viên bán hàng, xử lý đơn bán và xuất kho'),
    ('Warehouse Staff', 'Nhân viên kho, nhận và kiểm kê hàng hóa')
) AS r(RoleName, Description)
WHERE NOT EXISTS (
    SELECT 1 FROM dbo.Role_Master rm WHERE rm.RoleName = r.RoleName
);
GO

--------------------------------------------------------------------------------
-- 7. ADD NOTES COLUMN TO PURCHASE ORDER TABLE IF IT DOESN'T EXIST
--------------------------------------------------------------------------------
-- Add Notes column to PO_Header_Trans if it doesn't exist
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[PO_Header_Trans]') AND name = 'Notes')
BEGIN
    ALTER TABLE dbo.PO_Header_Trans ADD Notes NVARCHAR(MAX) NULL;
END
GO

--------------------------------------------------------------------------------
-- 8. SAMPLE DATA FOR TESTING
--------------------------------------------------------------------------------
-- Insert sample providers
INSERT INTO dbo.Provider_Master (ProviderName, Email, Address)
VALUES 
    ('Công ty ABC', 'abc@provider.com', '123 Đường ABC, TP.HCM'),
    ('Nhà cung cấp XYZ', 'xyz@supplier.com', '456 Đường XYZ, Hà Nội'),
    ('Công ty DEF', 'def@company.com', '789 Đường DEF, Đà Nẵng');

-- Insert sample products
INSERT INTO dbo.Product_Master (ProductCode, ProductName, SalePrice, Cost, ProductDescription, ProductCategory)
VALUES 
    ('SP001', 'Sản phẩm A', 100000, 80000, 'Mô tả sản phẩm A', 'Danh mục 1'),
    ('SP002', 'Sản phẩm B', 150000, 120000, 'Mô tả sản phẩm B', 'Danh mục 2'),
    ('SP003', 'Sản phẩm C', 200000, 160000, 'Mô tả sản phẩm C', 'Danh mục 3');

-- Insert sample staff
INSERT INTO dbo.Purchase_Staff_Master (StaffName, Email, Address)
VALUES 
    ('Nguyễn Văn A', 'nvana@company.com', '123 ABC Street'),
    ('Trần Thị B', 'ttb@company.com', '456 XYZ Street');

-- Insert sample users
INSERT INTO dbo.Users (PurchaseStaffID, Username, PasswordHash, Status)
VALUES 
    (1, 'admin', 'hashed_password_admin', 'ACTIVE'),
    (2, 'staff', 'hashed_password_staff', 'ACTIVE');

-- Assign roles to users
INSERT INTO dbo.User_Roles (UserID, RoleID)
VALUES 
    (1, 1), -- admin with Admin role
    (2, 3); -- staff with Purchase Staff role

-- Insert sample provider-product relationships
INSERT INTO dbo.Provider_Product (ProviderID, ProductID, DeliveryDuration, EstimatedPrice, Policies)
VALUES 
    (1, 1, 7, 80000, 'Chính sách giao hàng trong 7 ngày'),
    (1, 2, 10, 120000, 'Chính sách giao hàng trong 10 ngày'),
    (2, 2, 5, 115000, 'Chính sách giao hàng trong 5 ngày'),
    (2, 3, 14, 160000, 'Chính sách giao hàng trong 14 ngày'),
    (3, 1, 3, 75000, 'Chính sách giao hàng trong 3 ngày'),
    (3, 3, 7, 155000, 'Chính sách giao hàng trong 7 ngày');

GO
