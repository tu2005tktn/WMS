package com.warehouse.wms.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import com.warehouse.wms.dao.SaleConditionDAO;
import com.warehouse.wms.model.SaleCondition;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "SaleConditionApiServlet", urlPatterns = {"/api/sale-conditions"})
public class SaleConditionApiServlet extends HttpServlet {
    private SaleConditionDAO saleConditionDAO;

    @Override
    public void init() throws ServletException {
        saleConditionDAO = new SaleConditionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        PrintWriter out = response.getWriter();
        
        try {
            switch (action != null ? action : "list") {
                case "list":
                    getAllSaleConditions(out);
                    break;
                case "active":
                    getActiveSaleConditions(out);
                    break;
                case "search":
                    searchSaleConditions(request, out);
                    break;
                case "get":
                    getSaleConditionById(request, out);
                    break;
                default:
                    getAllSaleConditions(out);
                    break;
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Database error: " + e.getMessage() + "\"}");
        }
    }
    
    private void getAllSaleConditions(PrintWriter out) throws SQLException {
        List<SaleCondition> saleConditions = saleConditionDAO.getAllSaleConditions();
        out.println("[");
        for (int i = 0; i < saleConditions.size(); i++) {
            SaleCondition sc = saleConditions.get(i);
            out.println("  {");
            out.println("    \"id\": " + sc.getSaleConditionId() + ",");
            out.println("    \"conditionCode\": \"" + escapeJson(sc.getConditionCode()) + "\",");
            out.println("    \"amount\": " + sc.getAmount() + ",");
            out.println("    \"type\": \"" + escapeJson(sc.getType()) + "\",");
            out.println("    \"effectiveDate\": \"" + sc.getEffectiveDate() + "\",");
            out.println("    \"expiredDate\": " + (sc.getExpiredDate() != null ? "\"" + sc.getExpiredDate() + "\"" : "null") + ",");
            out.println("    \"createdBy\": \"" + escapeJson(sc.getCreatedBy()) + "\",");
            out.println("    \"createdDate\": \"" + sc.getCreatedDate() + "\",");
            out.println("    \"active\": " + sc.isActive());
            out.print("  }");
            if (i < saleConditions.size() - 1) {
                out.println(",");
            } else {
                out.println();
            }
        }
        out.println("]");
    }
    
    private void getActiveSaleConditions(PrintWriter out) throws SQLException {
        List<SaleCondition> saleConditions = saleConditionDAO.getActiveSaleConditions();
        out.println("[");
        for (int i = 0; i < saleConditions.size(); i++) {
            SaleCondition sc = saleConditions.get(i);
            out.println("  {");
            out.println("    \"id\": " + sc.getSaleConditionId() + ",");
            out.println("    \"conditionCode\": \"" + escapeJson(sc.getConditionCode()) + "\",");
            out.println("    \"amount\": " + sc.getAmount() + ",");
            out.println("    \"type\": \"" + escapeJson(sc.getType()) + "\",");
            out.println("    \"effectiveDate\": \"" + sc.getEffectiveDate() + "\",");
            out.println("    \"expiredDate\": " + (sc.getExpiredDate() != null ? "\"" + sc.getExpiredDate() + "\"" : "null") + ",");
            out.println("    \"createdBy\": \"" + escapeJson(sc.getCreatedBy()) + "\",");
            out.println("    \"createdDate\": \"" + sc.getCreatedDate() + "\"");
            out.print("  }");
            if (i < saleConditions.size() - 1) {
                out.println(",");
            } else {
                out.println();
            }
        }
        out.println("]");
    }
    
    private void searchSaleConditions(HttpServletRequest request, PrintWriter out) throws SQLException {
        String searchTerm = request.getParameter("q");
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            getAllSaleConditions(out);
            return;
        }
        
        List<SaleCondition> saleConditions = saleConditionDAO.searchSaleConditions(searchTerm.trim());
        out.println("[");
        for (int i = 0; i < saleConditions.size(); i++) {
            SaleCondition sc = saleConditions.get(i);
            out.println("  {");
            out.println("    \"id\": " + sc.getSaleConditionId() + ",");
            out.println("    \"conditionCode\": \"" + escapeJson(sc.getConditionCode()) + "\",");
            out.println("    \"amount\": " + sc.getAmount() + ",");
            out.println("    \"type\": \"" + escapeJson(sc.getType()) + "\",");
            out.println("    \"effectiveDate\": \"" + sc.getEffectiveDate() + "\",");
            out.println("    \"expiredDate\": " + (sc.getExpiredDate() != null ? "\"" + sc.getExpiredDate() + "\"" : "null") + ",");
            out.println("    \"createdBy\": \"" + escapeJson(sc.getCreatedBy()) + "\",");
            out.println("    \"createdDate\": \"" + sc.getCreatedDate() + "\",");
            out.println("    \"active\": " + sc.isActive());
            out.print("  }");
            if (i < saleConditions.size() - 1) {
                out.println(",");
            } else {
                out.println();
            }
        }
        out.println("]");
    }
    
    private void getSaleConditionById(HttpServletRequest request, PrintWriter out) throws SQLException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            out.println("{\"error\": \"ID parameter is required\"}");
            return;
        }
        
        try {
            int id = Integer.parseInt(idParam);
            SaleCondition sc = saleConditionDAO.getSaleConditionById(id);
            
            if (sc == null) {
                out.println("{\"error\": \"Sale condition not found\"}");
                return;
            }
            
            out.println("{");
            out.println("  \"id\": " + sc.getSaleConditionId() + ",");
            out.println("  \"conditionCode\": \"" + escapeJson(sc.getConditionCode()) + "\",");
            out.println("  \"amount\": " + sc.getAmount() + ",");
            out.println("  \"type\": \"" + escapeJson(sc.getType()) + "\",");
            out.println("  \"effectiveDate\": \"" + sc.getEffectiveDate() + "\",");
            out.println("  \"expiredDate\": " + (sc.getExpiredDate() != null ? "\"" + sc.getExpiredDate() + "\"" : "null") + ",");
            out.println("  \"createdBy\": \"" + escapeJson(sc.getCreatedBy()) + "\",");
            out.println("  \"createdDate\": \"" + sc.getCreatedDate() + "\",");
            out.println("  \"active\": " + sc.isActive());
            out.println("}");
            
        } catch (NumberFormatException e) {
            out.println("{\"error\": \"Invalid ID format\"}");
        }
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
