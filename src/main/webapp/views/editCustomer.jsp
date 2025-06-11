<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa khách hàng - WMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body { background: #f8f9fa; }
        .form-container { background: #fff; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); padding: 2rem; margin: 2rem 0; }
        .form-header { background: #667eea; color: #fff; border-radius: 10px; padding: 1.5rem; margin-bottom: 2rem; text-align: center; }
        .required-field::after { content: " *"; color: red; }
        .form-control:focus { border-color: #667eea; box-shadow: 0 0 0 0.2rem rgba(102,126,234,0.15); }
        .btn-primary { background: #667eea; border: none; font-weight: 600; }
        .btn-primary:hover { background: #5a67d8; }
        .btn-secondary { font-weight: 600; }
    </style>
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
<div class="container mt-4">
    <div class="form-header">
        <h2><i class="fas fa-user-edit"></i> Chỉnh sửa thông tin khách hàng</h2>
        <p class="mb-0">Cập nhật thông tin khách hàng trong hệ thống</p>
    </div>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-triangle"></i> ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <form action="${pageContext.request.contextPath}/customers" method="post" id="customerForm" class="form-container">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="customerId" value="${customer.customerId}">
        <div class="row">
            <div class="col-md-6">
                <div class="mb-3">
                    <label for="customerName" class="form-label required-field">Tên khách hàng</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-user"></i></span>
                        <input type="text" class="form-control" id="customerName" name="customerName" required maxlength="100" value="${customer.customerName}" placeholder="Nhập tên khách hàng">
                    </div>
                    <div class="form-text">Tên khách hàng là bắt buộc (tối đa 100 ký tự)</div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="mb-3">
                    <label for="email" class="form-label">Email</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                        <input type="email" class="form-control" id="email" name="email" maxlength="255" value="${customer.email}" placeholder="Nhập email khách hàng">
                    </div>
                    <div class="form-text">Email cho việc liên lạc (tùy chọn)</div>
                </div>
            </div>
        </div>
        <div class="mb-3">
            <label for="address" class="form-label">Địa chỉ</label>
            <div class="input-group">
                <span class="input-group-text"><i class="fas fa-map-marker-alt"></i></span>
                <textarea class="form-control" id="address" name="address" rows="3" maxlength="500" placeholder="Nhập địa chỉ khách hàng">${customer.address}</textarea>
            </div>
            <div class="form-text">Địa chỉ liên lạc của khách hàng (tùy chọn, tối đa 500 ký tự)</div>
        </div>
        <div class="d-flex justify-content-between">
            <a href="${pageContext.request.contextPath}/customers" class="btn btn-secondary"><i class="fas fa-arrow-left"></i> Quay lại danh sách</a>
            <div>
                <button type="reset" class="btn btn-outline-secondary me-2"><i class="fas fa-undo"></i> Khôi phục</button>
                <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Cập nhật khách hàng</button>
            </div>
        </div>
    </form>
    <div class="card mt-4">
        <div class="card-header bg-light">
            <h6 class="mb-0"><i class="fas fa-info-circle text-info"></i> Lưu ý khi chỉnh sửa</h6>
        </div>
        <div class="card-body">
            <ul class="mb-0">
                <li><strong>Mã khách hàng (#${customer.customerId}):</strong> Không thể thay đổi</li>
                <li><strong>Tên khách hàng:</strong> Có thể cập nhật, tối đa 100 ký tự</li>
                <li><strong>Email:</strong> Có thể cập nhật hoặc để trống</li>
                <li><strong>Địa chỉ:</strong> Có thể cập nhật hoặc để trống</li>
                <li>Nhấn "Khôi phục" để trở về thông tin ban đầu</li>
            </ul>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
        // Form validation
        document.getElementById('customerForm').addEventListener('submit', function(e) {
            const customerName = document.getElementById('customerName').value.trim();
            const email = document.getElementById('email').value.trim();
            
            if (!customerName) {
                e.preventDefault();
                alert('Vui lòng nhập tên khách hàng!');
                document.getElementById('customerName').focus();
                return;
            }
            
            if (email && !isValidEmail(email)) {
                e.preventDefault();
                alert('Định dạng email không hợp lệ!');
                document.getElementById('email').focus();
                return;
            }
        });
        
        function isValidEmail(email) {
            const emailRegex = /^[A-Za-z0-9+_.-]+@(.+)$/;
            return emailRegex.test(email);
        }
        
        // Character counter for text areas
        document.getElementById('address').addEventListener('input', function() {
            const maxLength = 500;
            const currentLength = this.value.length;
            const remaining = maxLength - currentLength;
            
            let helpText = this.parentNode.nextElementSibling;
            if (remaining < 50) {
                helpText.textContent = `Địa chỉ liên lạc của khách hàng (còn lại ${remaining} ký tự)`;
                helpText.className = remaining < 10 ? 'form-text text-danger' : 'form-text text-warning';
            } else {
                helpText.textContent = 'Địa chỉ liên lạc của khách hàng (tùy chọn, tối đa 500 ký tự)';
                helpText.className = 'form-text';
            }
        });

        // Auto-hide alert messages after 5 seconds
        document.addEventListener('DOMContentLoaded', function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                setTimeout(function() {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                }, 5000);
            });
        });
    </script>
</body>
</html>
