<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên mật khẩu - WMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-page">
    <div class="container">
        <h2>Quên mật khẩu</h2>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error">
                ${errorMessage}
            </div>
        </c:if>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">
                ${successMessage}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/forgot" method="post">
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" 
                       value="${email}" required placeholder="Nhập email">
            </div>

            <button type="submit" class="btn">Gửi</button>
        </form>

        <div class="auth-links">
            <p>Bạn đã nhớ mật khẩu? 
                <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
            </p>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            document.getElementById('email').focus();
        });

        const input = document.getElementById('email');
        const errorAlert = document.querySelector('.alert-error');
        if (input && errorAlert) {
            input.addEventListener('input', function() {
                errorAlert.style.display = 'none';
            });
        }
    </script>
</body>
</html>
