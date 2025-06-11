package com.warehouse.wms.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.warehouse.wms.dao.ProviderDAO;
import com.warehouse.wms.dao.ProviderProductDAO;
import com.warehouse.wms.model.Provider;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ManageProvidersServlet", urlPatterns = {"/providers"})
public class ManageProvidersServlet extends HttpServlet {
    private ProviderDAO providerDAO;
    private ProviderProductDAO providerProductDAO;

    @Override
    public void init() throws ServletException {
        providerDAO = new ProviderDAO();
        providerProductDAO = new ProviderProductDAO();
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
                    request.getRequestDispatcher("/views/newProvider.jsp").forward(request, response);
                    break;
                case "edit": {
                    int id = Integer.parseInt(request.getParameter("providerId"));
                    Provider provider = providerDAO.getProviderById(id);
                    request.setAttribute("provider", provider);
                    request.getRequestDispatcher("/views/editProvider.jsp").forward(request, response);
                    break;
                }                default: {
                    // Default: list providers with statistics
                    List<Provider> providers = providerDAO.getAllProviders();
                    int totalProviderProducts = providerProductDAO.getTotalProviderProductCount();
                    int completeProviderProducts = providerProductDAO.getCompleteProviderProductCount();
                    
                    request.setAttribute("providers", providers);
                    request.setAttribute("totalProviderProducts", totalProviderProducts);
                    request.setAttribute("completeProviderProducts", completeProviderProducts);
                    request.getRequestDispatcher("/views/providers.jsp").forward(request, response);
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
                    String providerName = request.getParameter("providerName");
                    String email = request.getParameter("email");
                    String address = request.getParameter("address");
                    
                    // Validate input
                    String errorMessage = validateProviderInput(providerName, email, true, 0);
                    if (errorMessage != null) {
                        request.setAttribute("errorMessage", errorMessage);
                        request.setAttribute("providerName", providerName);
                        request.setAttribute("email", email);
                        request.setAttribute("address", address);
                        request.getRequestDispatcher("/views/newProvider.jsp").forward(request, response);
                        return;
                    }
                    
                    Provider provider = new Provider();
                    provider.setProviderName(providerName);
                    provider.setEmail(email);
                    provider.setAddress(address);
                    providerDAO.createProvider(provider);
                    break;
                }
                case "update": {
                    int providerId = Integer.parseInt(request.getParameter("providerId"));
                    String providerName = request.getParameter("providerName");
                    String email = request.getParameter("email");
                    String address = request.getParameter("address");
                    
                    // Validate input
                    String errorMessage = validateProviderInput(providerName, email, false, providerId);
                    if (errorMessage != null) {
                        request.setAttribute("errorMessage", errorMessage);
                        Provider provider = new Provider();
                        provider.setProviderId(providerId);
                        provider.setProviderName(providerName);
                        provider.setEmail(email);
                        provider.setAddress(address);
                        request.setAttribute("provider", provider);
                        request.getRequestDispatcher("/views/editProvider.jsp").forward(request, response);
                        return;
                    }
                    
                    Provider provider = new Provider();
                    provider.setProviderId(providerId);
                    provider.setProviderName(providerName);
                    provider.setEmail(email);
                    provider.setAddress(address);
                    providerDAO.updateProvider(provider);
                    break;
                }
                case "delete": {
                    int id = Integer.parseInt(request.getParameter("providerId"));
                    providerDAO.deleteProvider(id);
                    break;
                }
                default:
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/providers");
    }
    
    private String validateProviderInput(String providerName, String email, boolean isCreate, int providerId) throws SQLException {
        if (providerName == null || providerName.trim().isEmpty()) {
            return "Vui lòng nhập tên nhà cung cấp";
        }
        
        if (email == null || email.trim().isEmpty()) {
            return "Vui lòng nhập email";
        }
        
        // Simple email validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            return "Email không hợp lệ";
        }
        
        // Check if email already exists
        if (isCreate) {
            if (providerDAO.isProviderEmailExists(email.trim())) {
                return "Email đã được sử dụng";
            }
        } else {
            if (providerDAO.isProviderEmailExistsExcludeId(email.trim(), providerId)) {
                return "Email đã được sử dụng";
            }
        }
        
        return null; // No errors
    }
}
