<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đổi mật khẩu</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<nav class="navbar">
    <h1>Warehouse Management System</h1>
    <div class="user-info">
        <span>Xin chào, <strong>${sessionScope.user.staffName}</strong></span>
        <span>|</span>
        <span>${sessionScope.user.email}</span>
        <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Đăng xuất</a>
    </div>
</nav>
<div class="container mt-4" style="max-width: 500px;">
    <h2 class="mb-4">Đổi mật khẩu</h2>
    <c:if test="${not empty message}">
        <div class="alert alert-success">${message}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>
    <form action="${pageContext.request.contextPath}/change-password" method="post">
        <div class="mb-3">
            <label for="oldPassword" class="form-label">Mật khẩu cũ</label>
            <input type="password" class="form-control" id="oldPassword" name="oldPassword" required>
        </div>
        <div class="mb-3">
            <label for="newPassword" class="form-label">Mật khẩu mới</label>
            <input type="password" class="form-control" id="newPassword" name="newPassword" required>
        </div>
        <div class="mb-3">
            <label for="confirmPassword" class="form-label">Xác nhận mật khẩu</label>
            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
        </div>
        <button type="submit" class="btn btn-primary">Đổi mật khẩu</button>
        <a href="${pageContext.request.contextPath}/profile" class="btn btn-secondary ms-2">Quay lại hồ sơ</a>
    </form>
</div>
</body>
</html>
