# Chức năng quản lý Provider-Product

Đã tạo thành công hệ thống quản lý mối quan hệ giữa nhà cung cấp và sản phẩm để phục vụ việc tạo đơn hàng.

## Các thành phần đã tạo:

### 1. Model
- **ProviderProduct.java**: Model đại diện cho mối quan hệ giữa provider và product
  - Chứa thông tin về thời gian giao hàng, giá dự kiến, chính sách
  - Có các trường để hiển thị tên provider và product

### 2. DAO Layer
- **ProviderProductDAO.java**: Xử lý tất cả các thao tác database
  - `getAllProviderProducts()`: Lấy tất cả mối quan hệ
  - `getProviderProductsByProviderId()`: Lấy sản phẩm theo nhà cung cấp
  - `getProviderProductsByProductId()`: Lấy nhà cung cấp theo sản phẩm
  - `createProviderProduct()`: Tạo mối quan hệ mới
  - `updateProviderProduct()`: Cập nhật mối quan hệ
  - `deleteProviderProduct()`: Xóa mối quan hệ
  - `getProviderProductsForPurchaseOrder()`: Lấy dữ liệu cho tạo đơn hàng
  - Các phương thức hỗ trợ khác

### 3. Controller Layer
- **ManageProviderProductsServlet.java**: Servlet chính quản lý provider-product
  - `/provider-products`: Hiển thị danh sách
  - `/provider-products?action=new`: Form thêm mới
  - `/provider-products?action=edit`: Form chỉnh sửa
  - `/provider-products?action=delete`: Xóa mối quan hệ
  - `/provider-products?action=search`: Tìm kiếm
  - `/provider-products?action=by-provider`: Lọc theo provider
  - `/provider-products?action=by-product`: Lọc theo product

- **ProviderProductApiServlet.java**: API endpoint để lấy dữ liệu JSON
  - `/api/provider-products`: API trả về JSON
  - Hỗ trợ các action: list, by-provider, by-product, for-purchase

- **PurchaseOrderServlet.java**: Servlet cho tạo đơn hàng
  - `/purchase-orders`: Trang tạo đơn hàng

### 4. View Layer
- **providerProducts.jsp**: Trang chính quản lý danh sách mối quan hệ
  - Hiển thị bảng với thông tin đầy đủ
  - Chức năng tìm kiếm, lọc, thống kê
  - Giao diện đẹp với Bootstrap và Font Awesome

- **newProviderProduct.jsp**: Form thêm mối quan hệ mới
  - Dropdown chọn provider và product
  - Nhập thông tin thời gian giao hàng, giá, chính sách
  - Validation client-side và server-side

- **editProviderProduct.jsp**: Form chỉnh sửa mối quan hệ
  - Không cho phép thay đổi provider/product
  - Chỉ cho phép cập nhật thông tin bổ sung

- **createPurchaseOrder.jsp**: Trang demo tạo đơn hàng
  - Giao diện chọn provider
  - Hiển thị sản phẩm có sẵn từ provider đã chọn
  - Tính toán tổng đơn hàng
  - Sử dụng JavaScript và API để load data động

### 5. Cập nhật các trang hiện có
- **providers.jsp**: Thêm liên kết đến quản lý provider-product
- **products.jsp**: Thêm nút xem nhà cung cấp cho từng sản phẩm
- **ManageProvidersServlet.java**: Thêm thống kê về provider-product

## Các tính năng chính:

### 1. Quản lý mối quan hệ Provider-Product
- ✅ Tạo, sửa, xóa mối quan hệ
- ✅ Thiết lập thời gian giao hàng (ngày)
- ✅ Thiết lập giá dự kiến
- ✅ Ghi chú chính sách và điều khoản
- ✅ Tìm kiếm theo tên provider, tên sản phẩm, mã sản phẩm
- ✅ Lọc theo provider hoặc product

### 2. Hỗ trợ tạo đơn hàng
- ✅ API lấy dữ liệu provider-product
- ✅ Trang demo tạo đơn hàng với giao diện thân thiện
- ✅ Chọn provider và xem sản phẩm có sẵn
- ✅ Tính toán tổng đơn hàng
- ✅ Chỉ hiển thị provider-product có đầy đủ thông tin

### 3. Thống kê và báo cáo
- ✅ Thống kê tổng số mối quan hệ
- ✅ Số lượng mối quan hệ có đầy đủ thông tin
- ✅ Hiển thị thống kê trên trang providers

### 4. Giao diện người dùng
- ✅ Thiết kế responsive với Bootstrap 5
- ✅ Icon Font Awesome
- ✅ Hiệu ứng hover và transition
- ✅ Màu sắc nhất quán với hệ thống
- ✅ Thông báo lỗi và thành công

## Cách sử dụng:

### 1. Quản lý mối quan hệ Provider-Product
1. Truy cập `/provider-products` để xem danh sách
2. Click "Thêm mới" để tạo mối quan hệ mới
3. Chọn provider và product từ dropdown
4. Nhập thông tin thời gian giao hàng, giá dự kiến, chính sách
5. Sử dụng chức năng tìm kiếm hoặc lọc theo nhu cầu

### 2. Tạo đơn hàng
1. Từ trang providers, click "Tạo đơn hàng"
2. Hoặc truy cập trực tiếp `/purchase-orders`
3. Chọn nhà cung cấp từ danh sách
4. Chọn sản phẩm và nhập số lượng
5. Xem tóm tắt đơn hàng và xác nhận

### 3. Tích hợp với hệ thống hiện có
- Từ trang Products: Click icon truck để xem nhà cung cấp của sản phẩm
- Từ trang Providers: Click "Quản lý mối quan hệ sản phẩm"

## Database Schema được sử dụng:

```sql
CREATE TABLE dbo.Provider_Product (
    ProviderID        INT            NOT NULL,
    ProductID         INT            NOT NULL,
    DeliveryDuration  INT            NULL,
    EstimatedPrice    DECIMAL(18,2)  NULL,
    Policies          NVARCHAR(MAX)  NULL,
    CONSTRAINT PK_Provider_Product PRIMARY KEY (ProviderID, ProductID),
    CONSTRAINT FK_PP_Provider FOREIGN KEY (ProviderID)
        REFERENCES dbo.Provider_Master(ProviderID),
    CONSTRAINT FK_PP_Product  FOREIGN KEY (ProductID)
        REFERENCES dbo.Product_Master(ProductID)
);
```

Hệ thống này đã sẵn sàng để sử dụng và có thể mở rộng thêm các tính năng như:
- Tạo đơn hàng thực tế với lưu database
- Quản lý trạng thái đơn hàng
- Lịch sử giao dịch với nhà cung cấp
- Báo cáo hiệu suất nhà cung cấp
