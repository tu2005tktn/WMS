<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý vai trò</title>
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
<div class="container mt-4">
    <h2 class="mb-4">Danh sách vai trò</h2>
    <c:if test="${not empty message}">
        <div class="alert alert-success">${message}</div>
    </c:if>
    <div class="table-responsive">
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <th>ID</th>
                <th>Tên vai trò</th>
                <th>Mô tả</th>
                <th>Ngày tạo</th>
                <th>Hành động</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="role" items="${roles}">
                <tr>
                    <td>${role.roleId}</td>
                    <td>${role.roleName}</td>
                    <td>${role.description}</td>
                    <td>${role.createdDate}</td>
                    <td>
                        <form action="${pageContext.request.contextPath}/roles" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="delete"/>
                            <input type="hidden" name="roleId" value="${role.roleId}"/>
                            <button type="submit" class="btn btn-danger btn-sm">Xóa</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <h3 class="mt-4">Thêm vai trò mới</h3>
    <form action="${pageContext.request.contextPath}/roles" method="post">
        <input type="hidden" name="action" value="create"/>
        <div class="mb-3">
            <label for="roleName" class="form-label">Tên vai trò</label>
            <input type="text" class="form-control" id="roleName" name="roleName" required>
        </div>
        <div class="mb-3">
            <label for="description" class="form-label">Mô tả</label>
            <textarea class="form-control" id="description" name="description" rows="3"></textarea>
        </div>
        <button type="submit" class="btn btn-primary">Thêm</button>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary ms-2">Quay lại Dashboard</a>
    </form>
</div>
</body>
</html>
