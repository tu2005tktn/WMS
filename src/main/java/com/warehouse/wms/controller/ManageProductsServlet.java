package com.warehouse.wms.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.warehouse.wms.dao.ProductDAO;
import com.warehouse.wms.model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@MultipartConfig
@WebServlet(name = "ManageProductsServlet", urlPatterns = {"/products"})
public class ManageProductsServlet extends HttpServlet {
    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
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
                    request.getRequestDispatcher("/views/newProduct.jsp").forward(request, response);
                    break;
                case "edit": {
                    int id = Integer.parseInt(request.getParameter("productId"));
                    Product product = productDAO.getProductById(id);
                    request.setAttribute("product", product);
                    request.getRequestDispatcher("/views/editProduct.jsp").forward(request, response);
                    break;
                }
                case "detail": {
                    int id = Integer.parseInt(request.getParameter("productId"));
                    Product product = productDAO.getProductById(id);
                    request.setAttribute("product", product);
                    request.getRequestDispatcher("/views/productDetail.jsp").forward(request, response);
                    break;
                }
                default: {
                    // Default: list products
                    List<Product> products = productDAO.getAllProducts();
                    request.setAttribute("products", products);
                    request.getRequestDispatcher("/views/products.jsp").forward(request, response);
                    break;
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
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
                case "create": {
                    Product p = new Product();
                    p.setProductCode(request.getParameter("productCode"));
                    p.setProductName(request.getParameter("productName"));
                    p.setSalePrice(new BigDecimal(request.getParameter("salePrice")));
                    p.setCost(new BigDecimal(request.getParameter("cost")));
                    p.setProductDescription(request.getParameter("productDescription"));
                    p.setProductCategory(request.getParameter("productCategory"));
                    p.setAttributes(request.getParameter("attributes"));
                    int productId = productDAO.createProduct(p);
                    Part imagePart = request.getPart("productImage");
                    if (imagePart != null && imagePart.getSize() > 0) {
                        productDAO.addProductImage(productId, imagePart);
                    }
                    break;
                }
                case "update": {
                    Product p = new Product();
                    p.setProductId(Integer.parseInt(request.getParameter("productId")));
                    p.setProductCode(request.getParameter("productCode"));
                    p.setProductName(request.getParameter("productName"));
                    p.setSalePrice(new BigDecimal(request.getParameter("salePrice")));
                    p.setCost(new BigDecimal(request.getParameter("cost")));
                    p.setProductDescription(request.getParameter("productDescription"));
                    p.setProductCategory(request.getParameter("productCategory"));
                    p.setAttributes(request.getParameter("attributes"));
                    productDAO.updateProduct(p);
                    Part imagePart = request.getPart("productImage");
                    if (imagePart != null && imagePart.getSize() > 0) {
                        productDAO.clearPrimaryImages(p.getProductId());
                        productDAO.addProductImage(p.getProductId(), imagePart);
                    }
                    break;
                }
                case "delete": {
                    int id = Integer.parseInt(request.getParameter("productId"));
                    productDAO.deleteProduct(id);
                    break;
                }
                default:
                    break;
            }
        } catch (SQLException | IOException e) {
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/products");
    }
}
