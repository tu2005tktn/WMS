package com.warehouse.wms.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.warehouse.wms.dao.WarehouseDAO;
import com.warehouse.wms.model.Warehouse;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ManageWarehousesServlet", urlPatterns = {"/warehouses"})
public class ManageWarehousesServlet extends HttpServlet {
    private WarehouseDAO warehouseDAO;

    @Override
    public void init() throws ServletException {
        warehouseDAO = new WarehouseDAO();
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
                    request.getRequestDispatcher("/views/newWarehouse.jsp").forward(request, response);
                    break;
                case "edit": {
                    int id = Integer.parseInt(request.getParameter("warehouseId"));
                    Warehouse warehouse = warehouseDAO.getWarehouseById(id);
                    if (warehouse == null) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Warehouse not found");
                        return;
                    }
                    request.setAttribute("warehouse", warehouse);
                    request.getRequestDispatcher("/views/editWarehouse.jsp").forward(request, response);
                    break;
                }
                case "search": {
                    String searchTerm = request.getParameter("search");
                    List<Warehouse> warehouses;
                    if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                        warehouses = warehouseDAO.searchWarehouses(searchTerm.trim());
                    } else {
                        warehouses = warehouseDAO.getAllWarehouses();
                    }
                    int totalWarehouses = warehouseDAO.getTotalWarehouseCount();
                    
                    request.setAttribute("warehouses", warehouses);
                    request.setAttribute("totalWarehouses", totalWarehouses);
                    request.setAttribute("searchTerm", searchTerm);
                    request.getRequestDispatcher("/views/warehouses.jsp").forward(request, response);
                    break;
                }
                default: {
                    // Default: list warehouses with statistics
                    List<Warehouse> warehouses = warehouseDAO.getAllWarehouses();
                    int totalWarehouses = warehouseDAO.getTotalWarehouseCount();
                    
                    request.setAttribute("warehouses", warehouses);
                    request.setAttribute("totalWarehouses", totalWarehouses);
                    request.getRequestDispatcher("/views/warehouses.jsp").forward(request, response);
                    break;
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Database error: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid warehouse ID");
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
                    String name = request.getParameter("name");
                    String location = request.getParameter("location");
                    String description = request.getParameter("description");
                    
                    // Validate input
                    String errorMessage = validateWarehouseInput(name, true, 0);
                    if (errorMessage != null) {
                        request.setAttribute("errorMessage", errorMessage);
                        request.setAttribute("name", name);
                        request.setAttribute("location", location);
                        request.setAttribute("description", description);
                        request.getRequestDispatcher("/views/newWarehouse.jsp").forward(request, response);
                        return;
                    }
                    
                    Warehouse warehouse = new Warehouse();
                    warehouse.setName(name);
                    warehouse.setLocation(location);
                    warehouse.setDescription(description);
                    warehouseDAO.createWarehouse(warehouse);
                    
                    request.getSession().setAttribute("successMessage", "Kho hàng đã được tạo thành công!");
                    break;
                }
                case "update": {
                    int warehouseId = Integer.parseInt(request.getParameter("warehouseId"));
                    String name = request.getParameter("name");
                    String location = request.getParameter("location");
                    String description = request.getParameter("description");
                    
                    // Validate input
                    String errorMessage = validateWarehouseInput(name, false, warehouseId);
                    if (errorMessage != null) {
                        request.setAttribute("errorMessage", errorMessage);
                        Warehouse warehouse = new Warehouse();
                        warehouse.setWarehouseId(warehouseId);
                        warehouse.setName(name);
                        warehouse.setLocation(location);
                        warehouse.setDescription(description);
                        request.setAttribute("warehouse", warehouse);
                        request.getRequestDispatcher("/views/editWarehouse.jsp").forward(request, response);
                        return;
                    }
                    
                    Warehouse warehouse = new Warehouse();
                    warehouse.setWarehouseId(warehouseId);
                    warehouse.setName(name);
                    warehouse.setLocation(location);
                    warehouse.setDescription(description);
                    warehouseDAO.updateWarehouse(warehouse);
                    
                    request.getSession().setAttribute("successMessage", "Kho hàng đã được cập nhật thành công!");
                    break;
                }
                case "delete": {
                    int id = Integer.parseInt(request.getParameter("warehouseId"));
                    warehouseDAO.deleteWarehouse(id);
                    request.getSession().setAttribute("successMessage", "Kho hàng đã được xóa thành công!");
                    break;
                }
                default:
                    break;
            }
        } catch (SQLException e) {
            request.getSession().setAttribute("errorMessage", "Lỗi cơ sở dữ liệu: " + e.getMessage());
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID kho hàng không hợp lệ");
        }
        response.sendRedirect(request.getContextPath() + "/warehouses");
    }
    
    private String validateWarehouseInput(String name, boolean isCreate, int warehouseId) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return "Vui lòng nhập tên kho hàng";
        }
        
        if (name.trim().length() > 100) {
            return "Tên kho hàng không được vượt quá 100 ký tự";
        }
        
        // Check if warehouse name already exists
        boolean nameExists;
        if (isCreate) {
            nameExists = warehouseDAO.isWarehouseNameExists(name.trim());
        } else {
            nameExists = warehouseDAO.isWarehouseNameExistsExcludingId(name.trim(), warehouseId);
        }
        
        if (nameExists) {
            return "Tên kho hàng đã tồn tại trong hệ thống";
        }
        
        return null;
    }
}
