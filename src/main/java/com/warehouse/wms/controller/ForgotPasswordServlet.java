package com.warehouse.wms.controller;

import java.io.IOException;
import java.util.UUID;

import com.warehouse.wms.dao.UserDAO;
import com.warehouse.wms.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ForgotPasswordServlet", value = "/forgot")
public class ForgotPasswordServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/forgot.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String errorMessage = null;

        if (email == null || email.trim().isEmpty()) {
            errorMessage = "Vui lòng nhập email";
        } else if (!ValidationUtil.isValidEmail(email.trim())) {
            errorMessage = "Email không hợp lệ";
        } else if (!userDAO.isEmailExists(email.trim())) {
            errorMessage = "Email không tồn tại";
        }

        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/forgot.jsp").forward(request, response);
            return;
        }

        // Generate random password
        String newPassword = UUID.randomUUID().toString().substring(0, 8);
        String hashed = ValidationUtil.hashPassword(newPassword);
        boolean updated = userDAO.updatePasswordByEmail(email.trim(), hashed);

        if (updated) {
            request.setAttribute("successMessage", 
                "Mật khẩu mới của bạn: " + newPassword + ". Vui lòng đăng nhập và thay đổi mật khẩu.");
        } else {
            request.setAttribute("errorMessage", "Có lỗi xảy ra. Vui lòng thử lại");
        }

        request.getRequestDispatcher("/views/forgot.jsp").forward(request, response);
    }
}
