<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thêm sản phẩm mới</title>
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
    <h2>Thêm sản phẩm mới</h2>
    <form action="${pageContext.request.contextPath}/products" method="post" enctype="multipart/form-data">
        <input type="hidden" name="action" value="create"/>
        <div class="mb-3">
            <label class="form-label">Mã sản phẩm</label>
            <input type="text" name="productCode" class="form-control" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Tên sản phẩm</label>
            <input type="text" name="productName" class="form-control" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Giá bán</label>
            <input type="number" step="0.01" name="salePrice" class="form-control" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Giá gốc</label>
            <input type="number" step="0.01" name="cost" class="form-control">
        </div>
        <div class="mb-3">
            <label class="form-label">Danh mục</label>
            <input type="text" name="productCategory" class="form-control">
        </div>
        <div class="mb-3">
            <label class="form-label">Mô tả</label>
            <textarea name="productDescription" class="form-control" rows="3"></textarea>
        </div>
        <div class="mb-3">
            <label class="form-label">Thuộc tính</label>
            <textarea name="attributes" class="form-control" rows="2"></textarea>
        </div>
        <div class="mb-3">
            <label class="form-label">Ảnh sản phẩm</label>
            <input type="file" name="productImage" class="form-control" accept="image/*">
        </div>
        <button type="submit" class="btn btn-primary">Lưu</button>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">Hủy</a>
    </form>
</div>
</body>
</html>
