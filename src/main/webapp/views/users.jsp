<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý người dùng</title>
    <!-- Bootstrap CSS -->
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
    <h2 class="mb-4">Danh sách người dùng</h2>
    <div class="table-responsive">
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <th>ID</th>
                <th>Tên</th>
                <th>Username</th>
                <th>Email</th>
                <th>Vai trò</th>  <!-- Added column header for roles -->
                <th>Trạng thái</th>
                <th>Hành động</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="user" items="${users}">
                <tr>
                    <td>${user.userId}</td>
                    <td>${user.staffName}</td>
                    <td>${user.username}</td>
                    <td>${user.email}</td>
                    <td>
                        <c:forEach var="role" items="${userRolesMap[user.userId]}">
                            <span class="badge bg-secondary me-1">${role.roleName}</span>
                        </c:forEach>
                    </td>  <!-- Display assigned roles -->
                    <td>
                        <c:choose>
                            <c:when test="${user.status == 'ACTIVE'}">
                                <span style="color:green;">${user.status}</span>
                            </c:when>
                            <c:otherwise>
                                <span style="color:red;">${user.status}</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <!-- Button to open role assignment page -->
                        <a href="${pageContext.request.contextPath}/users?action=editRoles&userId=${user.userId}" class="btn btn-primary btn-sm me-1">Phân vai trò</a>
                        <c:choose>
                            <c:when test="${user.status == 'ACTIVE'}">
                                <form action="${pageContext.request.contextPath}/users" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="deactivate"/>
                                    <input type="hidden" name="userId" value="${user.userId}"/>
                                    <button type="submit" class="btn btn-danger btn-sm">Vô hiệu hóa</button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <button type="button" class="btn btn-secondary btn-sm" disabled>Vô hiệu hóa</button>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary btn-sm">Quay lại Dashboard</a>
</div>
</body>
</html>
