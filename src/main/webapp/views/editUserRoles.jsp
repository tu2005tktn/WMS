<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Phân vai trò cho người dùng</title>
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
<div class="container mt-4" style="max-width:600px;">
    <h2 class="mb-4">Phân vai trò cho người: ${user.staffName}</h2>
    <form action="${pageContext.request.contextPath}/users" method="post">
        <input type="hidden" name="action" value="updateRoles"/>
        <input type="hidden" name="userId" value="${user.userId}"/>
        <div class="mb-3">
            <label class="form-label">Chọn vai trò:</label>
            <div class="form-check">
                <c:forEach var="role" items="${allRoles}">
                    <div>
                        <input class="form-check-input" type="checkbox" name="roleId" value="${role.roleId}"
                               id="role${role.roleId}"
                               <c:forEach var="ur" items="${userRoles}">
                                   <c:if test="${ur.roleId == role.roleId}">checked</c:if>
                               </c:forEach> />
                        <label class="form-check-label" for="role${role.roleId}">${role.roleName}</label>
                    </div>
                </c:forEach>
            </div>
        </div>
        <button type="submit" class="btn btn-primary">Lưu</button>
        <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary ms-2">Hủy</a>
    </form>
</div>
</body>
</html>
