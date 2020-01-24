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
import java.util.Set;
@Slf4j
public class UserInfoChanger extends HttpServlet {
    private final UserService userService= UserServiceWithJTA.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect("/user/userPage.jsp");
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

        final Set<ConstraintViolation<User>> constraintViolations = Validator.validate(userToUpdate);

        if (!constraintViolations.isEmpty()) {
            for (ConstraintViolation<User> violation : constraintViolations) {
                req.setAttribute(violation.getPropertyPath().toString(), violation.getMessage());
            }

            if (userService.isUsernameExists(username)) {
                req.setAttribute("username", "Username already exists");
            }
            req.getRequestDispatcher("/user/changeUser.jsp").forward(req, resp);
            log.debug("cant change user {} info.",username);
            return;
        }
        userService.updateUserInfo(userToUpdate);
        log.debug("user {} info changed.",username);
        if (userService.isAdmin(username)) {
            resp.sendRedirect("/admin/adminPage.jsp");
        } else {
            resp.sendRedirect("/user/userPage.jsp");
        }
    }
}
