<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý nhà cung cấp sản phẩm - WMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body { background: #f8f9fa; }
        .navbar { box-shadow: 0 2px 8px rgba(0,0,0,0.03); }
        .table th, .table td { vertical-align: middle; }
        .btn { min-width: 80px; }
        .modal-content { border-radius: 10px; }
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
<div class="container py-3">
    <h2 class="mb-4 text-center">Quản lý nhà cung cấp sản phẩm</h2>
    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success text-center">${sessionScope.message}</div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger text-center">${sessionScope.error}</div>
        <c:remove var="error" scope="session"/>
    </c:if>
    <div class="mb-3 d-flex flex-column flex-md-row justify-content-between align-items-stretch gap-2">
        <a href="${pageContext.request.contextPath}/provider-products?action=new" class="btn btn-primary">
            <i class="fas fa-plus"></i> Thêm mối quan hệ
        </a>
        <form method="get" action="${pageContext.request.contextPath}/provider-products" class="d-flex gap-2">
            <input type="hidden" name="action" value="search">
            <input type="text" class="form-control" name="search" placeholder="Tìm kiếm..." value="${searchTerm}">
            <button class="btn btn-outline-secondary" type="submit"><i class="fas fa-search"></i></button>
        </form>
    </div>
    <c:choose>
        <c:when test="${not empty providerProducts}">
            <div class="table-responsive">
                <table class="table table-bordered table-hover align-middle bg-white">
                    <thead class="table-light">
                        <tr>
                            <th>#</th>
                            <th>Nhà cung cấp</th>
                            <th>Sản phẩm</th>
                            <th>Thời gian giao hàng (ngày)</th>
                            <th>Giá dự kiến</th>
                            <th>Chính sách</th>
                            <th class="text-center">Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="pp" items="${providerProducts}" varStatus="loop">
                            <tr>
                                <td>${loop.count}</td>
                                <td><c:out value="${pp.providerName}" /></td>
                                <td><c:out value="${pp.productName}" /></td>
                                <td><c:out value="${pp.deliveryDuration}" /></td>
                                <td>
                                    <c:if test="${not empty pp.estimatedPrice}">
                                        <fmt:formatNumber value="${pp.estimatedPrice}" type="currency" currencySymbol="₫" />
                                    </c:if>
                                </td>
                                <td><c:out value="${pp.policies}" /></td>
                                <td class="text-center">
                                    <a href="${pageContext.request.contextPath}/provider-products?action=edit&providerId=${pp.providerId}&productId=${pp.productId}" class="btn btn-sm btn-warning"><i class="fas fa-edit"></i> Sửa</a>
                                    <button type="button" onclick="confirmDelete(${pp.providerId}, ${pp.productId}, '${fn:escapeXml(pp.providerName)}', '${fn:escapeXml(pp.productName)}')" class="btn btn-sm btn-danger"><i class="fas fa-trash"></i> Xóa</button>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info text-center">Không tìm thấy mối quan hệ nào.</div>
        </c:otherwise>
    </c:choose>
</div>
<!-- Delete Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-exclamation-triangle text-warning"></i> Xác nhận xóa</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p>Bạn có chắc chắn muốn xóa mối quan hệ giữa <strong id="deleteProviderName"></strong> và <strong id="deleteProductName"></strong>?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <a id="confirmDeleteBtn" href="#" class="btn btn-danger"><i class="fas fa-trash"></i> Xóa</a>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
function confirmDelete(providerId, productId, providerName, productName) {
    document.getElementById('deleteProviderName').textContent = providerName;
    document.getElementById('deleteProductName').textContent = productName;
    document.getElementById('confirmDeleteBtn').href = 
        '${pageContext.request.contextPath}/provider-products?action=delete&providerId=' + providerId + '&productId=' + productId;
    new bootstrap.Modal(document.getElementById('deleteModal')).show();
}
setTimeout(function() {
    var alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        if (alert.classList.contains('show')) {
            bootstrap.Alert.getOrCreateInstance(alert).close();
        }
    });
}, 5000);
</script>
</body>
</html>
