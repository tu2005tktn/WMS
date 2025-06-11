<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý điều kiện bán hàng - WMS</title>
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
        .stats-card {
            background: white;
            border-radius: 15px;
            padding: 1.5rem;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            border-left: 4px solid #667eea;
            margin-bottom: 2rem;
        }
        .sale-conditions-table {
            background: white;
            border-radius: 15px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .status-badge {
            padding: 0.25rem 0.75rem;
            border-radius: 50px;
            font-size: 0.875rem;
            font-weight: 500;
        }
        .status-active { background-color: #d4edda; color: #155724; }
        .status-expired { background-color: #f8d7da; color: #721c24; }
        .status-future { background-color: #fff3cd; color: #856404; }
        .type-badge {
            padding: 0.25rem 0.75rem;
            border-radius: 50px;
            font-size: 0.875rem;
            font-weight: 500;
        }
        .type-percentage { background-color: #e7f3ff; color: #0066cc; }
        .type-fixed { background-color: #f0f8f0; color: #006600; }
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
                            <a class="nav-link active" href="${pageContext.request.contextPath}/sale-conditions">
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
                <h1 class="display-4 fw-bold mb-0">
                    <i class="fas fa-tags me-3"></i>Quản lý Điều kiện Bán hàng
                </h1>
                <p class="lead mb-0">Tạo và quản lý các điều kiện bán hàng, khuyến mãi</p>
            </div>
        </div>

        <!-- Stats Card -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="stats-card">
                    <div class="d-flex align-items-center">
                        <div class="flex-shrink-0">
                            <i class="fas fa-tags fa-2x text-primary"></i>
                        </div>
                        <div class="flex-grow-1 ms-3">
                            <div class="fw-bold text-primary">Tổng số điều kiện</div>
                            <div class="h4 mb-0">${totalCount}</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Success/Error Messages -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>${successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle me-2"></i>${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Search and Actions -->
        <div class="card mb-4">
            <div class="card-body">
                <div class="row align-items-center">
                    <div class="col-md-8">
                        <form method="get" action="${pageContext.request.contextPath}/sale-conditions" class="d-flex">
                            <div class="input-group">
                                <span class="input-group-text">
                                    <i class="fas fa-search"></i>
                                </span>
                                <input type="text" class="form-control" name="search" 
                                       value="${searchTerm}" placeholder="Tìm kiếm theo mã điều kiện, loại hoặc người tạo...">
                                <button class="btn btn-outline-primary" type="submit">Tìm kiếm</button>
                                <c:if test="${not empty searchTerm}">
                                    <a href="${pageContext.request.contextPath}/sale-conditions" class="btn btn-outline-secondary">
                                        <i class="fas fa-times"></i> Xóa
                                    </a>
                                </c:if>
                            </div>
                        </form>
                    </div>
                    <div class="col-md-4 text-end">
                        <a href="${pageContext.request.contextPath}/sale-conditions?action=new" class="btn btn-primary">
                            <i class="fas fa-plus me-2"></i>Thêm điều kiện mới
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Sale Conditions Table -->
        <div class="sale-conditions-table">
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th>ID</th>
                            <th>Mã điều kiện</th>
                            <th>Giá trị</th>
                            <th>Loại</th>
                            <th>Ngày hiệu lực</th>
                            <th>Ngày hết hạn</th>
                            <th>Trạng thái</th>
                            <th>Người tạo</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty saleConditions}">
                                <tr>
                                    <td colspan="9" class="text-center py-5 text-muted">
                                        <i class="fas fa-inbox fa-3x mb-3 d-block"></i>
                                        <c:choose>
                                            <c:when test="${not empty searchTerm}">
                                                Không tìm thấy điều kiện bán hàng nào với từ khóa "${searchTerm}"
                                            </c:when>
                                            <c:otherwise>
                                                Chưa có điều kiện bán hàng nào
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="condition" items="${saleConditions}">
                                    <tr>
                                        <td>${condition.saleConditionId}</td>
                                        <td>
                                            <strong>${condition.conditionCode}</strong>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${condition.type == 'percentage' || condition.type == '%'}">
                                                    <fmt:formatNumber value="${condition.amount}" pattern="#0.##"/>%
                                                </c:when>
                                                <c:otherwise>
                                                    <fmt:formatNumber value="${condition.amount}" type="currency" currencySymbol="₫" groupingUsed="true"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${condition.type == 'percentage' || condition.type == '%'}">
                                                    <span class="type-badge type-percentage">
                                                        <i class="fas fa-percentage me-1"></i>Phần trăm
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="type-badge type-fixed">
                                                        <i class="fas fa-dollar-sign me-1"></i>Cố định
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>                                        <td>
                                            ${condition.effectiveDate.toString().replace('T', ' ')}
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${condition.expiredDate != null}">
                                                    ${condition.expiredDate.toString().replace('T', ' ')}
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted">Không giới hạn</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${condition.active}">
                                                    <span class="status-badge status-active">
                                                        <i class="fas fa-check-circle me-1"></i>Đang hoạt động
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <jsp:useBean id="now" class="java.util.Date"/>
                                                    <c:choose>
                                                        <c:when test="${condition.effectiveDate > now}">
                                                            <span class="status-badge status-future">
                                                                <i class="fas fa-clock me-1"></i>Chưa hiệu lực
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="status-badge status-expired">
                                                                <i class="fas fa-times-circle me-1"></i>Hết hạn
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${condition.createdBy}</td>
                                        <td>
                                            <div class="btn-group" role="group">
                                                <a href="${pageContext.request.contextPath}/sale-conditions?action=edit&id=${condition.saleConditionId}" 
                                                   class="btn btn-sm btn-outline-primary" title="Chỉnh sửa">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <button type="button" class="btn btn-sm btn-outline-danger" 
                                                        onclick="confirmDelete(${condition.saleConditionId}, '${condition.conditionCode}')" 
                                                        title="Xóa">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
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
                    <p>Bạn có chắc chắn muốn xóa điều kiện bán hàng <strong id="deleteConditionCode"></strong> không?</p>
                    <p class="text-danger"><small>Hành động này không thể hoàn tác!</small></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <a href="#" id="confirmDeleteBtn" class="btn btn-danger">Xóa</a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function confirmDelete(conditionId, conditionCode) {
            document.getElementById('deleteConditionCode').textContent = conditionCode;
            document.getElementById('confirmDeleteBtn').href = 
                '${pageContext.request.contextPath}/sale-conditions?action=delete&id=' + conditionId;
            
            var deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
            deleteModal.show();
        }

        // Auto dismiss alerts after 5 seconds
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
