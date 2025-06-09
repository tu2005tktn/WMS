package com.warehouse.wms.controller;

import java.io.IOException;

import com.warehouse.wms.dao.UserDAO;
import com.warehouse.wms.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Hiển thị trang đăng nhập
        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Validate input
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }
        
        try {
            // Kiểm tra đăng nhập
            User user = userDAO.loginUser(username.trim(), password);
            
            if (user != null) {
                // Đăng nhập thành công
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("username", user.getUsername());
                session.setAttribute("staffName", user.getStaffName());
                session.setMaxInactiveInterval(30 * 60); // 30 phút
                
                // Redirect đến trang chủ hoặc dashboard
                String redirectURL = request.getContextPath() + "/dashboard";
                response.sendRedirect(redirectURL);
            } else {
                // Đăng nhập thất bại
                request.setAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng");
                request.setAttribute("username", username); // Giữ lại username
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi đăng nhập. Vui lòng thử lại");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }
}
