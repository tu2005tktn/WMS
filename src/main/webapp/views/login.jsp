<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - WMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h2>Đăng nhập hệ thống WMS</h2>
        
        <!-- Hiển thị thông báo lỗi -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error">
                ${errorMessage}
            </div>
        </c:if>
        
        <!-- Hiển thị thông báo thành công -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">
                ${successMessage}
            </div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="form-group">
                <label for="username">Tên đăng nhập:</label>
                <input type="text" id="username" name="username" 
                       value="${username}" required 
                       placeholder="Nhập tên đăng nhập">
            </div>
            
            <div class="form-group">
                <label for="password">Mật khẩu:</label>
                <input type="password" id="password" name="password" 
                       required placeholder="Nhập mật khẩu">
            </div>
            
            <button type="submit" class="btn">Đăng nhập</button>
        </form>
        
        <div class="auth-links">
            <p>Chưa có tài khoản? 
                <a href="${pageContext.request.contextPath}/register">Đăng ký ngay</a>
            </p>
        </div>
    </div>
    
    <script>
        // Focus vào trường username khi trang load
        document.addEventListener('DOMContentLoaded', function() {
            document.getElementById('username').focus();
        });
        
        // Xóa thông báo lỗi khi user bắt đầu nhập
        const inputs = document.querySelectorAll('input');
        const errorAlert = document.querySelector('.alert-error');
        
        inputs.forEach(input => {
            input.addEventListener('input', function() {
                if (errorAlert) {
                    errorAlert.style.display = 'none';
                }
            });
        });
    </script>
</body>
</html>
