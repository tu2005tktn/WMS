# Chức năng quản lý Khách hàng (Customer)

Đã tạo thành công hệ thống quản lý khách hàng hoàn chỉnh cho WMS (Warehouse Management System).

## Các thành phần đã tạo:

### 1. Model
- **Customer.java**: Model đại diện cho khách hàng
  - Chứa thông tin cơ bản: ID, tên, email, địa chỉ
  - Có constructors và getters/setters đầy đủ

### 2. DAO Layer
- **CustomerDAO.java**: Xử lý tất cả các thao tác database
  - `getAllCustomers()`: Lấy tất cả khách hàng
  - `getCustomerById()`: Lấy khách hàng theo ID
  - `createCustomer()`: Tạo khách hàng mới
  - `updateCustomer()`: Cập nhật thông tin khách hàng
  - `deleteCustomer()`: Xóa khách hàng
  - `searchCustomers()`: Tìm kiếm khách hàng
  - `isCustomerNameExists()`: Kiểm tra tên khách hàng tồn tại
  - `isCustomerEmailExists()`: Kiểm tra email tồn tại
  - `isCustomerEmailExistsExcludeId()`: Kiểm tra email trùng khi update

### 3. Controller Layer
- **ManageCustomersServlet.java**: Servlet chính quản lý khách hàng
  - `/customers`: Hiển thị danh sách khách hàng
  - `/customers?action=new`: Form thêm mới
  - `/customers?action=edit&id=X`: Form chỉnh sửa
  - `/customers?action=search`: Tìm kiếm khách hàng
  - POST actions: create, update, delete

- **CustomerApiServlet.java**: API endpoint để lấy dữ liệu JSON
  - `/api/customers`: API trả về JSON
  - Hỗ trợ các action: list, search, get

### 4. View Layer
- **customers.jsp**: Trang chính quản lý danh sách khách hàng
  - Hiển thị bảng với thông tin đầy đủ
  - Chức năng tìm kiếm, thống kê
  - Giao diện đẹp với Bootstrap và Font Awesome
  - Modal xác nhận xóa

- **newCustomer.jsp**: Form thêm khách hàng mới
  - Form validation client-side và server-side
  - Giao diện thân thiện với hướng dẫn sử dụng
  - Character counter cho textarea

- **editCustomer.jsp**: Form chỉnh sửa khách hàng
  - Hiển thị thông tin hiện tại
  - Nút khôi phục về giá trị ban đầu
  - Validation đầy đủ

### 5. Cập nhật các trang hiện có
- **dashboard.jsp**: Thêm liên kết đến quản lý khách hàng
- Thêm card thống kê khách hàng
- Cập nhật menu và danh sách chức năng

## Các tính năng chính:

### 1. Quản lý khách hàng cơ bản
- ✅ Tạo, sửa, xóa khách hàng
- ✅ Validation dữ liệu đầu vào
- ✅ Kiểm tra email trùng lặp
- ✅ Tìm kiếm theo tên, email, địa chỉ

### 2. Giao diện người dùng
- ✅ Thiết kế responsive với Bootstrap 5
- ✅ Icon Font Awesome
- ✅ Hiệu ứng hover và transition
- ✅ Màu sắc nhất quán với hệ thống
- ✅ Thông báo lỗi và thành công

### 3. Tính năng nâng cao
- ✅ API JSON cho tích hợp
- ✅ Character counter cho form
- ✅ Modal xác nhận xóa
- ✅ Breadcrumb navigation
- ✅ Auto-hide alert messages

### 4. Validation và bảo mật
- ✅ Validation client-side bằng JavaScript
- ✅ Validation server-side bằng Java
- ✅ Kiểm tra định dạng email
- ✅ Giới hạn độ dài các trường
- ✅ Escape JSON để tránh XSS

## Cách sử dụng:

### 1. Quản lý khách hàng
1. Truy cập `/customers` để xem danh sách
2. Click "Thêm khách hàng mới" để tạo mới
3. Click biểu tượng bút chì để chỉnh sửa
4. Click biểu tượng thùng rác để xóa (có xác nhận)
5. Sử dụng ô tìm kiếm để tìm khách hàng

### 2. API sử dụng
```javascript
// Lấy tất cả khách hàng
fetch('/api/customers?action=list')

// Tìm kiếm khách hàng
fetch('/api/customers?action=search&search=keyword')

// Lấy khách hàng theo ID
fetch('/api/customers?action=get&id=1')
```

### 3. Tích hợp với hệ thống hiện có
- Từ Dashboard: Click "Quản lý khách hàng"
- API có thể được sử dụng cho các tính năng khác như tạo đơn hàng

## Database Schema được sử dụng:

```sql
CREATE TABLE dbo.Customer_Master (
    CustomerID     INT            IDENTITY(1,1) PRIMARY KEY,
    CustomerName   NVARCHAR(100)  NOT NULL,
    Email          NVARCHAR(255)  NULL,
    Address        NVARCHAR(255)  NULL
);
```

## Các tính năng có thể mở rộng:

- Thêm thông tin chi tiết khách hàng (số điện thoại, ngày sinh, etc.)
- Tích hợp với hệ thống đơn hàng
- Lịch sử giao dịch với khách hàng
- Phân loại khách hàng (VIP, thường, etc.)
- Export/Import danh sách khách hàng
- Gửi email marketing

Hệ thống quản lý khách hàng này đã sẵn sàng để sử dụng và có thể mở rộng thêm các tính năng theo nhu cầu.
