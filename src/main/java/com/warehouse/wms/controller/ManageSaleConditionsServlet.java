package com.warehouse.wms.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.warehouse.wms.dao.SaleConditionDAO;
import com.warehouse.wms.model.SaleCondition;
import com.warehouse.wms.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ManageSaleConditionsServlet", urlPatterns = {"/sale-conditions"})
public class ManageSaleConditionsServlet extends HttpServlet {
    private SaleConditionDAO saleConditionDAO;

    @Override
    public void init() throws ServletException {
        saleConditionDAO = new SaleConditionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "list":
                    listSaleConditions(request, response);
                    break;
                case "new":
                    showNewSaleConditionForm(request, response);
                    break;
                case "edit":
                    showEditSaleConditionForm(request, response);
                    break;
                case "delete":
                    deleteSaleCondition(request, response);
                    break;
                default:
                    listSaleConditions(request, response);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
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
                    createSaleCondition(request, response);
                    break;
                case "update":
                    updateSaleCondition(request, response);
                    break;
                default:
                    listSaleConditions(request, response);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/views/error/500.jsp").forward(request, response);
        }
    }

    private void listSaleConditions(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String searchTerm = request.getParameter("search");
        List<SaleCondition> saleConditions;
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            saleConditions = saleConditionDAO.searchSaleConditions(searchTerm.trim());
        } else {
            saleConditions = saleConditionDAO.getAllSaleConditions();
        }
        
        int totalCount = saleConditionDAO.getTotalSaleConditionCount();
        
        request.setAttribute("saleConditions", saleConditions);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("searchTerm", searchTerm);
        
        request.getRequestDispatcher("/views/saleConditions.jsp").forward(request, response);
    }

    private void showNewSaleConditionForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/newSaleCondition.jsp").forward(request, response);
    }

    private void showEditSaleConditionForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/sale-conditions");
            return;
        }

        try {
            int saleConditionId = Integer.parseInt(idParam);
            SaleCondition saleCondition = saleConditionDAO.getSaleConditionById(saleConditionId);
            
            if (saleCondition == null) {
                request.setAttribute("errorMessage", "Sale condition not found.");
                listSaleConditions(request, response);
                return;
            }
            
            request.setAttribute("saleCondition", saleCondition);
            request.getRequestDispatcher("/views/editSaleCondition.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/sale-conditions");
        }
    }

    private void createSaleCondition(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String conditionCode = request.getParameter("conditionCode");
        String amountStr = request.getParameter("amount");
        String type = request.getParameter("type");
        String effectiveDateStr = request.getParameter("effectiveDate");
        String expiredDateStr = request.getParameter("expiredDate");
        
        // Get current user
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String createdBy = user.getStaffName();

        // Validate input
        String errorMessage = validateSaleConditionInput(conditionCode, amountStr, type, 
                                                       effectiveDateStr, expiredDateStr, true, 0);
        
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.setAttribute("conditionCode", conditionCode);
            request.setAttribute("amount", amountStr);
            request.setAttribute("type", type);
            request.setAttribute("effectiveDate", effectiveDateStr);
            request.setAttribute("expiredDate", expiredDateStr);
            request.getRequestDispatcher("/views/newSaleCondition.jsp").forward(request, response);
            return;
        }

        try {
            SaleCondition saleCondition = new SaleCondition();
            saleCondition.setConditionCode(conditionCode.trim());
            saleCondition.setAmount(new BigDecimal(amountStr));
            saleCondition.setType(type);
            saleCondition.setEffectiveDate(LocalDateTime.parse(effectiveDateStr, 
                                         DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
            
            if (expiredDateStr != null && !expiredDateStr.trim().isEmpty()) {
                saleCondition.setExpiredDate(LocalDateTime.parse(expiredDateStr, 
                                           DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
            }
            
            saleCondition.setCreatedBy(createdBy);

            boolean created = saleConditionDAO.createSaleCondition(saleCondition);
            
            if (created) {
                request.setAttribute("successMessage", "Sale condition created successfully!");
            } else {
                request.setAttribute("errorMessage", "Failed to create sale condition.");
            }
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error creating sale condition: " + e.getMessage());
        }
        
        listSaleConditions(request, response);
    }

    private void updateSaleCondition(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String idParam = request.getParameter("id");
        String conditionCode = request.getParameter("conditionCode");
        String amountStr = request.getParameter("amount");
        String type = request.getParameter("type");
        String effectiveDateStr = request.getParameter("effectiveDate");
        String expiredDateStr = request.getParameter("expiredDate");

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/sale-conditions");
            return;
        }

        try {
            int saleConditionId = Integer.parseInt(idParam);
            
            // Validate input
            String errorMessage = validateSaleConditionInput(conditionCode, amountStr, type, 
                                                           effectiveDateStr, expiredDateStr, false, saleConditionId);
            
            if (errorMessage != null) {
                SaleCondition saleCondition = saleConditionDAO.getSaleConditionById(saleConditionId);
                request.setAttribute("errorMessage", errorMessage);
                request.setAttribute("saleCondition", saleCondition);
                request.getRequestDispatcher("/views/editSaleCondition.jsp").forward(request, response);
                return;
            }

            SaleCondition saleCondition = new SaleCondition();
            saleCondition.setSaleConditionId(saleConditionId);
            saleCondition.setConditionCode(conditionCode.trim());
            saleCondition.setAmount(new BigDecimal(amountStr));
            saleCondition.setType(type);
            saleCondition.setEffectiveDate(LocalDateTime.parse(effectiveDateStr, 
                                         DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
            
            if (expiredDateStr != null && !expiredDateStr.trim().isEmpty()) {
                saleCondition.setExpiredDate(LocalDateTime.parse(expiredDateStr, 
                                           DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
            }

            boolean updated = saleConditionDAO.updateSaleCondition(saleCondition);
            
            if (updated) {
                request.setAttribute("successMessage", "Sale condition updated successfully!");
            } else {
                request.setAttribute("errorMessage", "Failed to update sale condition.");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid sale condition ID.");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error updating sale condition: " + e.getMessage());
        }
        
        listSaleConditions(request, response);
    }

    private void deleteSaleCondition(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/sale-conditions");
            return;
        }

        try {
            int saleConditionId = Integer.parseInt(idParam);
            boolean deleted = saleConditionDAO.deleteSaleCondition(saleConditionId);
            
            if (deleted) {
                request.setAttribute("successMessage", "Sale condition deleted successfully!");
            } else {
                request.setAttribute("errorMessage", "Failed to delete sale condition.");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid sale condition ID.");
        }
        
        listSaleConditions(request, response);
    }

    private String validateSaleConditionInput(String conditionCode, String amountStr, String type,
                                            String effectiveDateStr, String expiredDateStr, 
                                            boolean isCreate, int saleConditionId) throws SQLException {
        
        // Validate condition code
        if (conditionCode == null || conditionCode.trim().isEmpty()) {
            return "Condition code is required.";
        }
        
        if (conditionCode.trim().length() > 50) {
            return "Condition code must not exceed 50 characters.";
        }
        
        // Check if condition code already exists
        if (isCreate) {
            if (saleConditionDAO.conditionCodeExists(conditionCode.trim())) {
                return "Condition code already exists.";
            }
        } else {
            if (saleConditionDAO.conditionCodeExists(conditionCode.trim(), saleConditionId)) {
                return "Condition code already exists.";
            }
        }
        
        // Validate amount
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return "Amount is required.";
        }
        
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return "Amount must be greater than 0.";
            }
            
            // For percentage type, amount should not exceed 100
            if ("percentage".equals(type) && amount.compareTo(new BigDecimal("100")) > 0) {
                return "Percentage amount cannot exceed 100%.";
            }
            
        } catch (NumberFormatException e) {
            return "Invalid amount format.";
        }
          // Validate type
        if (type == null || type.trim().isEmpty()) {
            return "Type is required.";
        }
        
        if (!"percentage".equals(type) && !"fixed".equals(type)) {
            return "Type must be either 'percentage' or 'fixed'.";
        }
        
        // Validate effective date
        if (effectiveDateStr == null || effectiveDateStr.trim().isEmpty()) {
            return "Effective date is required.";
        }
        
        try {
            LocalDateTime effectiveDate = LocalDateTime.parse(effectiveDateStr, 
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            
            // Validate expired date if provided
            if (expiredDateStr != null && !expiredDateStr.trim().isEmpty()) {
                LocalDateTime expiredDate = LocalDateTime.parse(expiredDateStr, 
                                          DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                
                if (expiredDate.isBefore(effectiveDate) || expiredDate.isEqual(effectiveDate)) {
                    return "Expired date must be after effective date.";
                }
            }
            
        } catch (DateTimeParseException e) {
            return "Invalid date format.";
        }
        
        return null; // No validation errors
    }
}
