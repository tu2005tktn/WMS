<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang không tìm thấy - WMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h2>Trang không tìm thấy</h2>
        <div class="alert alert-error">
            <p>Xin lỗi, trang bạn tìm kiếm không tồn tại hoặc đã bị di chuyển.</p>
        </div>
        
        <div style="text-align: center; margin-top: 30px;">
            <a href="${pageContext.request.contextPath}/" 
               class="btn" style="display: inline-block; text-decoration: none;">
                Về trang chủ
            </a>
        </div>
    </div>
</body>
</html>
