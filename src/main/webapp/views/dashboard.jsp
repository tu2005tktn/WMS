<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - WMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="dashboard-container">
    <!-- Navigation Bar -->
    <nav class="navbar">
        <h1>Warehouse Management System</h1>
        <div class="user-info">
            <span>Xin chào, <strong>${user.staffName}</strong></span>
            <span>|</span>
            <span>${user.email}</span>
            <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Đăng xuất</a>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="dashboard-content">
        <!-- Welcome Card -->
        <div class="welcome-card">
            <h2>Chào mừng bạn đến với hệ thống WMS!</h2>
            <p>
                Xin chào <strong>${user.staffName}</strong>, 
                bạn đã đăng nhập thành công vào hệ thống Warehouse Management System.
            </p>
            <p>
                <strong>Thông tin tài khoản:</strong><br>
                • Tên đăng nhập: ${user.username}<br>
                • Email: ${user.email}<br>
                • Địa chỉ: ${not empty user.address ? user.address : 'Chưa cập nhật'}<br>
                • Trạng thái: <span style="color: green; font-weight: bold;">${user.status}</span><br>
                • Ngày tạo: ${user.createdDate}
            </p>
        </div>        <div style="margin-top: 30px;">
            <a href="${pageContext.request.contextPath}/providers" class="btn btn-outline-primary">Quản lý nhà cung cấp</a>
        </div>

        <!-- Stats Grid -->
        <div class="stats-grid">
            <div class="stat-card">
                <h3>Sản phẩm</h3>
                <div class="number">0</div>
                <div class="label">Tổng số sản phẩm</div>
            </div>
            
            <div class="stat-card">
                <h3>Kho hàng</h3>
                <div class="number">0</div>
                <div class="label">Số kho đang quản lý</div>
            </div>
            
            <div class="stat-card">
                <h3>Đơn hàng</h3>
                <div class="number">0</div>
                <div class="label">Đơn hàng hôm nay</div>
            </div>
              <div class="stat-card">
                <h3>Nhà cung cấp</h3>
                <div class="number">0</div>
                <div class="label">Nhà cung cấp</div>
            </div>
            
            <div class="stat-card">
                <h3>Khách hàng</h3>
                <div class="number">0</div>
                <div class="label">Khách hàng</div>
            </div>
        </div><!-- Quick Actions -->
        <div class="welcome-card" style="margin-top: 30px;">
            <h2>Các chức năng chính</h2>
            <p>Dưới đây là các chức năng chính của hệ thống mà bạn có thể sử dụng:</p>            <!-- Quick Action Buttons -->            <div style="margin: 20px 0; display: flex; flex-wrap: wrap; gap: 10px;">
                <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Quản lý sản phẩm</a>
                <a href="${pageContext.request.contextPath}/warehouses" class="btn btn-primary">Quản lý kho hàng</a>
                <a href="${pageContext.request.contextPath}/providers" class="btn btn-primary">Quản lý nhà cung cấp</a>
                <a href="${pageContext.request.contextPath}/customers" class="btn btn-primary">Quản lý khách hàng</a>
                <a href="${pageContext.request.contextPath}/purchase-orders" class="btn btn-primary">Quản lý đơn mua hàng</a>
                <a href="${pageContext.request.contextPath}/sale-conditions" class="btn btn-primary">Quản lý điều kiện bán hàng</a>
                <a href="${pageContext.request.contextPath}/users" class="btn btn-primary">Quản lý người dùng</a>
                <a href="${pageContext.request.contextPath}/roles" class="btn btn-primary">Quản lý vai trò</a>
            </div>            <ul style="margin-top: 15px; margin-left: 20px; line-height: 1.8;">                <li><strong>Quản lý sản phẩm:</strong> Thêm, sửa, xóa thông tin sản phẩm</li>
                <li><strong>Quản lý kho hàng:</strong> Quản lý thông tin các kho, địa điểm lưu trữ</li>
                <li><strong>Quản lý tồn kho:</strong> Theo dõi tồn kho, nhập xuất hàng</li>
                <li><strong>Quản lý đơn hàng:</strong> Xử lý đơn đặt hàng và giao hàng</li>
                <li><strong>Quản lý đơn mua hàng:</strong> Tạo và theo dõi đơn hàng từ nhà cung cấp</li>
                <li><strong>Quản lý nhà cung cấp:</strong> Thông tin nhà cung cấp và hợp đồng</li>
                <li><strong>Quản lý khách hàng:</strong> Thông tin khách hàng và liên lạc</li>
                <li><strong>Quản lý điều kiện bán hàng:</strong> Tạo và quản lý các điều kiện khuyến mãi, giảm giá</li>
                <li><strong>Báo cáo:</strong> Thống kê và báo cáo hoạt động kinh doanh</li>
            </ul>
        </div>
    </div>

    <script>
        // Tự động cập nhật thời gian
        function updateTime() {
            const now = new Date();
            const timeString = now.toLocaleString('vi-VN');
            // Có thể thêm hiển thị thời gian real-time nếu cần
        }
        
        // Cập nhật mỗi giây
        setInterval(updateTime, 1000);
        
        // Confirm logout
        document.querySelector('.logout-btn').addEventListener('click', function(e) {
            if (!confirm('Bạn có chắc muốn đăng xuất?')) {
                e.preventDefault();
            }
        });
        
        // Hiển thị thông báo chào mừng (có thể tùy chỉnh)
        console.log('Chào mừng ${user.staffName} đến với WMS!');
    </script>
</body>
</html>
