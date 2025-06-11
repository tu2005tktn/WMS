package com.warehouse.wms.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import com.warehouse.wms.dao.ProviderProductDAO;
import com.warehouse.wms.model.ProviderProduct;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProviderProductApiServlet", urlPatterns = {"/api/provider-products"})
public class ProviderProductApiServlet extends HttpServlet {
    private ProviderProductDAO providerProductDAO;

    @Override
    public void init() throws ServletException {
        providerProductDAO = new ProviderProductDAO();
        // Log servlet initialization
        getServletContext().log("ProviderProductApiServlet initialized successfully");
        System.out.println("ProviderProductApiServlet initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Add CORS headers
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Default action
        }
        
        // Log incoming request
        getServletContext().log("ProviderProductApiServlet: Received request with action: " + action);
        System.out.println("ProviderProductApiServlet: Received request with action: " + action);
        
        try (PrintWriter out = response.getWriter()) { // Ensure PrintWriter is initialized here
            switch (action) {
                case "by-provider":
                    getProductsByProvider(request, out);
                    break;
                case "by-product":
                    getProvidersByProduct(request, out);
                    break;
                case "for-purchase":
                    getForPurchaseOrder(out);
                    break;
                default:
                    getAllProviderProducts(out);
                    break;
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Bad request: " + e.getMessage());
        } catch (SQLException e) {
            // Log server-side for detailed diagnostics
            getServletContext().log("SQL Error in ProviderProductApiServlet: " + e.getMessage(), e);
            System.err.println("SQL Error in ProviderProductApiServlet: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred.");
        } catch (IOException e) { // Catch IOException for PrintWriter issues
            getServletContext().log("IO Error in ProviderProductApiServlet: " + e.getMessage(), e);
            System.err.println("IO Error in ProviderProductApiServlet: " + e.getMessage());
            // Don't try to sendErrorResponse if it's an IOException, as the stream might be closed.
        } catch (Exception e) {
            getServletContext().log("Unexpected error in ProviderProductApiServlet: " + e.getMessage(), e);
            System.err.println("Unexpected error in ProviderProductApiServlet: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String errorMessage) throws IOException {
        if (!response.isCommitted()) {
            response.setStatus(statusCode); // Set status code before getting writer
            response.setContentType("application/json"); // Ensure content type is set for error response
            response.setCharacterEncoding("UTF-8");
            try (PrintWriter errOut = response.getWriter()) {
                errOut.print("{\"error\":\"" + escapeJson(errorMessage) + "\"}");
            }
        } else {
            // If response is committed, we can't send a new error body/status.
            // Log this situation on the server.
            getServletContext().log("Attempted to send error but response was already committed. Status: " + statusCode + ", Message: " + errorMessage);
        }
    }
    
    private void getAllProviderProducts(PrintWriter out) throws SQLException {
        List<ProviderProduct> providerProducts = providerProductDAO.getAllProviderProducts();
        writeProviderProductsJson(out, providerProducts);
    }
      private void getProductsByProvider(HttpServletRequest request, PrintWriter out) throws SQLException, NumberFormatException, IllegalArgumentException {
        String providerIdStr = request.getParameter("providerId");
        if (providerIdStr == null || providerIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("providerId parameter is required and cannot be empty.");
        }
        int providerId = Integer.parseInt(providerIdStr); // Can throw NumberFormatException
        
        // Log the request
        getServletContext().log("ProviderProductApiServlet: Getting products for providerId: " + providerId);
        
        List<ProviderProduct> providerProducts = providerProductDAO.getProviderProductsByProviderId(providerId);
        
        // Log the results
        getServletContext().log("ProviderProductApiServlet: Found " + providerProducts.size() + " products for providerId: " + providerId);
        
        writeProviderProductsJson(out, providerProducts);
    }
    
    private void getProvidersByProduct(HttpServletRequest request, PrintWriter out) throws SQLException, NumberFormatException, IllegalArgumentException {
        String productIdStr = request.getParameter("productId");
        if (productIdStr == null || productIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("productId parameter is required and cannot be empty.");
        }
        int productId = Integer.parseInt(productIdStr); // Can throw NumberFormatException
        List<ProviderProduct> providerProducts = providerProductDAO.getProviderProductsByProductId(productId);
        writeProviderProductsJson(out, providerProducts);
    }
    
    private void getForPurchaseOrder(PrintWriter out) throws SQLException {
        List<ProviderProduct> providerProducts = providerProductDAO.getProviderProductsForPurchaseOrder();
        writeProviderProductsJson(out, providerProducts);
    }
    
    private void writeProviderProductsJson(PrintWriter out, List<ProviderProduct> providerProducts) {
        out.print("{\"data\":[");
        for (int i = 0; i < providerProducts.size(); i++) {
            if (i > 0) out.print(",");
            ProviderProduct pp = providerProducts.get(i);
            out.print("{");
            out.print("\"providerId\":" + pp.getProviderId() + ",");
            out.print("\"productId\":" + pp.getProductId() + ",");
            out.print("\"providerName\":\"" + escapeJson(pp.getProviderName()) + "\",");
            out.print("\"productName\":\"" + escapeJson(pp.getProductName()) + "\",");
            out.print("\"productCode\":\"" + escapeJson(pp.getProductCode()) + "\",");
            out.print("\"productCategory\":\"" + escapeJson(pp.getProductCategory()) + "\",");
            out.print("\"deliveryDuration\":" + (pp.getDeliveryDuration() != null ? pp.getDeliveryDuration() : "null") + ",");
            out.print("\"estimatedPrice\":" + (pp.getEstimatedPrice() != null ? pp.getEstimatedPrice() : "null") + ",");
            out.print("\"policies\":\"" + escapeJson(pp.getPolicies()) + "\"");
            out.print("}");
        }
        out.print("],\"count\":" + providerProducts.size() + "}");
    }

    // Ensure escapeJson method is present and correct
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t")
                  .replace("/", "\\/"); 
    }
}
