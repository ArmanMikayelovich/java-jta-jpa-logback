package com.energizeglobal.internship.servlet;

import com.energizeglobal.internship.model.RegistrationRequest;
import com.energizeglobal.internship.service.UserService;
import com.energizeglobal.internship.service.UserServiceWithJTA;
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
import java.util.Map;
import java.util.Set;
@Slf4j
public class Register extends HttpServlet {
    private final UserService userService = UserService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath()+"/registration.jsp");
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

        final Map<String,String> complianceErrors =
                Validator.validate(registrationRequest);

        if (!complianceErrors.isEmpty() || userService.isUsernameExists(username)) {
            for (Map.Entry<String, String> entry : complianceErrors.entrySet()) {
                req.setAttribute(entry.getKey(), entry.getValue());
            }

            if (userService.isUsernameExists(username)) {
                req.setAttribute("username", "Username already exists");
            }

            req.getRequestDispatcher("/registration.jsp").forward(req, resp);
            return;
        }
        userService.register(registrationRequest);
        resp.sendRedirect(req.getContextPath()+"/login.jsp");
        log.debug("Successfully registered User : " + registrationRequest.toString());
    }
}
