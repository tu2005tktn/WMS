<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sửa đơn mua hàng - WMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body { background: #f8f9fa; }
        .navbar { box-shadow: 0 2px 8px rgba(0,0,0,0.03); }
        .modal-content { border-radius: 10px; }
    </style>
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-light bg-light mb-4">
        <div class="container-fluid">
            <span class="navbar-brand"><i class="fas fa-shopping-cart"></i> WMS</span>
            <div class="d-flex align-items-center ms-auto">
                <span class="me-3">Xin chào, <strong>${sessionScope.user.staffName}</strong></span>
                <span class="me-3"><i class="fas fa-envelope"></i> ${sessionScope.user.email}</span>
                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-secondary btn-sm me-2">
                    <i class="fas fa-home"></i> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger btn-sm">
                    <i class="fas fa-sign-out-alt"></i> Đăng xuất
                </a>
            </div>
        </div>
    </nav>

    <div class="container py-3">
        <div class="row justify-content-center">
            <div class="col-md-10">
                <div class="card">
                    <div class="card-header">
                        <h4 class="mb-0">
                            <i class="fas fa-edit text-warning"></i> Sửa đơn mua hàng #${purchaseOrder.poId}
                        </h4>
                    </div>
                    <div class="card-body">
                        <!-- Error Messages -->
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="fas fa-exclamation-circle"></i> ${errorMessage}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <form method="post" action="${pageContext.request.contextPath}/purchase-orders">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="id" value="${purchaseOrder.poId}">

                            <!-- Basic Information -->
                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <label for="providerId" class="form-label">Nhà cung cấp <span class="text-danger">*</span></label>
                                    <select class="form-select" id="providerId" name="providerId" required>
                                        <option value="">Chọn nhà cung cấp...</option>
                                        <c:forEach var="provider" items="${providers}">
                                            <option value="${provider.providerId}" 
                                                    ${purchaseOrder.providerId == provider.providerId ? 'selected' : ''}>
                                                ${provider.providerName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-6">                                    <label for="deliveryDate" class="form-label">Ngày giao hàng dự kiến</label>
                                    <input type="datetime-local" class="form-control" id="deliveryDate" 
                                           name="deliveryDate" 
                                           value="${not empty purchaseOrder.deliveryDate ? purchaseOrder.deliveryDate.toString().substring(0, 16) : ''}">
                                </div>
                            </div>

                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <label for="status" class="form-label">Trạng thái</label>
                                    <select class="form-select" id="status" name="status">
                                        <option value="PENDING" ${purchaseOrder.status == 'PENDING' ? 'selected' : ''}>Chờ duyệt</option>
                                        <option value="APPROVED" ${purchaseOrder.status == 'APPROVED' ? 'selected' : ''}>Đã duyệt</option>
                                        <option value="ORDERED" ${purchaseOrder.status == 'ORDERED' ? 'selected' : ''}>Đã đặt hàng</option>
                                        <option value="RECEIVED" ${purchaseOrder.status == 'RECEIVED' ? 'selected' : ''}>Đã nhận hàng</option>
                                        <option value="CANCELLED" ${purchaseOrder.status == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label for="notes" class="form-label">Ghi chú</label>
                                    <textarea class="form-control" id="notes" name="notes" rows="2">${purchaseOrder.notes}</textarea>
                                </div>
                            </div>

                            <!-- Current Order Details (Read-only for basic edit) -->
                            <div class="mb-4">
                                <h5>Chi tiết đơn hàng hiện tại</h5>
                                <div class="alert alert-info">
                                    <i class="fas fa-info-circle"></i> Để thay đổi chi tiết sản phẩm, vui lòng tạo đơn hàng mới hoặc liên hệ quản trị viên.
                                </div>
                                
                                <c:if test="${not empty purchaseOrder.details}">
                                    <div class="table-responsive">
                                        <table class="table table-striped">
                                            <thead class="table-dark">
                                                <tr>
                                                    <th>STT</th>
                                                    <th>Sản phẩm</th>
                                                    <th>Số lượng</th>
                                                    <th>Đơn giá</th>
                                                    <th>Thành tiền</th>
                                                    <th>Trạng thái</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="detail" items="${purchaseOrder.details}" varStatus="status">
                                                    <tr>
                                                        <td>${status.index + 1}</td>
                                                        <td>
                                                            <div class="fw-bold">${detail.productName}</div>
                                                            <small class="text-muted">${detail.productCode}</small>
                                                        </td>
                                                        <td><strong>${detail.quantity}</strong></td>
                                                        <td>
                                                            <fmt:formatNumber value="${detail.price}" 
                                                                            type="currency" currencySymbol="₫" groupingUsed="true"/>
                                                        </td>
                                                        <td>
                                                            <strong>
                                                                <fmt:formatNumber value="${detail.price * detail.quantity}" 
                                                                                type="currency" currencySymbol="₫" groupingUsed="true"/>
                                                            </strong>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${detail.status == 'PENDING'}">
                                                                    <span class="badge bg-warning">Chờ xử lý</span>
                                                                </c:when>
                                                                <c:when test="${detail.status == 'ORDERED'}">
                                                                    <span class="badge bg-primary">Đã đặt hàng</span>
                                                                </c:when>
                                                                <c:when test="${detail.status == 'RECEIVED'}">
                                                                    <span class="badge bg-success">Đã nhận</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="badge bg-secondary">${detail.status}</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                            <tfoot>
                                                <tr class="table-info">
                                                    <th colspan="2">Tổng cộng</th>
                                                    <th>${purchaseOrder.totalQuantity}</th>
                                                    <th></th>
                                                    <th>
                                                        <fmt:formatNumber value="${purchaseOrder.totalAmount}" 
                                                                        type="currency" currencySymbol="₫" groupingUsed="true"/>
                                                    </th>
                                                    <th></th>
                                                </tr>
                                            </tfoot>
                                        </table>
                                    </div>
                                </c:if>
                            </div>

                            <!-- Form Actions -->
                            <div class="row">
                                <div class="col-12">
                                    <a href="${pageContext.request.contextPath}/purchase-orders" class="btn btn-secondary">
                                        <i class="fas fa-arrow-left"></i> Quay lại
                                    </a>
                                    <a href="${pageContext.request.contextPath}/purchase-orders?action=view&id=${purchaseOrder.poId}" 
                                       class="btn btn-info ms-2">
                                        <i class="fas fa-eye"></i> Xem chi tiết
                                    </a>
                                    <button type="submit" class="btn btn-warning ms-2">
                                        <i class="fas fa-save"></i> Cập nhật đơn hàng
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Auto-hide alerts after 5 seconds
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 5000);
    </script>
</body>
</html>
