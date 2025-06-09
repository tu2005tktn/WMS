package com.warehouse.wms.controller;

import java.io.IOException;
import java.util.regex.Pattern;

import com.warehouse.wms.dao.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "RegisterServlet", value = "/register")
public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO;
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Hiển thị trang đăng ký
        request.getRequestDispatcher("/views/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String staffName = request.getParameter("staffName");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate input
        String errorMessage = validateInput(staffName, email, username, password, confirmPassword);
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            setFormData(request, staffName, email, address, username);
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            return;
        }
        
        try {
            // Kiểm tra username đã tồn tại
            if (userDAO.isUsernameExists(username.trim())) {
                request.setAttribute("errorMessage", "Tên đăng nhập đã tồn tại");
                setFormData(request, staffName, email, address, username);
                request.getRequestDispatcher("/views/register.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra email đã tồn tại
            if (userDAO.isEmailExists(email.trim())) {
                request.setAttribute("errorMessage", "Email đã được sử dụng");
                setFormData(request, staffName, email, address, username);
                request.getRequestDispatcher("/views/register.jsp").forward(request, response);
                return;
            }
            
            // Đăng ký người dùng mới
            boolean success = userDAO.registerUser(
                staffName.trim(), 
                email.trim(), 
                address != null ? address.trim() : "", 
                username.trim(), 
                password
            );
            
            if (success) {
                // Đăng ký thành công
                request.setAttribute("successMessage", 
                    "Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.");
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            } else {
                // Đăng ký thất bại
                request.setAttribute("errorMessage", "Có lỗi xảy ra khi đăng ký. Vui lòng thử lại");
                setFormData(request, staffName, email, address, username);
                request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi đăng ký. Vui lòng thử lại");
            setFormData(request, staffName, email, address, username);
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
        }
    }
    
    private String validateInput(String staffName, String email, String username, 
                               String password, String confirmPassword) {
        if (staffName == null || staffName.trim().isEmpty()) {
            return "Vui lòng nhập họ và tên";
        }
        
        if (email == null || email.trim().isEmpty()) {
            return "Vui lòng nhập email";
        }
        
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return "Email không hợp lệ";
        }
        
        if (username == null || username.trim().isEmpty()) {
            return "Vui lòng nhập tên đăng nhập";
        }
        
        if (username.trim().length() < 3) {
            return "Tên đăng nhập phải có ít nhất 3 ký tự";
        }
        
        if (password == null || password.isEmpty()) {
            return "Vui lòng nhập mật khẩu";
        }
        
        if (password.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự";
        }
        
        if (confirmPassword == null || !password.equals(confirmPassword)) {
            return "Xác nhận mật khẩu không khớp";
        }
        
        return null; // No errors
    }
    
    private void setFormData(HttpServletRequest request, String staffName, 
                           String email, String address, String username) {
        request.setAttribute("staffName", staffName);
        request.setAttribute("email", email);
        request.setAttribute("address", address);
        request.setAttribute("username", username);
    }
}
