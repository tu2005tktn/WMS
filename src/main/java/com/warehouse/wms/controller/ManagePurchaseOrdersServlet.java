package com.warehouse.wms.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.warehouse.wms.dao.ProviderDAO;
import com.warehouse.wms.dao.ProviderProductDAO;
import com.warehouse.wms.dao.PurchaseOrderDAO;
import com.warehouse.wms.model.Provider;
import com.warehouse.wms.model.ProviderProduct;
import com.warehouse.wms.model.PurchaseOrder;
import com.warehouse.wms.model.PurchaseOrderDetail;
import com.warehouse.wms.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ManagePurchaseOrdersServlet", urlPatterns = {"/purchase-orders"})
public class ManagePurchaseOrdersServlet extends HttpServlet {
    private PurchaseOrderDAO purchaseOrderDAO;
    private ProviderDAO providerDAO;
    private ProviderProductDAO providerProductDAO;

    @Override
    public void init() throws ServletException {
        purchaseOrderDAO = new PurchaseOrderDAO();
        providerDAO = new ProviderDAO();
        providerProductDAO = new ProviderProductDAO();
    }    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?message=Session expired or not logged in. Please login again.");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Default action
        }

        try {
            switch (action) {
                case "list":
                    listPurchaseOrders(request, response);
                    break;
                case "new":
                    showNewPurchaseOrderForm(request, response);
                    break;
                case "view":
                    viewPurchaseOrder(request, response);
                    break;
                case "edit":
                    showEditPurchaseOrderForm(request, response);
                    break;
                default:
                    // Log unknown action or redirect to a default page/error page
                    System.out.println("Unknown action: " + action); // Replace with logger
                    listPurchaseOrders(request, response); // Or redirect to an error page
                    break;
            }
        } catch (SQLException e) {
            // Log the error and redirect to an error page
            e.printStackTrace(); // TODO: Replace with a proper logger
            request.setAttribute("errorMessage", "Lỗi thao tác với cơ sở dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/views/error/500.jsp").forward(request, response);
        } catch (Exception e) { // Catch other runtime exceptions
            e.printStackTrace(); // TODO: Replace with a proper logger
            request.setAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            request.getRequestDispatcher("/views/error/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        
        try {
            switch (action) {
                case "create":
                    createPurchaseOrder(request, response);
                    break;
                case "update":
                    updatePurchaseOrder(request, response);
                    break;
                default:
                    listPurchaseOrders(request, response);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/views/error/500.jsp").forward(request, response);
        }
    }

    private void listPurchaseOrders(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String searchTerm = request.getParameter("search");
        String statusFilter = request.getParameter("status");
        List<PurchaseOrder> purchaseOrders;
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            purchaseOrders = purchaseOrderDAO.searchPurchaseOrders(searchTerm.trim());
        } else if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            purchaseOrders = purchaseOrderDAO.getPurchaseOrdersByStatus(statusFilter);
        } else {
            purchaseOrders = purchaseOrderDAO.getAllPurchaseOrders();
        }
        
        int totalCount = purchaseOrderDAO.getTotalPurchaseOrderCount();
        
        request.setAttribute("purchaseOrders", purchaseOrders);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("searchTerm", searchTerm);
        request.setAttribute("statusFilter", statusFilter);
        
        request.getRequestDispatcher("/views/purchaseOrders.jsp").forward(request, response);
    }

    private void showNewPurchaseOrderForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        // Load providers for the dropdown
        List<Provider> providers = providerDAO.getAllProviders();
        request.setAttribute("providers", providers);
        
        // Load provider-products for selection
        List<ProviderProduct> providerProducts = providerProductDAO.getProviderProductsForPurchaseOrder();
        request.setAttribute("providerProducts", providerProducts);
        
        request.getRequestDispatcher("/views/newPurchaseOrder.jsp").forward(request, response);
    }

    private void viewPurchaseOrder(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/purchase-orders");
            return;
        }

        try {
            int poId = Integer.parseInt(idParam);
            PurchaseOrder purchaseOrder = purchaseOrderDAO.getPurchaseOrderById(poId);
            
            if (purchaseOrder == null) {
                request.setAttribute("errorMessage", "Purchase order not found.");
                listPurchaseOrders(request, response);
                return;
            }
            
            request.setAttribute("purchaseOrder", purchaseOrder);
            request.getRequestDispatcher("/views/viewPurchaseOrder.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/purchase-orders");
        }
    }

    private void showEditPurchaseOrderForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/purchase-orders");
            return;
        }

        try {
            int poId = Integer.parseInt(idParam);
            PurchaseOrder purchaseOrder = purchaseOrderDAO.getPurchaseOrderById(poId);
            
            if (purchaseOrder == null) {
                request.setAttribute("errorMessage", "Purchase order not found.");
                listPurchaseOrders(request, response);
                return;
            }
            
            // Load providers for the dropdown
            List<Provider> providers = providerDAO.getAllProviders();
            request.setAttribute("providers", providers);
            
            request.setAttribute("purchaseOrder", purchaseOrder);
            request.getRequestDispatcher("/views/editPurchaseOrder.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/purchase-orders");
        }
    }

    private void createPurchaseOrder(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String providerIdStr = request.getParameter("providerId");
        String deliveryDateStr = request.getParameter("deliveryDate");
        String status = request.getParameter("status");
        String notes = request.getParameter("notes");
        
        // Get current user
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String createdBy = user.getStaffName();

        // Validate input
        String errorMessage = validatePurchaseOrderInput(providerIdStr, deliveryDateStr, status, true, 0);
        
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.setAttribute("providerId", providerIdStr);
            request.setAttribute("deliveryDate", deliveryDateStr);
            request.setAttribute("status", status);
            request.setAttribute("notes", notes);
            
            // Reload form data
            List<Provider> providers = providerDAO.getAllProviders();
            request.setAttribute("providers", providers);
            List<ProviderProduct> providerProducts = providerProductDAO.getProviderProductsForPurchaseOrder();
            request.setAttribute("providerProducts", providerProducts);
            
            request.getRequestDispatcher("/views/newPurchaseOrder.jsp").forward(request, response);
            return;
        }

        try {
            PurchaseOrder purchaseOrder = new PurchaseOrder();
            purchaseOrder.setProviderId(Integer.parseInt(providerIdStr));
            
            if (deliveryDateStr != null && !deliveryDateStr.trim().isEmpty()) {
                purchaseOrder.setDeliveryDate(LocalDateTime.parse(deliveryDateStr, 
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
            }
            
            purchaseOrder.setStatus(status != null ? status : "PENDING");
            purchaseOrder.setCreatedBy(createdBy);
            purchaseOrder.setNotes(notes);

            // Parse purchase order details from form
            List<PurchaseOrderDetail> details = parsePurchaseOrderDetails(request);
            purchaseOrder.setDetails(details);

            int poId = purchaseOrderDAO.createPurchaseOrder(purchaseOrder);
            
            if (poId > 0) {
                request.setAttribute("successMessage", "Purchase order created successfully!");
            } else {
                request.setAttribute("errorMessage", "Failed to create purchase order.");
            }
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error creating purchase order: " + e.getMessage());
        }
        
        listPurchaseOrders(request, response);
    }

    private void updatePurchaseOrder(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String idParam = request.getParameter("id");
        String providerIdStr = request.getParameter("providerId");
        String deliveryDateStr = request.getParameter("deliveryDate");
        String status = request.getParameter("status");
        String notes = request.getParameter("notes");

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/purchase-orders");
            return;
        }

        try {
            int poId = Integer.parseInt(idParam);
            
            // Validate input
            String errorMessage = validatePurchaseOrderInput(providerIdStr, deliveryDateStr, status, false, poId);
            
            if (errorMessage != null) {
                PurchaseOrder purchaseOrder = purchaseOrderDAO.getPurchaseOrderById(poId);
                request.setAttribute("errorMessage", errorMessage);
                request.setAttribute("purchaseOrder", purchaseOrder);
                
                // Reload form data
                List<Provider> providers = providerDAO.getAllProviders();
                request.setAttribute("providers", providers);
                
                request.getRequestDispatcher("/views/editPurchaseOrder.jsp").forward(request, response);
                return;
            }

            PurchaseOrder purchaseOrder = new PurchaseOrder();
            purchaseOrder.setPoId(poId);
            purchaseOrder.setProviderId(Integer.parseInt(providerIdStr));
            
            if (deliveryDateStr != null && !deliveryDateStr.trim().isEmpty()) {
                purchaseOrder.setDeliveryDate(LocalDateTime.parse(deliveryDateStr, 
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
            }
            
            purchaseOrder.setStatus(status);
            purchaseOrder.setNotes(notes);

            boolean updated = purchaseOrderDAO.updatePurchaseOrder(purchaseOrder);
            
            if (updated) {
                request.setAttribute("successMessage", "Purchase order updated successfully!");
            } else {
                request.setAttribute("errorMessage", "Failed to update purchase order.");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid purchase order ID.");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error updating purchase order: " + e.getMessage());
        }
        
        listPurchaseOrders(request, response);
    }

    private void deletePurchaseOrder(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/purchase-orders");
            return;
        }

        try {
            int poId = Integer.parseInt(idParam);
            boolean deleted = purchaseOrderDAO.deletePurchaseOrder(poId);
            
            if (deleted) {
                request.setAttribute("successMessage", "Purchase order deleted successfully!");
            } else {
                request.setAttribute("errorMessage", "Failed to delete purchase order.");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid purchase order ID.");
        }
        
        listPurchaseOrders(request, response);
    }    private String validatePurchaseOrderInput(String providerIdStr, String deliveryDateStr, 
                                            String status, boolean isCreate, int poId) {
        
        // Validate provider ID
        if (providerIdStr == null || providerIdStr.trim().isEmpty()) {
            return "Provider is required.";
        }
        
        try {
            int providerId = Integer.parseInt(providerIdStr);
            if (providerId <= 0) {
                return "Invalid provider selected.";
            }
        } catch (NumberFormatException e) {
            return "Invalid provider ID format.";
        }
        
        // Validate delivery date if provided
        if (deliveryDateStr != null && !deliveryDateStr.trim().isEmpty()) {
            try {
                LocalDateTime deliveryDate = LocalDateTime.parse(deliveryDateStr, 
                                           DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                
                // Check if delivery date is in the future
                if (deliveryDate.isBefore(LocalDateTime.now())) {
                    return "Delivery date must be in the future.";
                }
                
            } catch (DateTimeParseException e) {
                return "Invalid delivery date format.";
            }
        }
        
        // Validate status
        if (status != null && !status.trim().isEmpty()) {
            String[] validStatuses = {"PENDING", "APPROVED", "ORDERED", "RECEIVED", "CANCELLED"};
            boolean validStatus = false;
            for (String validStat : validStatuses) {
                if (validStat.equals(status)) {
                    validStatus = true;
                    break;
                }
            }
            if (!validStatus) {
                return "Invalid status selected.";
            }
        }
        
        return null; // No validation errors
    }

    private List<PurchaseOrderDetail> parsePurchaseOrderDetails(HttpServletRequest request) {
        List<PurchaseOrderDetail> details = new ArrayList<>();
        
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");
        String[] prices = request.getParameterValues("price");
        
        if (productIds != null && quantities != null && prices != null) {
            int length = Math.min(Math.min(productIds.length, quantities.length), prices.length);
            
            for (int i = 0; i < length; i++) {
                try {
                    if (productIds[i] != null && !productIds[i].trim().isEmpty() &&
                        quantities[i] != null && !quantities[i].trim().isEmpty() &&
                        prices[i] != null && !prices[i].trim().isEmpty()) {
                        
                        PurchaseOrderDetail detail = new PurchaseOrderDetail();
                        detail.setProductId(Integer.parseInt(productIds[i]));
                        detail.setQuantity(Integer.parseInt(quantities[i]));
                        detail.setPrice(new BigDecimal(prices[i]));
                        detail.setStatus("PENDING");
                        
                        details.add(detail);
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid entries
                }
            }
        }
        
        return details;
    }
}
