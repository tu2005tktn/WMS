<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết đơn mua hàng - WMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body { background: #f8f9fa; }
        .navbar { box-shadow: 0 2px 8px rgba(0,0,0,0.03); }
        .status-badge {
            padding: 0.5rem 1rem;
            border-radius: 0.5rem;
            font-size: 0.875rem;
            font-weight: 600;
        }
        .status-pending { background-color: #fff3cd; color: #856404; }
        .status-approved { background-color: #d1ecf1; color: #0c5460; }
        .status-ordered { background-color: #cce5ff; color: #0066cc; }
        .status-received { background-color: #d4edda; color: #155724; }
        .status-cancelled { background-color: #f8d7da; color: #721c24; }
        .info-card { background: white; border: 1px solid #dee2e6; border-radius: 0.5rem; }
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
        <div class="row">
            <div class="col-12">
                <!-- Header -->
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2>Chi tiết đơn mua hàng #${purchaseOrder.poId}</h2>
                    <div>
                        <a href="${pageContext.request.contextPath}/purchase-orders" class="btn btn-outline-secondary">
                            <i class="fas fa-arrow-left"></i> Quay lại
                        </a>
                        <a href="${pageContext.request.contextPath}/purchase-orders?action=edit&id=${purchaseOrder.poId}" 
                           class="btn btn-warning">
                            <i class="fas fa-edit"></i> Sửa
                        </a>
                    </div>
                </div>

                <!-- Purchase Order Information -->
                <div class="row mb-4">
                    <div class="col-md-8">
                        <div class="info-card p-4">
                            <h5 class="mb-3">Thông tin đơn hàng</h5>
                            <div class="row">
                                <div class="col-md-6">
                                    <table class="table table-borderless">
                                        <tr>
                                            <td><strong>Mã đơn hàng:</strong></td>
                                            <td>#${purchaseOrder.poId}</td>
                                        </tr>                                        <tr>
                                            <td><strong>Ngày tạo:</strong></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty purchaseOrder.createdDate}">
                                                        ${purchaseOrder.createdDate.toString().replace('T', ' ').substring(0, 16)}
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">Chưa xác định</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td><strong>Ngày giao hàng:</strong></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty purchaseOrder.deliveryDate}">
                                                        ${purchaseOrder.deliveryDate.toString().replace('T', ' ').substring(0, 16)}
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">Chưa xác định</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td><strong>Người tạo:</strong></td>
                                            <td>${purchaseOrder.createdBy}</td>
                                        </tr>
                                    </table>
                                </div>
                                <div class="col-md-6">
                                    <table class="table table-borderless">
                                        <tr>
                                            <td><strong>Trạng thái:</strong></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${purchaseOrder.status == 'PENDING'}">
                                                        <span class="status-badge status-pending">Chờ duyệt</span>
                                                    </c:when>
                                                    <c:when test="${purchaseOrder.status == 'APPROVED'}">
                                                        <span class="status-badge status-approved">Đã duyệt</span>
                                                    </c:when>
                                                    <c:when test="${purchaseOrder.status == 'ORDERED'}">
                                                        <span class="status-badge status-ordered">Đã đặt hàng</span>
                                                    </c:when>
                                                    <c:when test="${purchaseOrder.status == 'RECEIVED'}">
                                                        <span class="status-badge status-received">Đã nhận hàng</span>
                                                    </c:when>
                                                    <c:when test="${purchaseOrder.status == 'CANCELLED'}">
                                                        <span class="status-badge status-cancelled">Đã hủy</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-badge status-pending">${purchaseOrder.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td><strong>Tổng số lượng:</strong></td>
                                            <td><span class="badge bg-info">${purchaseOrder.totalQuantity}</span></td>
                                        </tr>
                                        <tr>
                                            <td><strong>Tổng tiền:</strong></td>
                                            <td>
                                                <strong class="text-success">
                                                    <fmt:formatNumber value="${purchaseOrder.totalAmount}" 
                                                                    type="currency" currencySymbol="₫" groupingUsed="true"/>
                                                </strong>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                            
                            <c:if test="${not empty purchaseOrder.notes}">
                                <div class="mt-3">
                                    <strong>Ghi chú:</strong>
                                    <p class="mt-1">${purchaseOrder.notes}</p>
                                </div>
                            </c:if>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="info-card p-4">
                            <h5 class="mb-3">Thông tin nhà cung cấp</h5>
                            <div class="mb-2">
                                <strong>${purchaseOrder.providerName}</strong>
                            </div>
                            <div class="mb-2">
                                <i class="fas fa-envelope text-muted"></i>
                                <span class="ms-1">${purchaseOrder.providerEmail}</span>
                            </div>
                            <div class="mb-2">
                                <i class="fas fa-map-marker-alt text-muted"></i>
                                <span class="ms-1">${purchaseOrder.providerAddress}</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Purchase Order Details -->
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Chi tiết sản phẩm</h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${empty purchaseOrder.details}">
                                <div class="text-center py-4">
                                    <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                    <h6 class="text-muted">Đơn hàng này chưa có sản phẩm nào</h6>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-striped">
                                        <thead class="table-dark">
                                            <tr>
                                                <th>STT</th>
                                                <th>Sản phẩm</th>
                                                <th>Số lượng</th>
                                                <th>Đơn giá</th>
                                                <th>Thành tiền</th>
                                                <th>Ngày giao</th>
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
                                                        <c:if test="${not empty detail.productCategory}">
                                                            <br><small class="text-muted">Danh mục: ${detail.productCategory}</small>
                                                        </c:if>
                                                    </td>
                                                    <td><strong>${detail.quantity}</strong></td>
                                                    <td>
                                                        <fmt:formatNumber value="${detail.price}" 
                                                                        type="currency" currencySymbol="₫" groupingUsed="true"/>
                                                    </td>                                                    <td>
                                                        <strong>
                                                            <fmt:formatNumber value="${detail.lineTotal}" 
                                                                            type="currency" currencySymbol="₫" groupingUsed="true"/>
                                                        </strong>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty detail.deliveryDate}">
                                                                ${detail.deliveryDate.toString().replace('T', ' ').substring(0, 16)}
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-muted">-</span>
                                                            </c:otherwise>
                                                        </c:choose>
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
                                                <th colspan="2"></th>
                                            </tr>
                                        </tfoot>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
