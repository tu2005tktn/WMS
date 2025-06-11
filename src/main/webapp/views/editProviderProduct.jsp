<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa mối quan hệ nhà cung cấp - sản phẩm - WMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body { background: #f8f9fa; }
        .form-container { background: #fff; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); padding: 2rem; margin: 2rem 0; }
        .form-header { background: #667eea; color: #fff; border-radius: 10px; padding: 1.5rem; margin-bottom: 2rem; text-align: center; }
        .required-field::after { content: " *"; color: red; }
        .form-control:focus { border-color: #667eea; box-shadow: 0 0 0 0.2rem rgba(102,126,234,0.15); }
        .btn-primary, .btn-gradient { background: #667eea; border: none; font-weight: 600; color: #fff; }
        .btn-primary:hover, .btn-gradient:hover { background: #5a67d8; color: #fff; }
        .btn-secondary { font-weight: 600; }
        .readonly-field { background-color: #f8f9fa; color: #6c757d; }
        .info-card { background: #f8f9fa; border-left: 4px solid #667eea; padding: 1rem; margin-bottom: 2rem; border-radius: 0 10px 10px 0; }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light mb-4">
    <div class="container-fluid">
        <span class="navbar-brand"><i class="fas fa-warehouse"></i> WMS</span>
        <div class="d-flex align-items-center ms-auto">
            <c:if test="${not empty sessionScope.user}">
                <span class="me-3">Xin chào, <strong>${sessionScope.user.staffName}</strong></span>
                <span class="me-3"><i class="fas fa-envelope"></i> ${sessionScope.user.email}</span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-secondary btn-sm">Đăng xuất</a>
            </c:if>
            <c:if test="${empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-outline-primary btn-sm">Đăng nhập</a>
            </c:if>
        </div>
    </div>
</nav>
<div class="container mt-4">
    <div class="form-header">
        <h2><i class="fas fa-edit"></i> Chỉnh sửa mối quan hệ nhà cung cấp - sản phẩm</h2>
        <p class="mb-0">Cập nhật thông tin mối quan hệ</p>
    </div>
    <div class="info-card">
        <h5><i class="fas fa-info-circle"></i> Thông tin hiện tại</h5>
        <div class="row">
            <div class="col-md-6">
                <strong>Nhà cung cấp:</strong> ${providerProduct.providerName} (ID: ${providerProduct.providerId})
            </div>
            <div class="col-md-6">
                <strong>Sản phẩm:</strong> ${providerProduct.productName} (${providerProduct.productCode})
            </div>
        </div>
    </div>
    <!-- Messages -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-triangle"></i> ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${not empty message}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle"></i> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <form action="${pageContext.request.contextPath}/provider-products" method="post" class="form-container">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="providerId" value="${providerProduct.providerId}">
        <input type="hidden" name="productId" value="${providerProduct.productId}">
        <div class="row">
            <div class="col-md-6">
                <div class="mb-3">
                    <label for="providerInfo" class="form-label">Nhà cung cấp</label>
                    <input type="text" id="providerInfo" class="form-control readonly-field" value="${providerProduct.providerName}" readonly>
                    <div class="form-text">Không thể thay đổi nhà cung cấp trong mối quan hệ hiện tại</div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="mb-3">
                    <label for="productInfo" class="form-label">Sản phẩm</label>
                    <input type="text" id="productInfo" class="form-control readonly-field" value="${providerProduct.productCode} - ${providerProduct.productName}" readonly>
                    <div class="form-text">Không thể thay đổi sản phẩm trong mối quan hệ hiện tại</div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-6">
                <div class="mb-3">
                    <label for="deliveryDuration" class="form-label"><i class="fas fa-clock"></i> Thời gian giao hàng (ngày)</label>
                    <input type="number" name="deliveryDuration" id="deliveryDuration" class="form-control" min="1" max="365" value="${providerProduct.deliveryDuration}" placeholder="Nhập số ngày giao hàng">
                    <div class="form-text">Thời gian giao hàng dự kiến tính theo ngày</div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="mb-3">
                    <label for="estimatedPrice" class="form-label"><i class="fas fa-dollar-sign"></i> Giá dự kiến (VNĐ)</label>
                    <input type="number" name="estimatedPrice" id="estimatedPrice" class="form-control" step="0.01" min="0" value="${providerProduct.estimatedPrice}" placeholder="Nhập giá dự kiến">
                    <div class="form-text">Giá dự kiến cho sản phẩm từ nhà cung cấp này</div>
                </div>
            </div>
        </div>
        <div class="mb-4">
            <label for="policies" class="form-label"><i class="fas fa-file-contract"></i> Chính sách và điều khoản</label>
            <textarea name="policies" id="policies" class="form-control" rows="4" placeholder="Nhập các chính sách, điều khoản đặc biệt...">${providerProduct.policies}</textarea>
            <div class="form-text">Ghi chú về chính sách thanh toán, bảo hành, đổi trả, v.v.</div>
        </div>
        <div class="d-flex justify-content-between">
            <a href="${pageContext.request.contextPath}/provider-products" class="btn btn-secondary"><i class="fas fa-arrow-left"></i> Quay lại</a>
            <div>
                <button type="reset" class="btn btn-outline-secondary me-2" onclick="resetForm()"><i class="fas fa-undo"></i> Khôi phục</button>
                <button type="submit" class="btn btn-gradient"><i class="fas fa-save"></i> Cập nhật</button>
            </div>
        </div>
    </form>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function() {
    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
    // Form validation
    const form = document.querySelector('form');
    form.addEventListener('submit', function(e) {
        const deliveryDuration = document.getElementById('deliveryDuration').value;
        const estimatedPrice = document.getElementById('estimatedPrice').value;
        if (deliveryDuration && (deliveryDuration < 1 || deliveryDuration > 365)) {
            e.preventDefault();
            alert('Thời gian giao hàng phải từ 1 đến 365 ngày!');
            return false;
        }
        if (estimatedPrice && estimatedPrice < 0) {
            e.preventDefault();
            alert('Giá dự kiến không thể âm!');
            return false;
        }
    });
});
function resetForm() {
    document.getElementById('deliveryDuration').value = '${providerProduct.deliveryDuration}';
    document.getElementById('estimatedPrice').value = '${providerProduct.estimatedPrice}';
    document.getElementById('policies').value = '${providerProduct.policies}';
}
</script>
</body>
</html>
