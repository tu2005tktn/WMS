package com.warehouse.wms.controller;

import java.io.IOException;
import java.io.OutputStream;

import com.warehouse.wms.dao.UserDAO;
import com.warehouse.wms.model.User;
import com.warehouse.wms.model.UserImage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AvatarServlet", value = "/avatar")
public class AvatarServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userIdParam = request.getParameter("userId");
        int userId;
        if (userIdParam == null || userIdParam.isEmpty()) {
            // fallback to current logged-in user
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                User currentUser = (User) session.getAttribute("user");
                userId = currentUser.getUserId();
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } else {
            try {
                userId = Integer.parseInt(userIdParam);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        UserImage img = userDAO.getUserAvatar(userId);
        if (img == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType(img.getContentType());
        response.setContentLength(img.getData().length);
        try (OutputStream os = response.getOutputStream()) {
            os.write(img.getData());
            os.flush();
        }
    }
}
