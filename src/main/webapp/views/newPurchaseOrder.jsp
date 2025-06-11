<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo đơn mua hàng mới - WMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body { background: #f8f9fa; }
        .navbar { box-shadow: 0 2px 8px rgba(0,0,0,0.03); }
        .modal-content { border-radius: 10px; }
        .product-item {
            border: 1px solid #dee2e6;
            border-radius: 0.375rem;
            padding: 1rem;
            margin-bottom: 1rem;
            background: white;
        }
        .product-item.selected {
            border-color: #0d6efd;
            background-color: #f8f9ff;
        }
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
                            <i class="fas fa-plus-circle text-primary"></i> Tạo đơn mua hàng mới
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
                            <input type="hidden" name="action" value="create">

                            <!-- Basic Information -->
                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <label for="providerId" class="form-label">Nhà cung cấp <span class="text-danger">*</span></label>
                                    <select class="form-select" id="providerId" name="providerId" required onchange="loadProviderProducts()">
                                        <option value="">Chọn nhà cung cấp...</option>
                                        <c:forEach var="provider" items="${providers}">
                                            <option value="${provider.providerId}" 
                                                    ${providerId == provider.providerId ? 'selected' : ''}>
                                                ${provider.providerName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label for="deliveryDate" class="form-label">Ngày giao hàng dự kiến</label>
                                    <input type="datetime-local" class="form-control" id="deliveryDate" 
                                           name="deliveryDate" value="${deliveryDate}">
                                </div>
                            </div>

                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <label for="status" class="form-label">Trạng thái</label>
                                    <select class="form-select" id="status" name="status">
                                        <option value="PENDING" ${status == 'PENDING' ? 'selected' : ''}>Chờ duyệt</option>
                                        <option value="APPROVED" ${status == 'APPROVED' ? 'selected' : ''}>Đã duyệt</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label for="notes" class="form-label">Ghi chú</label>
                                    <textarea class="form-control" id="notes" name="notes" rows="2">${notes}</textarea>
                                </div>
                            </div>

                            <!-- Product Selection -->
                            <div class="mb-4">
                                <h5>Chọn sản phẩm</h5>
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <button type="button" class="btn btn-outline-primary" onclick="showProductModal()">
                                            <i class="fas fa-plus"></i> Thêm sản phẩm
                                        </button>
                                    </div>
                                </div>

                                <!-- Selected Products -->
                                <div id="selectedProducts">
                                    <div class="alert alert-info">
                                        <i class="fas fa-info-circle"></i> Chưa có sản phẩm nào được chọn. Vui lòng chọn nhà cung cấp và thêm sản phẩm.
                                    </div>
                                </div>
                            </div>

                            <!-- Form Actions -->
                            <div class="row">
                                <div class="col-12">
                                    <a href="${pageContext.request.contextPath}/purchase-orders" class="btn btn-secondary">
                                        <i class="fas fa-arrow-left"></i> Quay lại
                                    </a>
                                    <button type="submit" class="btn btn-primary ms-2">
                                        <i class="fas fa-save"></i> Tạo đơn mua hàng
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Product Selection Modal -->
    <div class="modal fade" id="productModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Chọn sản phẩm</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div id="productList">
                        <div class="text-center text-muted">
                            Vui lòng chọn nhà cung cấp để xem danh sách sản phẩm
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                    <button type="button" class="btn btn-primary" onclick="addSelectedProducts()">Thêm sản phẩm đã chọn</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        let selectedProductsData = [];
        let availableProducts = [];

        function loadProviderProducts() {
            const providerId = document.getElementById('providerId').value;
            if (!providerId) {
                document.getElementById('selectedProducts').innerHTML = 
                    '<div class="alert alert-info"><i class="fas fa-info-circle"></i> Chưa có sản phẩm nào được chọn. Vui lòng chọn nhà cung cấp và thêm sản phẩm.</div>';
                return;
            }

            // Load products for the selected provider via API
            fetch('${pageContext.request.contextPath}/api/provider-products?action=by-provider&providerId=' + providerId)
                .then(response => response.json())
                .then(data => {
                    availableProducts = data.data || [];
                    updateProductList();
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        }

        function updateProductList() {
            const productListDiv = document.getElementById('productList');
            
            if (availableProducts.length === 0) {
                productListDiv.innerHTML = '<div class="text-center text-muted">Nhà cung cấp này chưa có sản phẩm nào</div>';
                return;
            }

            let html = '';
            availableProducts.forEach(product => {
                html += `
                    <div class="product-item" onclick="toggleProduct(${product.productId})">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="product_${product.productId}" 
                                   onchange="toggleProduct(${product.productId})">
                            <label class="form-check-label" for="product_${product.productId}">
                                <strong>${product.productName}</strong> (${product.productCode})
                            </label>
                        </div>                        <div class="mt-2">
                            <small class="text-muted">Danh mục: ${product.productCategory || 'N/A'}</small><br>
                            <small class="text-muted">Giá bán: ${product.estimatedPrice ? formatCurrency(product.estimatedPrice) : 'Chưa có'}</small><br>
                            <small class="text-muted">Thời gian giao hàng: ${product.deliveryDuration ? product.deliveryDuration + ' ngày' : 'Chưa xác định'}</small>
                        </div>
                    </div>
                `;
            });
            
            productListDiv.innerHTML = html;
        }

        function showProductModal() {
            const providerId = document.getElementById('providerId').value;
            if (!providerId) {
                alert('Vui lòng chọn nhà cung cấp trước');
                return;
            }
            
            const modal = new bootstrap.Modal(document.getElementById('productModal'));
            modal.show();
        }

        function toggleProduct(productId) {
            const checkbox = document.getElementById('product_' + productId);
            const productDiv = checkbox.closest('.product-item');
            
            if (checkbox.checked) {
                productDiv.classList.add('selected');
            } else {
                productDiv.classList.remove('selected');
            }
        }        function addSelectedProducts() {
            const checkboxes = document.querySelectorAll('#productList input[type="checkbox"]:checked');
            
            checkboxes.forEach(checkbox => {
                const productId = parseInt(checkbox.id.split('_')[1]);
                const product = availableProducts.find(p => p.productId === productId);
                
                if (product && !selectedProductsData.find(p => p.productId === productId)) {
                    selectedProductsData.push({
                        productId: product.productId,
                        productName: product.productName,
                        productCode: product.productCode,
                        estimatedPrice: product.estimatedPrice || 0,
                        quantity: 1,
                        price: product.estimatedPrice || 0
                    });
                }
            });
            
            updateSelectedProductsDisplay();
            
            // Close modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('productModal'));
            modal.hide();
        }

        function updateSelectedProductsDisplay() {
            const container = document.getElementById('selectedProducts');
            
            if (selectedProductsData.length === 0) {
                container.innerHTML = '<div class="alert alert-info"><i class="fas fa-info-circle"></i> Chưa có sản phẩm nào được chọn.</div>';
                return;
            }

            let html = '<div class="table-responsive"><table class="table table-bordered"><thead><tr><th>Sản phẩm</th><th>Số lượng</th><th>Giá</th><th>Thành tiền</th><th>Thao tác</th></tr></thead><tbody>';
            
            selectedProductsData.forEach((product, index) => {
                const lineTotal = product.quantity * product.price;
                html += `
                    <tr>
                        <td>
                            <strong>${product.productName}</strong><br>
                            <small class="text-muted">${product.productCode}</small>
                            <input type="hidden" name="productId" value="${product.productId}">
                        </td>
                        <td>
                            <input type="number" class="form-control" name="quantity" min="1" 
                                   value="${product.quantity}" onchange="updateProductQuantity(${index}, this.value)">
                        </td>                        <td>
                            <input type="number" class="form-control" name="price" min="0" step="0.01"
                                   value="${product.price}" onchange="updateProductPrice(${index}, this.value)">
                        </td>
                        <td><strong>${formatCurrency(lineTotal)}</strong></td>
                        <td>
                            <button type="button" class="btn btn-outline-danger btn-sm" 
                                    onclick="removeProduct(${index})">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    </tr>
                `;
            });
            
            html += '</tbody></table></div>';
            container.innerHTML = html;
        }

        function updateProductQuantity(index, quantity) {
            selectedProductsData[index].quantity = parseInt(quantity) || 1;
            updateSelectedProductsDisplay();
        }

        function updateProductPrice(index, price) {
            selectedProductsData[index].price = parseFloat(price) || 0;
            updateSelectedProductsDisplay();
        }

        function removeProduct(index) {
            selectedProductsData.splice(index, 1);
            updateSelectedProductsDisplay();
        }

        function formatCurrency(amount) {
            return new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND'
            }).format(amount);
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
