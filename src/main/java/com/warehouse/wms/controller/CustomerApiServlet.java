package com.warehouse.wms.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import com.warehouse.wms.dao.CustomerDAO;
import com.warehouse.wms.model.Customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CustomerApiServlet", urlPatterns = {"/api/customers"})
public class CustomerApiServlet extends HttpServlet {
    private CustomerDAO customerDAO;

    @Override
    public void init() throws ServletException {
        customerDAO = new CustomerDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        
        try (PrintWriter out = response.getWriter()) {
            switch (action) {
                case "list":
                    getAllCustomers(out);
                    break;
                case "search":
                    searchCustomers(request, out);
                    break;
                case "get":
                    getCustomerById(request, out);
                    break;
                default:
                    getAllCustomers(out);
                    break;
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"Database error: " + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }
    
    private void getAllCustomers(PrintWriter out) throws SQLException {
        List<Customer> customers = customerDAO.getAllCustomers();
        writeCustomersJson(out, customers);
    }
    
    private void searchCustomers(HttpServletRequest request, PrintWriter out) throws SQLException {
        String searchTerm = request.getParameter("search");
        List<Customer> customers;
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            customers = customerDAO.searchCustomers(searchTerm.trim());
        } else {
            customers = customerDAO.getAllCustomers();
        }
        
        writeCustomersJson(out, customers);
    }
    
    private void getCustomerById(HttpServletRequest request, PrintWriter out) throws SQLException {
        try {
            int customerId = Integer.parseInt(request.getParameter("id"));
            Customer customer = customerDAO.getCustomerById(customerId);
            
            if (customer != null) {
                out.print("{");
                out.print("\"customerId\":" + customer.getCustomerId() + ",");
                out.print("\"customerName\":\"" + escapeJson(customer.getCustomerName()) + "\",");
                out.print("\"email\":\"" + escapeJson(customer.getEmail()) + "\",");
                out.print("\"address\":\"" + escapeJson(customer.getAddress()) + "\"");
                out.print("}");
            } else {
                out.print("{\"error\":\"Customer not found\"}");
            }
        } catch (NumberFormatException e) {
            out.print("{\"error\":\"Invalid customer ID\"}");
        }
    }
    
    private void writeCustomersJson(PrintWriter out, List<Customer> customers) {
        out.print("{\"data\":[");
        for (int i = 0; i < customers.size(); i++) {
            if (i > 0) out.print(",");
            Customer customer = customers.get(i);
            out.print("{");
            out.print("\"customerId\":" + customer.getCustomerId() + ",");
            out.print("\"customerName\":\"" + escapeJson(customer.getCustomerName()) + "\",");
            out.print("\"email\":\"" + escapeJson(customer.getEmail()) + "\",");
            out.print("\"address\":\"" + escapeJson(customer.getAddress()) + "\"");
            out.print("}");
        }
        out.print("],\"count\":" + customers.size() + "}");
    }
    
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
