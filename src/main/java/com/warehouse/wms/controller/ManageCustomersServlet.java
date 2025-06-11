package com.warehouse.wms.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.warehouse.wms.dao.CustomerDAO;
import com.warehouse.wms.model.Customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ManageCustomersServlet", urlPatterns = {"/customers"})
public class ManageCustomersServlet extends HttpServlet {
    private CustomerDAO customerDAO;

    @Override
    public void init() throws ServletException {
        customerDAO = new CustomerDAO();
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
                case "search":
                    searchCustomers(request, response);
                    break;
                default:
                    listCustomers(request, response);
                    break;
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
                    String customerName = request.getParameter("customerName");
                    String email = request.getParameter("email");
                    String address = request.getParameter("address");
                    
                    // Validate input
                    String errorMessage = validateCustomerInput(customerName, email, true, 0);
                    if (errorMessage != null) {
                        request.setAttribute("errorMessage", errorMessage);
                        request.setAttribute("customerName", customerName);
                        request.setAttribute("email", email);
                        request.setAttribute("address", address);
                        request.getRequestDispatcher("/views/newCustomer.jsp").forward(request, response);
                        return;
                    }
                    
                    Customer customer = new Customer();
                    customer.setCustomerName(customerName);
                    customer.setEmail(email);
                    customer.setAddress(address);
                    customerDAO.createCustomer(customer);
                    break;
                }
                case "update": {
                    int customerId = Integer.parseInt(request.getParameter("customerId"));
                    String customerName = request.getParameter("customerName");
                    String email = request.getParameter("email");
                    String address = request.getParameter("address");
                    
                    // Validate input
                    String errorMessage = validateCustomerInput(customerName, email, false, customerId);
                    if (errorMessage != null) {
                        request.setAttribute("errorMessage", errorMessage);
                        Customer customer = new Customer();
                        customer.setCustomerId(customerId);
                        customer.setCustomerName(customerName);
                        customer.setEmail(email);
                        customer.setAddress(address);
                        request.setAttribute("customer", customer);
                        request.getRequestDispatcher("/views/editCustomer.jsp").forward(request, response);
                        return;
                    }
                    
                    Customer customer = new Customer();
                    customer.setCustomerId(customerId);
                    customer.setCustomerName(customerName);
                    customer.setEmail(email);
                    customer.setAddress(address);
                    customerDAO.updateCustomer(customer);
                    break;
                }
                case "delete": {
                    int id = Integer.parseInt(request.getParameter("customerId"));
                    customerDAO.deleteCustomer(id);
                    break;
                }
                default:
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/customers");
    }
    
    private void listCustomers(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Customer> customers = customerDAO.getAllCustomers();
        request.setAttribute("customers", customers);
        request.getRequestDispatcher("/views/customers.jsp").forward(request, response);
    }
    
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/newCustomer.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Customer customer = customerDAO.getCustomerById(id);
        if (customer == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Khách hàng không tồn tại");
            return;
        }
        request.setAttribute("customer", customer);
        request.getRequestDispatcher("/views/editCustomer.jsp").forward(request, response);
    }
    
    private void searchCustomers(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String searchTerm = request.getParameter("search");
        List<Customer> customers;
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            customers = customerDAO.searchCustomers(searchTerm.trim());
            request.setAttribute("searchTerm", searchTerm);
        } else {
            customers = customerDAO.getAllCustomers();
        }
        
        request.setAttribute("customers", customers);
        request.getRequestDispatcher("/views/customers.jsp").forward(request, response);
    }
    
    private String validateCustomerInput(String customerName, String email, boolean isCreate, int customerId) throws SQLException {
        // Validate customer name
        if (customerName == null || customerName.trim().isEmpty()) {
            return "Tên khách hàng không được để trống!";
        }
        
        if (customerName.trim().length() > 100) {
            return "Tên khách hàng không được vượt quá 100 ký tự!";
        }
        
        // Validate email
        if (email != null && !email.trim().isEmpty()) {
            if (email.trim().length() > 255) {
                return "Email không được vượt quá 255 ký tự!";
            }
            
            // Basic email format validation
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return "Định dạng email không hợp lệ!";
            }
            
            // Check if email already exists
            if (isCreate) {
                if (customerDAO.isCustomerEmailExists(email.trim())) {
                    return "Email này đã được sử dụng bởi khách hàng khác!";
                }
            } else {
                if (customerDAO.isCustomerEmailExistsExcludeId(email.trim(), customerId)) {
                    return "Email này đã được sử dụng bởi khách hàng khác!";
                }
            }
        }
        
        return null; // No validation errors
    }
}
