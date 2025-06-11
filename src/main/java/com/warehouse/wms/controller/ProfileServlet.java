package com.warehouse.wms.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import com.warehouse.wms.dao.UserDAO;
import com.warehouse.wms.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet(name = "ProfileServlet", value = "/profile")
@MultipartConfig
public class ProfileServlet extends HttpServlet {
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
        User user = (User) session.getAttribute("user");
        request.setAttribute("user", user);
        request.getRequestDispatcher("/views/profile.jsp").forward(request, response);
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
        String action = request.getParameter("action");

        if ("updateProfile".equals(action)) {
            currentUser.setStaffName(request.getParameter("staffName"));
            currentUser.setEmail(request.getParameter("email"));
            currentUser.setAddress(request.getParameter("address"));
            currentUser.setUsername(request.getParameter("username"));
            userDAO.updateUser(currentUser);
            // Xử lý ảnh đại diện nếu có
            Part avatarPart = request.getPart("avatar");
            if (avatarPart != null && avatarPart.getSize() > 0) {
                String fileName = Paths.get(avatarPart.getSubmittedFileName()).getFileName().toString();
                String contentType = avatarPart.getContentType();
                try (InputStream is = avatarPart.getInputStream()) {
                    byte[] data = is.readAllBytes();
                    userDAO.saveUserAvatar(currentUser.getUserId(), fileName, contentType, data);
                }
            }
            session.setAttribute("user", currentUser);
            request.setAttribute("message", "Cập nhật thông tin thành công.");
        }
        // Password change logic moved to ChangePasswordServlet
        request.setAttribute("user", currentUser);
        request.getRequestDispatcher("/views/profile.jsp").forward(request, response);
    }
}
