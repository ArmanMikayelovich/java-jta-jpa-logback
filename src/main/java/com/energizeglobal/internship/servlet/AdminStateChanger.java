package com.energizeglobal.internship.servlet;

import com.energizeglobal.internship.service.UserService;
import com.energizeglobal.internship.service.UserServiceWithJTA;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * changing user role (admin or not), if admin has changed his state, he can't recover that.
 */
@Slf4j
public class AdminStateChanger extends HttpServlet {
    private final UserService userService = UserServiceWithJTA.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.debug("Accepted request to AdminStateChanger servlet : " + req.getParameterMap().toString() + "\n" + req.getParameterMap().toString());
        final String username = req.getParameter("username");
        final String isAdminString = req.getParameter("isAdmin");
        final boolean isAdmin = Boolean.parseBoolean(isAdminString);
        userService.changeAdminState(username, isAdmin);
        resp.sendRedirect("/admin/adminPage.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("redirected from AdminStateChanger servlet to /admin/adminPage.js");
        resp.sendRedirect("/admin/adminPage.jsp");
    }
}
