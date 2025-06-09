# WMS Authentication System

Hệ thống xác thực (đăng nhập, đăng ký, đăng xuất) cho Warehouse Management System sử dụng mô hình MVC với Java Servlet và JSP.

## Tính năng

- ✅ **Đăng ký tài khoản mới** với validation đầy đủ
- ✅ **Đăng nhập** với username/password
- ✅ **Đăng xuất** an toàn
- ✅ **Mã hóa mật khẩu** bằng BCrypt (salt rounds = 12)
- ✅ **Session management** với timeout 30 phút
- ✅ **Authentication filter** bảo vệ các trang cần đăng nhập
- ✅ **Validation** đầu vào phía client và server
- ✅ **Responsive UI** với CSS hiện đại
- ✅ **Error handling** và thông báo user-friendly

## Cấu trúc dự án

```
src/
├── main/
│   ├── java/com/warehouse/wms/
│   │   ├── controller/          # Servlets (Controllers)
│   │   │   ├── LoginServlet.java
│   │   │   ├── RegisterServlet.java
│   │   │   ├── LogoutServlet.java
│   │   │   └── DashboardServlet.java
│   │   ├── model/               # Models
│   │   │   └── User.java
│   │   ├── dao/                 # Data Access Objects
│   │   │   └── UserDAO.java
│   │   ├── filter/              # Filters
│   │   │   └── AuthenticationFilter.java
│   │   └── util/                # Utilities
│   │       ├── DatabaseConnection.java
│   │       └── ValidationUtil.java
│   └── webapp/
│       ├── css/
│       │   └── style.css        # CSS styles
│       ├── views/               # JSP Views
│       │   ├── login.jsp
│       │   ├── register.jsp
│       │   ├── dashboard.jsp
│       │   └── error/
│       │       ├── 404.jsp
│       │       └── 500.jsp
│       ├── WEB-INF/
│       │   └── web.xml
│       └── index.jsp
├── db.sql                       # Database schema
└── test_data.sql               # Test data
```

## Cài đặt và chạy

### 1. Chuẩn bị Database

```sql
-- Chạy script tạo database
sqlcmd -S localhost -U sa -P your_password -i src/db.sql

-- Chạy script tạo test data (tùy chọn)
sqlcmd -S localhost -U sa -P your_password -i src/test_data.sql
```

### 2. Cấu hình Database Connection

Sửa file `src/main/java/com/warehouse/wms/util/DatabaseConnection.java`:

```java
private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=InventoryManagementDB;trustServerCertificate=true";
private static final String USERNAME = "sa"; // Thay bằng username của bạn
private static final String PASSWORD = "123456"; // Thay bằng password của bạn
```

### 3. Build và deploy

```bash
# Build project
mvn clean compile

# Package WAR file  
mvn package

# Deploy to Tomcat (copy file WMS.war vào thư mục webapps của Tomcat)
cp target/WMS.war $CATALINA_HOME/webapps/
```

### 4. Truy cập ứng dụng

- URL: `http://localhost:8080/WMS`
- Trang đăng nhập: `http://localhost:8080/WMS/login`
- Trang đăng ký: `http://localhost:8080/WMS/register`

## Test Accounts (nếu chạy test_data.sql)

| Username | Password | Role    |
|----------|----------|---------|
| admin    | 123456   | ADMIN   |
| manager  | 123456   | MANAGER |
| staff    | 123456   | STAFF   |

## API Endpoints

- `GET /` - Trang chủ
- `GET /login` - Form đăng nhập
- `POST /login` - Xử lý đăng nhập
- `GET /register` - Form đăng ký
- `POST /register` - Xử lý đăng ký
- `GET /logout` - Đăng xuất
- `GET /dashboard` - Trang dashboard (cần đăng nhập)

## Tính năng bảo mật

### 1. Mã hóa mật khẩu
- Sử dụng BCrypt với salt rounds = 12
- Mật khẩu không bao giờ lưu dưới dạng plaintext

### 2. Session Security
- Session timeout 30 phút
- Session invalidation khi logout
- Session-based authentication

### 3. Input Validation
- Validation phía client (JavaScript)
- Validation phía server (Java)
- XSS protection với input sanitization

### 4. Authentication Filter
- Bảo vệ các trang cần đăng nhập
- Tự động redirect về login nếu chưa xác thực

## Dependencies

- **Jakarta Servlet API 6.1.0** - Web servlet framework
- **SQL Server JDBC Driver 12.4.2** - Database connectivity
- **BCrypt 0.4** - Password hashing
- **JSTL 3.0** - JSP Standard Tag Library

## Cấu trúc Database

### Bảng chính liên quan đến authentication:

1. **Purchase_Staff_Master** - Thông tin nhân viên
2. **Users** - Tài khoản đăng nhập
3. **Role_Master** - Vai trò/quyền hạn
4. **User_Roles** - Liên kết user và role

## Customization

### Thêm validation mới
Sửa file `ValidationUtil.java` để thêm các rule validation mới.

### Thay đổi UI
Sửa file `style.css` và các JSP files trong thư mục `views/`.

### Thêm tính năng mới
1. Tạo servlet mới trong package `controller`
2. Tạo DAO methods trong `UserDAO` nếu cần
3. Tạo JSP view tương ứng
4. Cập nhật `AuthenticationFilter` nếu cần bảo vệ

## Troubleshooting

### Lỗi kết nối database
- Kiểm tra SQL Server đã chạy chưa
- Kiểm tra connection string trong `DatabaseConnection.java`
- Kiểm tra firewall và network connectivity

### Lỗi 404 khi truy cập trang
- Kiểm tra URL mapping trong servlet annotations
- Kiểm tra web.xml configuration
- Kiểm tra file JSP có tồn tại không

### Lỗi session/authentication
- Kiểm tra session timeout configuration
- Xóa browser cookies và thử lại
- Kiểm tra filter configuration

## Contributing

Khi thêm tính năng mới:
1. Follow MVC pattern
2. Thêm validation cho tất cả input
3. Cập nhật documentation
4. Test thoroughly trước khi commit

## License

This project is part of SWP391 course.
