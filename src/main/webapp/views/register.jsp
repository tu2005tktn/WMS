<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký - WMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .container {
            max-width: 500px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Đăng ký tài khoản WMS</h2>
        
        <!-- Hiển thị thông báo lỗi -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error">
                ${errorMessage}
            </div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/register" method="post" id="registerForm">
            <div class="form-group">
                <label for="staffName">Họ và tên: <span style="color: red;">*</span></label>
                <input type="text" id="staffName" name="staffName" 
                       value="${staffName}" required 
                       placeholder="Nhập họ và tên đầy đủ">
            </div>
            
            <div class="form-group">
                <label for="email">Email: <span style="color: red;">*</span></label>
                <input type="email" id="email" name="email" 
                       value="${email}" required 
                       placeholder="Nhập địa chỉ email">
            </div>
            
            <div class="form-group">
                <label for="address">Địa chỉ:</label>
                <input type="text" id="address" name="address" 
                       value="${address}" 
                       placeholder="Nhập địa chỉ (tùy chọn)">
            </div>
            
            <div class="form-group">
                <label for="username">Tên đăng nhập: <span style="color: red;">*</span></label>
                <input type="text" id="username" name="username" 
                       value="${username}" required 
                       placeholder="Nhập tên đăng nhập (ít nhất 3 ký tự)"
                       minlength="3">
            </div>
            
            <div class="form-group">
                <label for="password">Mật khẩu: <span style="color: red;">*</span></label>
                <input type="password" id="password" name="password" 
                       required placeholder="Nhập mật khẩu (ít nhất 6 ký tự)"
                       minlength="6">
            </div>
            
            <div class="form-group">
                <label for="confirmPassword">Xác nhận mật khẩu: <span style="color: red;">*</span></label>
                <input type="password" id="confirmPassword" name="confirmPassword" 
                       required placeholder="Nhập lại mật khẩu">
                <small id="passwordMatchMsg" style="color: red; display: none;">
                    Mật khẩu xác nhận không khớp
                </small>
            </div>
            
            <button type="submit" class="btn" id="submitBtn">Đăng ký</button>
        </form>
        
        <div class="auth-links">
            <p>Đã có tài khoản? 
                <a href="${pageContext.request.contextPath}/login">Đăng nhập ngay</a>
            </p>
        </div>
    </div>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const form = document.getElementById('registerForm');
            const password = document.getElementById('password');
            const confirmPassword = document.getElementById('confirmPassword');
            const passwordMatchMsg = document.getElementById('passwordMatchMsg');
            const submitBtn = document.getElementById('submitBtn');
            
            // Focus vào trường đầu tiên
            document.getElementById('staffName').focus();
            
            // Kiểm tra mật khẩu khớp
            function checkPasswordMatch() {
                if (confirmPassword.value && password.value !== confirmPassword.value) {
                    passwordMatchMsg.style.display = 'block';
                    confirmPassword.style.borderColor = '#dc3545';
                    return false;
                } else {
                    passwordMatchMsg.style.display = 'none';
                    confirmPassword.style.borderColor = '#ddd';
                    return true;
                }
            }
            
            // Event listeners cho password validation
            password.addEventListener('input', checkPasswordMatch);
            confirmPassword.addEventListener('input', checkPasswordMatch);
            
            // Validate form trước khi submit
            form.addEventListener('submit', function(e) {
                if (!checkPasswordMatch()) {
                    e.preventDefault();
                    confirmPassword.focus();
                    return false;
                }
                
                // Disable submit button để tránh double submit
                submitBtn.disabled = true;
                submitBtn.textContent = 'Đang xử lý...';
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
        });
    </script>
</body>
</html>
