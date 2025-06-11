<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý khách hàng - WMS</title>
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
            <span class="me-3">Xin chào, <strong>${sessionScope.user.staffName}</strong></span>
            <span class="me-3"><i class="fas fa-envelope"></i> ${sessionScope.user.email}</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-secondary btn-sm">Đăng xuất</a>
        </div>
    </div>
</nav>
<div class="container py-3">
    <h2 class="mb-4 text-center">Quản lý khách hàng</h2>
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="alert alert-success text-center">${sessionScope.successMessage}</div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="alert alert-danger text-center">${sessionScope.errorMessage}</div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <div class="mb-3 d-flex flex-column flex-md-row justify-content-between align-items-stretch gap-2">
        <a href="${pageContext.request.contextPath}/customers?action=new" class="btn btn-primary">
            <i class="fas fa-plus"></i> Thêm khách hàng mới
        </a>
        <form method="get" action="${pageContext.request.contextPath}/customers" class="d-flex gap-2">
            <input type="hidden" name="action" value="search">
            <input type="text" class="form-control" name="search" placeholder="Tìm kiếm..." value="${searchTerm}">
            <button class="btn btn-outline-secondary" type="submit"><i class="fas fa-search"></i></button>
        </form>
    </div>
    <c:choose>
        <c:when test="${not empty customers}">
            <div class="table-responsive">
                <table class="table table-bordered table-hover align-middle bg-white">
                    <thead class="table-light">
                        <tr>
                            <th>#</th>
                            <th>Tên khách hàng</th>
                            <th>Email</th>
                            <th>Địa chỉ</th>
                            <th class="text-center">Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="customer" items="${customers}">
                            <tr>
                                <td>${customer.customerId}</td>
                                <td>${customer.customerName}</td>
                                <td>${customer.email}</td>
                                <td>${customer.address}</td>
                                <td class="text-center">
                                    <a href="${pageContext.request.contextPath}/customers?action=edit&id=${customer.customerId}" class="btn btn-warning btn-sm me-1"><i class="fas fa-edit"></i> Sửa</a>
                                    <button type="button" class="btn btn-danger btn-sm" onclick="deleteCustomer(${customer.customerId}, '${customer.customerName}')"><i class="fas fa-trash"></i> Xóa</button>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info text-center">Không tìm thấy khách hàng nào.</div>
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
                <p>Bạn có chắc chắn muốn xóa khách hàng <strong id="customerNameToDelete"></strong>?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <form action="${pageContext.request.contextPath}/customers" method="post" class="d-inline" id="deleteForm">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="customerId" id="customerIdToDelete">
                    <button type="submit" class="btn btn-danger">
                        <i class="fas fa-trash"></i> Xóa khách hàng
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
function deleteCustomer(customerId, customerName) {
    document.getElementById('customerIdToDelete').value = customerId;
    document.getElementById('customerNameToDelete').textContent = customerName;
    document.getElementById('deleteForm').action = '${pageContext.request.contextPath}/customers';
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
