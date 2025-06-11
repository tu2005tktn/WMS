<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý đơn mua hàng - WMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body { background: #f8f9fa; }
        .navbar { box-shadow: 0 2px 8px rgba(0,0,0,0.03); }
        .table th, .table td { vertical-align: middle; }
        .btn { min-width: 80px; }
        .modal-content { border-radius: 10px; }
        .status-badge {
            padding: 0.25rem 0.5rem;
            border-radius: 0.375rem;
            font-size: 0.75rem;
            font-weight: 600;
        }
        .status-pending { background-color: #fff3cd; color: #856404; }
        .status-approved { background-color: #d1ecf1; color: #0c5460; }
        .status-ordered { background-color: #cce5ff; color: #0066cc; }
        .status-received { background-color: #d4edda; color: #155724; }
        .status-cancelled { background-color: #f8d7da; color: #721c24; }
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

    <div class="container-fluid py-3">
        <div class="row">
            <div class="col-12">
                <h2 class="mb-4 text-center">Quản lý đơn mua hàng</h2>

                <!-- Success/Error Messages -->
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle"></i> ${successMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-circle"></i> ${errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <!-- Action Bar -->
                <div class="row mb-4">
                    <div class="col-md-6">
                        <a href="${pageContext.request.contextPath}/purchase-orders?action=new" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Tạo đơn mua hàng mới
                        </a>
                    </div>
                    <div class="col-md-6">
                        <form method="get" action="${pageContext.request.contextPath}/purchase-orders" class="d-flex">
                            <select name="status" class="form-select me-2" onchange="this.form.submit()">
                                <option value="">Tất cả trạng thái</option>
                                <option value="PENDING" ${statusFilter == 'PENDING' ? 'selected' : ''}>Chờ duyệt</option>
                                <option value="APPROVED" ${statusFilter == 'APPROVED' ? 'selected' : ''}>Đã duyệt</option>
                                <option value="ORDERED" ${statusFilter == 'ORDERED' ? 'selected' : ''}>Đã đặt hàng</option>
                                <option value="RECEIVED" ${statusFilter == 'RECEIVED' ? 'selected' : ''}>Đã nhận hàng</option>
                                <option value="CANCELLED" ${statusFilter == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
                            </select>
                            <input type="text" name="search" class="form-control me-2" placeholder="Tìm kiếm..." 
                                   value="${searchTerm}">
                            <button type="submit" class="btn btn-outline-secondary">
                                <i class="fas fa-search"></i>
                            </button>
                        </form>
                    </div>
                </div>

                <!-- Statistics -->
                <div class="row mb-4">
                    <div class="col-md-3">
                        <div class="card bg-primary text-white">
                            <div class="card-body">
                                <div class="d-flex justify-content-between">
                                    <div>
                                        <h4>${totalCount}</h4>
                                        <p class="mb-0">Tổng đơn hàng</p>
                                    </div>
                                    <div class="align-self-center">
                                        <i class="fas fa-shopping-cart fa-2x"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Purchase Orders Table -->
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Danh sách đơn mua hàng</h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${empty purchaseOrders}">
                                <div class="text-center py-5">
                                    <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                    <h5 class="text-muted">Không có đơn mua hàng nào</h5>
                                    <p class="text-muted">Hãy tạo đơn mua hàng đầu tiên của bạn.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-striped table-hover">
                                        <thead class="table-dark">
                                            <tr>
                                                <th>ID</th>
                                                <th>Nhà cung cấp</th>
                                                <th>Ngày tạo</th>
                                                <th>Ngày giao hàng</th>
                                                <th>Trạng thái</th>
                                                <th>Tổng tiền</th>
                                                <th>Tổng SL</th>
                                                <th>Người tạo</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="po" items="${purchaseOrders}">
                                                <tr>
                                                    <td><strong>#${po.poId}</strong></td>
                                                    <td>
                                                        <div class="fw-bold">${po.providerName}</div>
                                                        <small class="text-muted">${po.providerEmail}</small>
                                                    </td>                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty po.createdDate}">
                                                                ${po.createdDate.toString().replace('T', ' ').substring(0, 16)}
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-muted">Chưa xác định</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty po.deliveryDate}">
                                                                ${po.deliveryDate.toString().replace('T', ' ').substring(0, 16)}
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-muted">Chưa xác định</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${po.status == 'PENDING'}">
                                                                <span class="status-badge status-pending">Chờ duyệt</span>
                                                            </c:when>
                                                            <c:when test="${po.status == 'APPROVED'}">
                                                                <span class="status-badge status-approved">Đã duyệt</span>
                                                            </c:when>
                                                            <c:when test="${po.status == 'ORDERED'}">
                                                                <span class="status-badge status-ordered">Đã đặt hàng</span>
                                                            </c:when>
                                                            <c:when test="${po.status == 'RECEIVED'}">
                                                                <span class="status-badge status-received">Đã nhận hàng</span>
                                                            </c:when>
                                                            <c:when test="${po.status == 'CANCELLED'}">
                                                                <span class="status-badge status-cancelled">Đã hủy</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="status-badge status-pending">${po.status}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <strong>
                                                            <fmt:formatNumber value="${po.totalAmount}" type="currency" 
                                                                            currencySymbol="₫" groupingUsed="true"/>
                                                        </strong>
                                                    </td>
                                                    <td><strong>${po.totalQuantity}</strong></td>
                                                    <td>${po.createdBy}</td>
                                                    <td>
                                                        <div class="btn-group" role="group">
                                                            <a href="${pageContext.request.contextPath}/purchase-orders?action=view&id=${po.poId}" 
                                                               class="btn btn-outline-info btn-sm" title="Xem chi tiết">
                                                                <i class="fas fa-eye"></i>
                                                            </a>
                                                            <a href="${pageContext.request.contextPath}/purchase-orders?action=edit&id=${po.poId}" 
                                                               class="btn btn-outline-warning btn-sm" title="Sửa">
                                                                <i class="fas fa-edit"></i>
                                                            </a>
                                                            <button type="button" class="btn btn-outline-danger btn-sm" 
                                                                    title="Xóa" onclick="confirmDelete(${po.poId})">
                                                                <i class="fas fa-trash"></i>
                                                            </button>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Xác nhận xóa</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    Bạn có chắc chắn muốn xóa đơn mua hàng này? Hành động này không thể hoàn tác.
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <a id="deleteLink" href="#" class="btn btn-danger">Xóa</a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function confirmDelete(poId) {
            const deleteLink = document.getElementById('deleteLink');
            deleteLink.href = '${pageContext.request.contextPath}/purchase-orders?action=delete&id=' + poId;
            const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
            modal.show();
        }

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
