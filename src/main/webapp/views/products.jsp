<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">    <title>Quản lý sản phẩm</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
</head>
<body>
<nav class="navbar bg-light px-3">
    <h1>Warehouse Management System</h1>
    <div class="ms-auto">
        <span>Xin chào, <strong>${sessionScope.user.staffName}</strong></span>
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-secondary btn-sm ms-2">Đăng xuất</a>
    </div>
</nav>
<div class="container mt-4">
    <h2 class="mb-3">Danh sách sản phẩm</h2>
    <div class="mb-3">
        <a href="${pageContext.request.contextPath}/products?action=new" class="btn btn-success">Thêm sản phẩm</a>
        <a href="${pageContext.request.contextPath}/provider-products" class="btn btn-info ms-2">
            <i class="fas fa-handshake"></i> Quản lý nhà cung cấp
        </a>
    </div>
    <div class="table-responsive">
        <table class="table table-striped">
            <thead>
            <tr>
                <th>Ảnh</th>
                <th>ID</th>
                <th>Mã SP</th>
                <th>Tên SP</th>
                <th>Giá bán</th>
                <th>Giá gốc</th>
                <th>Danh mục</th>
                <th>Mô tả</th>
                <th>Thuộc tính</th>
                <th>Hành động</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="p" items="${products}">
                <tr>
                    <td>
                        <c:choose>
                            <c:when test="${not empty p.imageId}">
                                <img src="${pageContext.request.contextPath}/productImage?imageId=${p.imageId}" alt="Ảnh SP" class="img-thumbnail" style="width: 80px; height: 80px; object-fit:cover;">
                            </c:when>
                            <c:otherwise>
                                <span>Không có ảnh</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>${p.productId}</td>
                    <td>${p.productCode}</td>
                    <td>${p.productName}</td>
                    <td>${p.salePrice}</td>
                    <td>${p.cost}</td>
                    <td>${p.productCategory}</td>
                    <td>${p.productDescription}</td>
                    <td>${p.attributes}</td>                    <td>
                        <a href="${pageContext.request.contextPath}/products?action=detail&productId=${p.productId}" class="btn btn-info btn-sm me-1">Chi tiết</a>
                        <a href="${pageContext.request.contextPath}/products?action=edit&productId=${p.productId}" class="btn btn-primary btn-sm me-1">Sửa</a>
                        <a href="${pageContext.request.contextPath}/provider-products?action=by-product&productId=${p.productId}" class="btn btn-warning btn-sm me-1" title="Xem nhà cung cấp">
                            <i class="fas fa-truck"></i>
                        </a>
                        <form action="${pageContext.request.contextPath}/products" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="delete"/>
                            <input type="hidden" name="productId" value="${p.productId}"/>
                            <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Bạn có chắc muốn xóa sản phẩm này?');">Xóa</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary">Quay lại Dashboard</a>
</div>
</body>
</html>
