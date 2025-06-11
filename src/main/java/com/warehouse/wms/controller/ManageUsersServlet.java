package com.warehouse.wms.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.warehouse.wms.dao.RoleDAO;
import com.warehouse.wms.dao.UserDAO;
import com.warehouse.wms.model.Role;
import com.warehouse.wms.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ManageUsersServlet", value = "/users")
public class ManageUsersServlet extends HttpServlet {
    private UserDAO userDAO;
    private RoleDAO roleDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        roleDAO = new RoleDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("editRoles".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                User user = userDAO.getUserById(userId);
                List<Role> userRoles = userDAO.getRolesByUserId(userId);
                List<Role> allRoles = roleDAO.getAllRoles();
                request.setAttribute("user", user);
                request.setAttribute("userRoles", userRoles);
                request.setAttribute("allRoles", allRoles);
                request.getRequestDispatcher("/views/editUserRoles.jsp").forward(request, response);
            } else {
                // Default: list users with roles
                List<User> users = userDAO.getAllUsers();
                Map<Integer, List<Role>> userRolesMap = new HashMap<>();
                for (User u : users) {
                    userRolesMap.put(u.getUserId(), userDAO.getRolesByUserId(u.getUserId()));
                }
                List<Role> allRoles = roleDAO.getAllRoles();
                request.setAttribute("users", users);
                request.setAttribute("userRolesMap", userRolesMap);
                request.setAttribute("allRoles", allRoles);
                request.getRequestDispatcher("/views/users.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("deactivate".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                userDAO.deactivateUser(userId);
            } else if ("updateRoles".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                String[] roleIds = request.getParameterValues("roleId");
                List<Integer> selectedRoles = new ArrayList<>();
                if (roleIds != null) {
                    for (String r : roleIds) {
                        selectedRoles.add(Integer.parseInt(r));
                    }
                }
                userDAO.updateUserRoles(userId, selectedRoles);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/users");
    }
}
