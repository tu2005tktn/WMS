<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết sản phẩm</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
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
    <h2>Chi tiết sản phẩm</h2>
    <div class="row">
        <div class="col-md-4">
            <c:choose>
                <c:when test="${not empty product.imageId}">
                    <img src="${pageContext.request.contextPath}/productImage?imageId=${product.imageId}" class="img-fluid" alt="Ảnh sản phẩm">
                </c:when>
                <c:otherwise>
                    <span>Không có ảnh sản phẩm</span>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="col-md-8">
            <table class="table table-bordered">
                <tr><th>ID</th><td>${product.productId}</td></tr>
                <tr><th>Mã sản phẩm</th><td>${product.productCode}</td></tr>
                <tr><th>Tên sản phẩm</th><td>${product.productName}</td></tr>
                <tr><th>Giá bán</th><td>${product.salePrice}</td></tr>
                <tr><th>Giá gốc</th><td>${product.cost}</td></tr>
                <tr><th>Danh mục</th><td>${product.productCategory}</td></tr>
                <tr><th>Mô tả</th><td>${product.productDescription}</td></tr>
                <tr><th>Thuộc tính</th><td>${product.attributes}</td></tr>
            </table>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">Quay lại danh sách</a>
        </div>
    </div>
</div>
</body>
</html>
