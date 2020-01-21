package com.energizeglobal.internship.servlet;

import com.energizeglobal.internship.dao.UserDao;
import com.energizeglobal.internship.dao.UserDaoJDBCImpl;
import com.energizeglobal.internship.model.RegistrationRequest;
import com.energizeglobal.internship.util.Validator;
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
public class Register extends HttpServlet {
    private final UserDao userDao = UserDaoJDBCImpl.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect("/registration.jsp");
    }

    /**
     * The method checks user's info in validator, and if ALL compliance with standard, saves user to DB.
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String username = req.getParameter("username");
        final String password = req.getParameter("password");
        LocalDate birthday;

        try {
            birthday = LocalDate.parse(req.getParameter("birthday"));
        } catch (DateTimeParseException ignored) {
            birthday = null;
        }

        final String email = req.getParameter("email");
        final String country = req.getParameter("country");

        final RegistrationRequest registrationRequest =
                new RegistrationRequest(username, password, birthday, email, country);

        final Set<ConstraintViolation<RegistrationRequest>> constraintViolations =
                Validator.validate(registrationRequest);

        if (!constraintViolations.isEmpty()) {
            for (ConstraintViolation<RegistrationRequest> violation : constraintViolations) {
                req.setAttribute(violation.getPropertyPath().toString(), violation.getMessage());
            }

            if (userDao.isUsernameExists(username)) {
                req.setAttribute("username", "Username already exists");
            }

            req.getRequestDispatcher("/registration.jsp").forward(req, resp);
            return;
        }
        userDao.register(registrationRequest);
        resp.sendRedirect("/login.jsp");
        log.debug("Successfully registered User : " + registrationRequest.toString());
    }
}
