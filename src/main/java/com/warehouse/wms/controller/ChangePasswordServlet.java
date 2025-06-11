package com.warehouse.wms.controller;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import com.warehouse.wms.dao.UserDAO;
import com.warehouse.wms.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ChangePasswordServlet", value = "/change-password")
public class ChangePasswordServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        request.getRequestDispatcher("/views/changePassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User currentUser = (User) session.getAttribute("user");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (oldPassword == null || newPassword == null || confirmPassword == null) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin.");
        } else if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu mới và xác nhận không khớp.");
        } else if (!BCrypt.checkpw(oldPassword, currentUser.getPasswordHash())) {
            request.setAttribute("error", "Mật khẩu cũ không đúng.");
        } else {
            String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
            boolean updated = userDAO.updatePasswordById(currentUser.getUserId(), newHash);
            if (updated) {
                currentUser.setPasswordHash(newHash);
                session.setAttribute("user", currentUser);
                request.setAttribute("message", "Đổi mật khẩu thành công.");
            } else {
                request.setAttribute("error", "Đã có lỗi xảy ra, vui lòng thử lại.");
            }
        }
        request.getRequestDispatcher("/views/changePassword.jsp").forward(request, response);
    }
}
