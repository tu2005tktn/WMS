package com.warehouse.wms.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.warehouse.wms.dao.ProductDAO;
import com.warehouse.wms.dao.ProviderDAO;
import com.warehouse.wms.dao.ProviderProductDAO;
import com.warehouse.wms.model.Product;
import com.warehouse.wms.model.Provider;
import com.warehouse.wms.model.ProviderProduct;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ManageProviderProductsServlet", urlPatterns = {"/provider-products"})
public class ManageProviderProductsServlet extends HttpServlet {
    private ProviderProductDAO providerProductDAO;
    private ProviderDAO providerDAO;
    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        providerProductDAO = new ProviderProductDAO();
        providerDAO = new ProviderDAO();
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        
        try {
            switch (action) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteProviderProduct(request, response);
                    break;
                case "search":
                    searchProviderProducts(request, response);
                    break;
                case "by-provider":
                    showProductsByProvider(request, response);
                    break;
                case "by-product":
                    showProvidersByProduct(request, response);
                    break;
                default:
                    listProviderProducts(request, response);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        
        try {
            switch (action) {
                case "create":
                    createProviderProduct(request, response);
                    break;
                case "update":
                    updateProviderProduct(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/provider-products");
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
    
    private void listProviderProducts(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<ProviderProduct> providerProducts = providerProductDAO.getAllProviderProducts();
        request.setAttribute("providerProducts", providerProducts);
        request.getRequestDispatcher("/views/providerProducts.jsp").forward(request, response);
    }
    
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Provider> providers = providerDAO.getAllProviders();
        List<Product> products = productDAO.getAllProducts();
        
        request.setAttribute("providers", providers);
        request.setAttribute("products", products);
        request.getRequestDispatcher("/views/newProviderProduct.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int providerId = Integer.parseInt(request.getParameter("providerId"));
        int productId = Integer.parseInt(request.getParameter("productId"));
        
        ProviderProduct providerProduct = providerProductDAO.getProviderProduct(providerId, productId);
        if (providerProduct == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Provider-Product relationship not found");
            return;
        }
        
        List<Provider> providers = providerDAO.getAllProviders();
        List<Product> products = productDAO.getAllProducts();
        
        request.setAttribute("providerProduct", providerProduct);
        request.setAttribute("providers", providers);
        request.setAttribute("products", products);
        request.getRequestDispatcher("/views/editProviderProduct.jsp").forward(request, response);
    }
    
    private void createProviderProduct(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        try {
            ProviderProduct providerProduct = buildProviderProductFromRequest(request);
            
            // Check if relationship already exists
            if (providerProductDAO.existsProviderProduct(providerProduct.getProviderId(), providerProduct.getProductId())) {
                request.setAttribute("error", "Mối quan hệ giữa nhà cung cấp và sản phẩm này đã tồn tại!");
                showNewForm(request, response);
                return;
            }
            
            boolean success = providerProductDAO.createProviderProduct(providerProduct);
            if (success) {
                request.setAttribute("message", "Thêm mối quan hệ nhà cung cấp - sản phẩm thành công!");
            } else {
                request.setAttribute("error", "Không thể thêm mối quan hệ nhà cung cấp - sản phẩm!");
            }
            
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            showNewForm(request, response);
            return;
        }
        
        response.sendRedirect(request.getContextPath() + "/provider-products");
    }
    
    private void updateProviderProduct(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        try {
            ProviderProduct providerProduct = buildProviderProductFromRequest(request);
            
            boolean success = providerProductDAO.updateProviderProduct(providerProduct);
            if (success) {
                request.setAttribute("message", "Cập nhật mối quan hệ nhà cung cấp - sản phẩm thành công!");
            } else {
                request.setAttribute("error", "Không thể cập nhật mối quan hệ nhà cung cấp - sản phẩm!");
            }
            
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            showEditForm(request, response);
            return;
        }
        
        response.sendRedirect(request.getContextPath() + "/provider-products");
    }
    
    private void deleteProviderProduct(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int providerId = Integer.parseInt(request.getParameter("providerId"));
        int productId = Integer.parseInt(request.getParameter("productId"));
        
        boolean success = providerProductDAO.deleteProviderProduct(providerId, productId);
        if (success) {
            request.getSession().setAttribute("message", "Xóa mối quan hệ nhà cung cấp - sản phẩm thành công!");
        } else {
            request.getSession().setAttribute("error", "Không thể xóa mối quan hệ nhà cung cấp - sản phẩm!");
        }
        
        response.sendRedirect(request.getContextPath() + "/provider-products");
    }
    
    private void searchProviderProducts(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String searchTerm = request.getParameter("search");
        List<ProviderProduct> providerProducts;
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            providerProducts = providerProductDAO.searchProviderProducts(searchTerm.trim());
        } else {
            providerProducts = providerProductDAO.getAllProviderProducts();
        }
        
        request.setAttribute("providerProducts", providerProducts);
        request.setAttribute("searchTerm", searchTerm);
        request.getRequestDispatcher("/views/providerProducts.jsp").forward(request, response);
    }
    
    private void showProductsByProvider(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int providerId = Integer.parseInt(request.getParameter("providerId"));
        
        List<ProviderProduct> providerProducts = providerProductDAO.getProviderProductsByProviderId(providerId);
        Provider provider = providerDAO.getProviderById(providerId);
        
        request.setAttribute("providerProducts", providerProducts);
        request.setAttribute("provider", provider);
        request.setAttribute("filterByProvider", true);
        request.getRequestDispatcher("/views/providerProducts.jsp").forward(request, response);
    }
    
    private void showProvidersByProduct(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int productId = Integer.parseInt(request.getParameter("productId"));
        
        List<ProviderProduct> providerProducts = providerProductDAO.getProviderProductsByProductId(productId);
        Product product = productDAO.getProductById(productId);
        
        request.setAttribute("providerProducts", providerProducts);
        request.setAttribute("product", product);
        request.setAttribute("filterByProduct", true);
        request.getRequestDispatcher("/views/providerProducts.jsp").forward(request, response);
    }
    
    private ProviderProduct buildProviderProductFromRequest(HttpServletRequest request) {
        ProviderProduct providerProduct = new ProviderProduct();
        
        providerProduct.setProviderId(Integer.parseInt(request.getParameter("providerId")));
        providerProduct.setProductId(Integer.parseInt(request.getParameter("productId")));
        
        // Handle delivery duration
        String deliveryDurationStr = request.getParameter("deliveryDuration");
        if (deliveryDurationStr != null && !deliveryDurationStr.trim().isEmpty()) {
            try {
                providerProduct.setDeliveryDuration(Integer.parseInt(deliveryDurationStr.trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Thời gian giao hàng phải là số nguyên");
            }
        }
        
        // Handle estimated price
        String estimatedPriceStr = request.getParameter("estimatedPrice");
        if (estimatedPriceStr != null && !estimatedPriceStr.trim().isEmpty()) {
            try {
                providerProduct.setEstimatedPrice(new BigDecimal(estimatedPriceStr.trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Giá dự kiến phải là số hợp lệ");
            }
        }
        
        providerProduct.setPolicies(request.getParameter("policies"));
        
        return providerProduct;
    }
}
