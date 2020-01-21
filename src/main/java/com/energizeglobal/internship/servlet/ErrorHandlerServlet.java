package com.energizeglobal.internship.servlet;

import com.energizeglobal.internship.util.exception.IllegalAccessException;
import com.energizeglobal.internship.util.exception.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * checking error type and redirect the user to appropriate page. <br/>
 * In <b>4XX</b> errors or client side exceptions redirect to page frontErrors.jsp.<br/>
 * In <b>5XX</b> errors redirect to 500error.jsp.<br/>
 * In <b>Validation</b> errors redirect to same page with validation error messages.
 */
@Slf4j
public class ErrorHandlerServlet extends HttpServlet {
    private static final int BAD_REQUEST = 400;
    private static final int UNAUTHENTICATED = 401;
    private static final int UNAUTHORIZED = 403;
    private static final int NOT_FOUND = 404;
    private static final int SERVER_ERROR = 500;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Class<? extends Throwable> exceptionClass =
                (Class<? extends Throwable>) req.getAttribute("javax.servlet.error.exception_type");
        if (exceptionClass != null) {

            if (exceptionClass.equals(InvalidCredentialsException.class)) {

                log.debug("Handled InvalidCredentialsException and redirected to /login.jsp?error=true");
                resp.setStatus(BAD_REQUEST);
                req.getRequestDispatcher("/frontErrors.jsp").forward(req, resp);
                log.debug(exceptionClass.toString() + " handled");

            } else if (exceptionClass.equals(IllegalAccessException.class)) {

                log.debug("Handled IllegalAccessException and redirected to /frontErrors.jsp");
                resp.setStatus(UNAUTHENTICATED);
                req.getRequestDispatcher("/frontErrors.jsp").forward(req, resp);
                log.debug(exceptionClass.toString() + " handled");

            } else if (exceptionClass.equals(ServerSideException.class)) {

                log.debug("Handled ServerSideException and redirected to /500error.jsp");
                resp.setStatus(SERVER_ERROR);
                req.getRequestDispatcher("/frontErrors.jsp").forward(req, resp);
                log.debug(exceptionClass.toString() + " handled");

            } else if (exceptionClass.equals(UsernameNotFountException.class)) {

                resp.setStatus(NOT_FOUND);
                log.debug("Handled UsernameNotFountException and redirected to /frontErrors.jsp");
                req.getRequestDispatcher("/frontErrors.jsp").forward(req, resp);
                log.debug(exceptionClass.toString() + " handled");

            } else if (exceptionClass.equals(UnAuthenticatedException.class)) {

                resp.setStatus(UNAUTHENTICATED);
                log.debug("Handled UnAuthenticatedException and redirected to /frontErrors.jsp");
                req.getRequestDispatcher("/frontErrors.jsp").forward(req, resp);
                log.debug(exceptionClass.toString() + " handled");

            } else if (exceptionClass.equals(UnAuthorizedException.class)) {

                resp.setStatus(UNAUTHORIZED);
                log.debug("Handled UnAuthorizedException and redirected to /frontErrors.jsp");
                req.getRequestDispatcher("/frontErrors.jsp").forward(req, resp);
                log.debug(exceptionClass.toString() + " handled");

            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
