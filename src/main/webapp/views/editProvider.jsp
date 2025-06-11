<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa nhà cung cấp - WMS</title>
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
        <h2><i class="fas fa-edit"></i> Chỉnh sửa nhà cung cấp</h2>
        <p class="mb-0">Cập nhật thông tin nhà cung cấp</p>
    </div>
    
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-triangle"></i> ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle"></i> ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    
    <form action="${pageContext.request.contextPath}/providers" method="post" id="providerForm" class="form-container">
        <input type="hidden" name="action" value="update"/>
        <input type="hidden" name="providerId" value="${provider.providerId}"/>
        
        <div class="row">
            <div class="col-md-6">
                <div class="mb-3">
                    <label for="providerName" class="form-label required-field">Tên nhà cung cấp</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-building"></i></span>
                        <input type="text" id="providerName" name="providerName" class="form-control" required maxlength="100" 
                               value="${provider.providerName}" placeholder="Nhập tên nhà cung cấp">
                    </div>
                    <div class="form-text">Tên nhà cung cấp là bắt buộc (tối đa 100 ký tự)</div>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="mb-3">
                    <label for="email" class="form-label required-field">Email</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                        <input type="email" id="email" name="email" class="form-control" required maxlength="255" 
                               value="${provider.email}" placeholder="Nhập email nhà cung cấp">
                    </div>
                    <div class="form-text">Email liên lạc (bắt buộc)</div>
                </div>
            </div>
        </div>
        
        <div class="mb-3">
            <label for="address" class="form-label required-field">Địa chỉ</label>
            <div class="input-group">
                <span class="input-group-text"><i class="fas fa-map-marker-alt"></i></span>
                <textarea id="address" name="address" class="form-control" required rows="3" maxlength="500" 
                          placeholder="Nhập địa chỉ nhà cung cấp">${provider.address}</textarea>
            </div>
            <div class="form-text">Địa chỉ trụ sở chính của nhà cung cấp (bắt buộc, tối đa 500 ký tự)</div>
        </div>
        
        <div class="d-flex justify-content-between">
            <a href="${pageContext.request.contextPath}/providers" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Quay lại danh sách
            </a>
            <div>
                <button type="reset" class="btn btn-outline-secondary me-2" onclick="resetForm()">
                    <i class="fas fa-undo"></i> Khôi phục
                </button>
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> Cập nhật nhà cung cấp
                </button>
            </div>
        </div>
    </form>
    
    <div class="card mt-4">
        <div class="card-header bg-light">
            <h6 class="mb-0"><i class="fas fa-info-circle text-info"></i> Hướng dẫn</h6>
        </div>
        <div class="card-body">
            <ul class="mb-0">
                <li><strong>Tên nhà cung cấp:</strong> Là thông tin bắt buộc, tối đa 100 ký tự</li>
                <li><strong>Email:</strong> Bắt buộc, phải đúng định dạng email</li>
                <li><strong>Địa chỉ:</strong> Bắt buộc, địa chỉ trụ sở chính để liên lạc</li>
                <li>Các trường có dấu <span class="text-danger">*</span> là bắt buộc</li>
            </ul>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
        // Form validation
        document.getElementById('providerForm').addEventListener('submit', function(e) {
            const providerName = document.getElementById('providerName').value.trim();
            const email = document.getElementById('email').value.trim();
            const address = document.getElementById('address').value.trim();
            
            if (!providerName) {
                e.preventDefault();
                alert('Vui lòng nhập tên nhà cung cấp!');
                document.getElementById('providerName').focus();
                return;
            }
            
            if (!email) {
                e.preventDefault();
                alert('Vui lòng nhập email!');
                document.getElementById('email').focus();
                return;
            }
            
            if (!isValidEmail(email)) {
                e.preventDefault();
                alert('Định dạng email không hợp lệ!');
                document.getElementById('email').focus();
                return;
            }
            
            if (!address) {
                e.preventDefault();
                alert('Vui lòng nhập địa chỉ!');
                document.getElementById('address').focus();
                return;
            }
        });
        
        function isValidEmail(email) {
            const emailRegex = /^[A-Za-z0-9+_.-]+@(.+)$/;
            return emailRegex.test(email);
        }
        
        function resetForm() {
            document.getElementById('providerName').value = '${provider.providerName}';
            document.getElementById('email').value = '${provider.email}';
            document.getElementById('address').value = '${provider.address}';
        }
        
        // Character counter for text areas
        document.getElementById('address').addEventListener('input', function() {
            const maxLength = 500;
            const currentLength = this.value.length;
            const remaining = maxLength - currentLength;
            
            let helpText = this.parentNode.nextElementSibling;
            if (remaining < 50) {
                helpText.textContent = `Địa chỉ trụ sở chính của nhà cung cấp (còn lại ${remaining} ký tự)`;
                helpText.className = remaining < 10 ? 'form-text text-danger' : 'form-text text-warning';
            } else {
                helpText.textContent = 'Địa chỉ trụ sở chính của nhà cung cấp (bắt buộc, tối đa 500 ký tự)';
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
