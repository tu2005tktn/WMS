<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa kho hàng - WMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light mb-4">
    <div class="container-fluid">
        <span class="navbar-brand"><i class="fas fa-warehouse"></i> WMS</span>
        <div class="d-flex align-items-center ms-auto">
            <span class="me-3">Xin chào, <strong>${sessionScope.user.staffName}</strong></span>
            <span class="me-3"><i class="fas fa-envelope"></i> ${sessionScope.user.email}</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-secondary btn-sm">Đăng xuất</a>
        </div>
    </div>
</nav>
<div class="container mt-4" style="max-width: 600px;">
    <h2 class="mb-4 text-center">Chỉnh sửa kho hàng</h2>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">${errorMessage}</div>
    </c:if>
    <form action="${pageContext.request.contextPath}/warehouses" method="post">
        <input type="hidden" name="action" value="update"/>
        <input type="hidden" name="warehouseId" value="${warehouse.warehouseId}"/>
        <div class="mb-3">
            <label class="form-label">Tên kho</label>
            <input type="text" name="name" class="form-control" value="${warehouse.name}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Địa điểm</label>
            <input type="text" name="location" class="form-control" value="${warehouse.location}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Mô tả</label>
            <textarea name="description" class="form-control" rows="3">${warehouse.description}</textarea>
        </div>
        <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
        <a href="${pageContext.request.contextPath}/warehouses" class="btn btn-secondary ms-2">Hủy</a>
    </form>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
