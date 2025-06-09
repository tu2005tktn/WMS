<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WMS - Warehouse Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h2>Warehouse Management System</h2>
        <p style="text-align: center; margin-bottom: 30px; color: #666;">
            Hệ thống quản lý kho hàng chuyên nghiệp
        </p>
        
        <div style="text-align: center;">
            <a href="${pageContext.request.contextPath}/login" 
               class="btn" style="display: inline-block; text-decoration: none; margin-bottom: 15px;">
                Đăng nhập
            </a>
            <br>
            <div class="auth-links">
                <p>Chưa có tài khoản? 
                    <a href="${pageContext.request.contextPath}/register">Đăng ký ngay</a>
                </p>
            </div>
        </div>
        
        <div style="margin-top: 40px; padding-top: 20px; border-top: 1px solid #eee;">
            <h3 style="color: #333; margin-bottom: 15px;">Tính năng chính:</h3>
            <ul style="color: #666; line-height: 1.6;">
                <li>Quản lý sản phẩm và tồn kho</li>
                <li>Theo dõi nhập xuất hàng</li>
                <li>Quản lý nhà cung cấp và khách hàng</li>
                <li>Báo cáo và thống kê chi tiết</li>
                <li>Bảo mật thông tin với mã hóa mật khẩu</li>
            </ul>
        </div>
    </div>
    
    <script>
        // Check if user is already logged in
        <% if (session.getAttribute("user") != null) { %>
            window.location.href = '${pageContext.request.contextPath}/dashboard';
        <% } %>
    </script>
</body>
</html>