<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý hồ sơ cá nhân</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<nav class="navbar">
    <h1>Warehouse Management System</h1>
    <div class="user-info">
        <span>Xin chào, <strong>${user.staffName}</strong></span>
        <span>|</span>
        <span>${user.email}</span>
        <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Đăng xuất</a>
    </div>
</nav>
<div class="container mt-4">
    <!-- Button to enable edit mode -->
    <button id="editBtn" class="btn btn-secondary mb-3">Chỉnh sửa hồ sơ</button>
    <h2 class="mb-4">Hồ sơ cá nhân</h2>
    <c:if test="${not empty message}">
        <div class="alert alert-success">${message}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>
    <div class="row">
        <div class="col-md-6">
            <form id="profileForm" action="${pageContext.request.contextPath}/profile" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="updateProfile"/>
                <div class="mb-3">
                    <label for="staffName" class="form-label">Họ và tên</label>
                    <input type="text" class="form-control" id="staffName" name="staffName" value="${user.staffName}" required disabled>
                </div>
                <div class="mb-3">
                    <label for="email" class="form-label">Email</label>
                    <input type="email" class="form-control" id="email" name="email" value="${user.email}" required disabled>
                </div>
                <div class="mb-3">
                    <label for="address" class="form-label">Địa chỉ</label>
                    <input type="text" class="form-control" id="address" name="address" value="${user.address}" disabled>
                </div>
                <div class="mb-3">
                    <label for="username" class="form-label">Tên đăng nhập</label>
                    <input type="text" class="form-control" id="username" name="username" value="${user.username}" required disabled>
                </div>
                <!-- Hiển thị ảnh đại diện hiện tại -->
                <c:if test="${not empty user.userId}">
                    <div class="mb-3 text-center">
                        <img src="${pageContext.request.contextPath}/avatar?userId=${user.userId}" alt="Avatar" class="rounded-circle" width="150" onerror="this.src='https://via.placeholder.com/150'">
                    </div>
                </c:if>
                <div class="mb-3" id="avatarGroup" style="display:none;">
                    <label for="avatar" class="form-label">Ảnh đại diện</label>
                    <input type="file" class="form-control" id="avatar" name="avatar" accept="image/*" disabled>
                </div>
                <button id="saveBtn" type="submit" class="btn btn-primary" style="display:none;">Lưu</button>
                <button id="cancelBtn" type="button" class="btn btn-secondary ms-2" style="display:none;">Hủy</button>
            </form>
        </div>
    </div>
    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/change-password" class="btn btn-warning btn-sm">Đổi mật khẩu</a>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary btn-sm ms-2">Quay lại Dashboard</a>
    </div>
</div>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const form = document.getElementById('profileForm');
        const editBtn = document.getElementById('editBtn');
        const saveBtn = document.getElementById('saveBtn');
        const cancelBtn = document.getElementById('cancelBtn');
        const avatarGroup = document.getElementById('avatarGroup');
        const inputs = form.querySelectorAll('input:not([type=hidden])');
        function setEditable(editable) {
            inputs.forEach(input => input.disabled = !editable);
            avatarGroup.style.display = editable ? 'block' : 'none';
            saveBtn.style.display = editable ? 'inline-block' : 'none';
            cancelBtn.style.display = editable ? 'inline-block' : 'none';
            editBtn.style.display = editable ? 'none' : 'inline-block';
        }
        setEditable(false);
        editBtn.addEventListener('click', function(e) {
            e.preventDefault();
            setEditable(true);
        });
        cancelBtn.addEventListener('click', function() {
            window.location.reload();
        });
    });
</script>
</body>
</html>
