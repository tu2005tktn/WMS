package com.warehouse.wms.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import com.warehouse.wms.dao.ProductDAO;
import com.warehouse.wms.model.ProductImage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProductImageServlet", urlPatterns = {"/productImage"})
public class ProductImageServlet extends HttpServlet {
    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String imageIdParam = request.getParameter("imageId");
        if (imageIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int imageId;
        try {
            imageId = Integer.parseInt(imageIdParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            ProductImage img = productDAO.getProductImageById(imageId);
            if (img == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            response.setContentType(img.getContentType());
            response.setContentLength(img.getData().length);
            try (OutputStream os = response.getOutputStream()) {
                os.write(img.getData());
                os.flush();
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
