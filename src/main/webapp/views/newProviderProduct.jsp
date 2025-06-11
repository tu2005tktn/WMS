<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm mối quan hệ nhà cung cấp - sản phẩm - WMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <style>
        .form-container {
            background: white;
            border-radius: 15px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            padding: 2rem;
            margin-top: 2rem;
        }
        .form-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 1.5rem;
            border-radius: 15px;
            margin-bottom: 2rem;
            text-align: center;
        }
        .btn-gradient {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 25px;
            padding: 0.75rem 2rem;
            color: white;
            transition: all 0.3s ease;
        }
        .btn-gradient:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
            color: white;
        }
        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }
        .form-select:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }
        .required-field::after {
            content: " *";
            color: red;
        }
    </style>
</head>
<body>
<nav class="navbar">
    <h1><i class="fas fa-warehouse"></i> Warehouse Management System</h1>
    <div class="user-info">
        <c:if test="${not empty sessionScope.user}">
            <span>Xin chào, <strong>${sessionScope.user.staffName}</strong></span>
            <span>|</span>
            <span><i class="fas fa-envelope"></i> ${sessionScope.user.email}</span>
            <a href="${pageContext.request.contextPath}/logout" class="logout-btn">
                <i class="fas fa-sign-out-alt"></i> Đăng xuất
            </a>
        </c:if>
        <c:if test="${empty sessionScope.user}">
            <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-outline-primary btn-sm">Đăng nhập</a>
        </c:if>
    </div>
</nav>

<div class="container mt-4">
    <!-- Form Header -->
    <div class="form-header">
        <h2><i class="fas fa-plus-circle"></i> Thêm mối quan hệ nhà cung cấp - sản phẩm</h2>
        <p class="mb-0">Tạo mới mối quan hệ giữa nhà cung cấp và sản phẩm</p>
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

    <!-- Form -->
    <div class="form-container">
        <form action="${pageContext.request.contextPath}/provider-products" method="post">
            <input type="hidden" name="action" value="create">
            
            <div class="row">
                <div class="col-md-6">
                    <div class="mb-3">
                        <label for="providerId" class="form-label required-field">Nhà cung cấp</label>
                        <select name="providerId" id="providerId" class="form-select" required>
                            <option value="">-- Chọn nhà cung cấp --</option>
                            <c:forEach items="${providers}" var="provider">
                                <option value="${provider.providerId}" 
                                        ${param.providerId == provider.providerId ? 'selected' : ''}>
                                    ${provider.providerName}
                                    <c:if test="${not empty provider.email}">
                                        - ${provider.email}
                                    </c:if>
                                </option>
                            </c:forEach>
                        </select>
                        <div class="form-text">Chọn nhà cung cấp cho mối quan hệ này</div>
                    </div>
                </div>
                
                <div class="col-md-6">
                    <div class="mb-3">
                        <label for="productId" class="form-label required-field">Sản phẩm</label>
                        <select name="productId" id="productId" class="form-select" required>
                            <option value="">-- Chọn sản phẩm --</option>
                            <c:forEach items="${products}" var="product">
                                <option value="${product.productId}" 
                                        ${param.productId == product.productId ? 'selected' : ''}>
                                    ${product.productCode} - ${product.productName}
                                    <c:if test="${not empty product.productCategory}">
                                        (${product.productCategory})
                                    </c:if>
                                </option>
                            </c:forEach>
                        </select>
                        <div class="form-text">Chọn sản phẩm cho mối quan hệ này</div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-6">
                    <div class="mb-3">
                        <label for="deliveryDuration" class="form-label">
                            <i class="fas fa-clock"></i> Thời gian giao hàng (ngày)
                        </label>
                        <input type="number" name="deliveryDuration" id="deliveryDuration" 
                               class="form-control" min="1" max="365" 
                               value="${param.deliveryDuration}"
                               placeholder="Nhập số ngày giao hàng">
                        <div class="form-text">Thời gian giao hàng dự kiến tính theo ngày</div>
                    </div>
                </div>
                
                <div class="col-md-6">
                    <div class="mb-3">
                        <label for="estimatedPrice" class="form-label">
                            <i class="fas fa-dollar-sign"></i> Giá dự kiến (VNĐ)
                        </label>
                        <input type="number" name="estimatedPrice" id="estimatedPrice" 
                               class="form-control" step="0.01" min="0" 
                               value="${param.estimatedPrice}"
                               placeholder="Nhập giá dự kiến">
                        <div class="form-text">Giá dự kiến cho sản phẩm từ nhà cung cấp này</div>
                    </div>
                </div>
            </div>

            <div class="mb-4">
                <label for="policies" class="form-label">
                    <i class="fas fa-file-contract"></i> Chính sách và điều khoản
                </label>
                <textarea name="policies" id="policies" class="form-control" rows="4" 
                          placeholder="Nhập các chính sách, điều khoản đặc biệt...">${param.policies}</textarea>
                <div class="form-text">Ghi chú về chính sách thanh toán, bảo hành, đổi trả, v.v.</div>
            </div>

            <div class="d-flex justify-content-between">
                <a href="${pageContext.request.contextPath}/provider-products" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Quay lại
                </a>
                <div>
                    <button type="reset" class="btn btn-outline-secondary me-2">
                        <i class="fas fa-undo"></i> Làm mới
                    </button>
                    <button type="submit" class="btn btn-gradient">
                        <i class="fas fa-save"></i> Lưu
                    </button>
                </div>
            </div>
        </form>
    </div>
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
        const providerId = document.getElementById('providerId').value;
        const productId = document.getElementById('productId').value;
        
        if (!providerId || !productId) {
            e.preventDefault();
            alert('Vui lòng chọn cả nhà cung cấp và sản phẩm!');
            return false;
        }
        
        // Check for duplicate combination (basic client-side check)
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

    // Enhanced select dropdowns with search capability
    const providerSelect = document.getElementById('providerId');
    const productSelect = document.getElementById('productId');
    
    // Add search functionality to selects (simple implementation)
    [providerSelect, productSelect].forEach(select => {
        select.addEventListener('focus', function() {
            this.size = Math.min(this.options.length, 8);
        });
        
        select.addEventListener('blur', function() {
            this.size = 1;
        });
        
        select.addEventListener('change', function() {
            this.size = 1;
        });
    });
});
</script>
</body>
</html>
