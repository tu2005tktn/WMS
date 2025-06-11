<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Sửa sản phẩm</title>
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
    <h2>Sửa sản phẩm</h2>
    <form action="${pageContext.request.contextPath}/products" method="post" enctype="multipart/form-data">
        <input type="hidden" name="action" value="update"/>
        <input type="hidden" name="productId" value="${product.productId}"/>
        <div class="mb-3">
            <label class="form-label">Mã sản phẩm</label>
            <input type="text" name="productCode" class="form-control" value="${product.productCode}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Tên sản phẩm</label>
            <input type="text" name="productName" class="form-control" value="${product.productName}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Giá bán</label>
            <input type="number" step="0.01" name="salePrice" class="form-control" value="${product.salePrice}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Giá gốc</label>
            <input type="number" step="0.01" name="cost" class="form-control" value="${product.cost}">
        </div>
        <div class="mb-3">
            <label class="form-label">Danh mục</label>
            <input type="text" name="productCategory" class="form-control" value="${product.productCategory}">
        </div>
        <div class="mb-3">
            <label class="form-label">Mô tả</label>
            <textarea name="productDescription" class="form-control" rows="3">${product.productDescription}</textarea>
        </div>
        <div class="mb-3">
            <label class="form-label">Thuộc tính</label>
            <textarea name="attributes" class="form-control" rows="2">${product.attributes}</textarea>
        </div>
        <div class="mb-3">
            <label class="form-label">Ảnh sản phẩm hiện tại</label>
            <c:choose>
                <c:when test="${not empty product.imageId}">
                    <div><img src="${pageContext.request.contextPath}/productImage?imageId=${product.imageId}" class="img-thumbnail" style="width:150px; height:150px; object-fit:cover;" alt="Ảnh SP"></div>
                </c:when>
                <c:otherwise>
                    <span>Chưa có ảnh</span>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="mb-3">
            <label class="form-label">Thay đổi ảnh mới</label>
            <input type="file" name="productImage" class="form-control" accept="image/*">
        </div>
        <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">Hủy</a>
    </form>
</div>
</body>
</html>
