package com.energizeglobal.internship.servlet;

import com.energizeglobal.internship.model.User;
import com.energizeglobal.internship.service.UserService;
import com.energizeglobal.internship.service.UserServiceWithJTA;
import com.energizeglobal.internship.util.Validator;
import com.energizeglobal.internship.util.exception.IllegalAccessException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;
@Slf4j
public class UserInfoChanger extends HttpServlet {
    private final UserService userService= UserService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath()+"/user/userPage.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String username = req.getParameter("username");
        log.debug("trying to change {} info",username);

        if (!userService.isAdmin(username)) {
            final String loggedInUsername = (String) req.getSession().getAttribute("username");
            if (!username.equals(loggedInUsername)) {
                throw new IllegalAccessException();
            }
        }

        LocalDate birthday;

        try {
            birthday = LocalDate.parse(req.getParameter("birthday"));
        } catch (DateTimeParseException ignored) {
            birthday = null;
        }

        final String email = req.getParameter("email");
        final String country = req.getParameter("country");
        final User userToUpdate = new User(username, birthday, email, country);

        final Map<String,String> complianceErrors =
                Validator.validate(userToUpdate);

        if (!complianceErrors.isEmpty()) {
            for (Map.Entry<String, String> entry : complianceErrors.entrySet()) {
                req.setAttribute(entry.getKey(), entry.getValue());
            }
            req.getRequestDispatcher("/user/changeUser.jsp").forward(req, resp);
            log.debug("cant change user {} info.",username);
            return;
        }
        userService.updateUserInfo(userToUpdate);
        log.debug("user {} info changed.",username);
        if (userService.isAdmin(username)) {
            resp.sendRedirect(req.getContextPath()+"/admin/adminPage.jsp");
        } else {
            resp.sendRedirect(req.getContextPath()+"/user/userPage.jsp");
        }
    }
}
