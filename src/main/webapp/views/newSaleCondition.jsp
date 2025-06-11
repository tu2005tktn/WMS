<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm điều kiện bán hàng mới - WMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <style>
        .page-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 2rem 0;
            border-radius: 15px;
            margin-bottom: 2rem;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        .form-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            padding: 2rem;
        }
        .form-label {
            font-weight: 500;
            color: #495057;
        }
        .required::after {
            content: " *";
            color: #dc3545;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <!-- Navigation -->
        <nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
            <div class="container-fluid">
                <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">
                    <i class="fas fa-warehouse me-2"></i>WMS
                </a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarNav">
                    <ul class="navbar-nav me-auto">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">
                                <i class="fas fa-home me-1"></i>Dashboard
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/sale-conditions">
                                <i class="fas fa-tags me-1"></i>Sale Conditions
                            </a>
                        </li>
                    </ul>
                    <ul class="navbar-nav">
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown">
                                <i class="fas fa-user me-1"></i>${user.staffName}
                            </a>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile">Profile</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Logout</a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>

        <!-- Page Header -->
        <div class="page-header text-center">
            <div class="container">
                <h1 class="display-5 fw-bold mb-0">
                    <i class="fas fa-plus-circle me-3"></i>Thêm Điều kiện Bán hàng Mới
                </h1>
                <p class="lead mb-0">Tạo điều kiện bán hàng, khuyến mãi mới</p>
            </div>
        </div>

        <!-- Breadcrumb -->
        <nav aria-label="breadcrumb" class="mb-4">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/sale-conditions">Sale Conditions</a></li>
                <li class="breadcrumb-item active">Thêm mới</li>
            </ol>
        </nav>

        <!-- Error Messages -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle me-2"></i>${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Form -->
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                <div class="form-card">
                    <form method="post" action="${pageContext.request.contextPath}/sale-conditions" id="saleConditionForm">
                        <input type="hidden" name="action" value="create">
                        
                        <!-- Condition Code -->
                        <div class="mb-3">
                            <label for="conditionCode" class="form-label required">Mã điều kiện</label>
                            <input type="text" class="form-control" id="conditionCode" name="conditionCode" 
                                   value="${conditionCode}" maxlength="50" required>
                            <div class="form-text">Mã định danh duy nhất cho điều kiện bán hàng (tối đa 50 ký tự)</div>
                        </div>

                        <!-- Type -->
                        <div class="mb-3">
                            <label for="type" class="form-label required">Loại giảm giá</label>
                            <select class="form-select" id="type" name="type" required onchange="updateAmountLabel()">
                                <option value="">Chọn loại giảm giá</option>
                                <option value="percentage" ${type == 'percentage' ? 'selected' : ''}>Phần trăm (%)</option>
                                <option value="fixed" ${type == 'fixed' ? 'selected' : ''}>Số tiền cố định (₫)</option>
                            </select>
                        </div>

                        <!-- Amount -->
                        <div class="mb-3">
                            <label for="amount" class="form-label required" id="amountLabel">Giá trị</label>
                            <div class="input-group">
                                <input type="number" class="form-control" id="amount" name="amount" 
                                       value="${amount}" step="0.01" min="0.01" required>
                                <span class="input-group-text" id="amountUnit">-</span>
                            </div>
                            <div class="form-text" id="amountHelp">Nhập giá trị giảm giá</div>
                        </div>

                        <!-- Effective Date -->
                        <div class="mb-3">
                            <label for="effectiveDate" class="form-label required">Ngày bắt đầu hiệu lực</label>
                            <input type="datetime-local" class="form-control" id="effectiveDate" name="effectiveDate" 
                                   value="${effectiveDate}" required>
                            <div class="form-text">Ngày và giờ điều kiện bắt đầu có hiệu lực</div>
                        </div>

                        <!-- Expired Date -->
                        <div class="mb-3">
                            <label for="expiredDate" class="form-label">Ngày hết hạn</label>
                            <input type="datetime-local" class="form-control" id="expiredDate" name="expiredDate" 
                                   value="${expiredDate}">
                            <div class="form-text">Để trống nếu không có ngày hết hạn</div>
                        </div>

                        <!-- Buttons -->
                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <a href="${pageContext.request.contextPath}/sale-conditions" class="btn btn-secondary me-md-2">
                                <i class="fas fa-times me-2"></i>Hủy
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save me-2"></i>Lưu điều kiện
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function updateAmountLabel() {
            const typeSelect = document.getElementById('type');
            const amountLabel = document.getElementById('amountLabel');
            const amountUnit = document.getElementById('amountUnit');
            const amountHelp = document.getElementById('amountHelp');
            const amountInput = document.getElementById('amount');

            if (typeSelect.value === 'percentage') {
                amountLabel.textContent = 'Phần trăm giảm';
                amountUnit.textContent = '%';
                amountHelp.textContent = 'Nhập phần trăm giảm giá (1-100)';
                amountInput.max = '100';
            } else if (typeSelect.value === 'fixed') {
                amountLabel.textContent = 'Số tiền giảm';
                amountUnit.textContent = '₫';
                amountHelp.textContent = 'Nhập số tiền giảm giá cố định';
                amountInput.removeAttribute('max');
            } else {
                amountLabel.textContent = 'Giá trị';
                amountUnit.textContent = '-';
                amountHelp.textContent = 'Nhập giá trị giảm giá';
                amountInput.removeAttribute('max');
            }
        }

        // Set default effective date to current time
        document.addEventListener('DOMContentLoaded', function() {
            const effectiveDateInput = document.getElementById('effectiveDate');
            if (!effectiveDateInput.value) {
                const now = new Date();
                now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
                effectiveDateInput.value = now.toISOString().slice(0, 16);
            }
            
            // Update amount label based on current selection
            updateAmountLabel();
        });

        // Form validation
        document.getElementById('saleConditionForm').addEventListener('submit', function(e) {
            const effectiveDate = new Date(document.getElementById('effectiveDate').value);
            const expiredDateInput = document.getElementById('expiredDate');
            
            if (expiredDateInput.value) {
                const expiredDate = new Date(expiredDateInput.value);
                if (expiredDate <= effectiveDate) {
                    e.preventDefault();
                    alert('Ngày hết hạn phải sau ngày bắt đầu hiệu lực!');
                    return false;
                }
            }

            const typeSelect = document.getElementById('type');
            const amountInput = document.getElementById('amount');
            
            if (typeSelect.value === 'percentage' && parseFloat(amountInput.value) > 100) {
                e.preventDefault();
                alert('Phần trăm giảm giá không thể vượt quá 100%!');
                return false;
            }
        });

        // Auto dismiss alerts
        setTimeout(function() {
            var alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                var bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 5000);
    </script>
</body>
</html>
