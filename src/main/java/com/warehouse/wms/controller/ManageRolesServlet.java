package com.warehouse.wms.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.warehouse.wms.dao.RoleDAO;
import com.warehouse.wms.model.Role;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ManageRolesServlet", urlPatterns = {"/roles"})
public class ManageRolesServlet extends HttpServlet {
    private RoleDAO roleDAO;

    @Override
    public void init() throws ServletException {
        roleDAO = new RoleDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Role> roles = roleDAO.getAllRoles();
            request.setAttribute("roles", roles);
            request.getRequestDispatcher("/views/roles.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("create".equals(action)) {
                String name = request.getParameter("roleName");
                String description = request.getParameter("description");
                Role role = new Role();
                role.setRoleName(name);
                role.setDescription(description);
                roleDAO.createRole(role);
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("roleId"));
                roleDAO.deleteRole(id);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/roles");
    }
}
