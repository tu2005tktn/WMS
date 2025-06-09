package com.warehouse.wms.controller;

import java.io.IOException;

import com.warehouse.wms.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "DashboardServlet", value = {"/dashboard", "/home"})
public class DashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            // Chưa đăng nhập, redirect về trang login
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy thông tin user từ session
        User user = (User) session.getAttribute("user");
        request.setAttribute("user", user);
        
        // Forward đến trang dashboard
        request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
