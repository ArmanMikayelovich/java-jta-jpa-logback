package com.energizeglobal.internship.servlet;

import com.energizeglobal.internship.dao.UserDao;
import com.energizeglobal.internship.dao.UserDaoJDBCImpl;
import com.energizeglobal.internship.model.LoginRequest;
import com.energizeglobal.internship.util.exception.InvalidCredentialsException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class ChangePassword extends HttpServlet {
    private final UserDao userDao = UserDaoJDBCImpl.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String username = (String) req.getSession().getAttribute("username");
        final String password = req.getParameter("password");
        final String newPassword = req.getParameter("newPassword");
        log.debug("trying to change password of user {} to {}", username, newPassword);
        if (newPassword.length() < 8 || newPassword.length() > 25) {

            req.setAttribute("newPassword", "password should be between 8-25 characters:");
            req.getRequestDispatcher("/user/changePassword.jsp").forward(req, resp);
            return;
        }

        try {

            userDao.updatePassword(new LoginRequest(username, password), newPassword);
            resp.sendRedirect("/user/userPage.jsp");
            log.debug("changed password of user {} to {}", username, newPassword);
        } catch (InvalidCredentialsException ex) {
            req.setAttribute("password", "Wrong password, please try again");
            req.getRequestDispatcher("/user/changePassword.jsp").forward(req, resp);
        }

    }
}
