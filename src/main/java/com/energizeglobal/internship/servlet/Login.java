package com.energizeglobal.internship.servlet;

import com.energizeglobal.internship.dao.UserDao;
import com.energizeglobal.internship.dao.UserDaoJDBCImpl;
import com.energizeglobal.internship.model.LoginRequest;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class Login extends HttpServlet {
    private final UserDao userDao = UserDaoJDBCImpl.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String username = req.getParameter("username");
        final String password = req.getParameter("password");
        final LoginRequest loginRequest = new LoginRequest(username, password);

        log.debug("Requested login with username " + username + "and password " + password);
        userDao.login(loginRequest);
        final HttpSession session = req.getSession(true);
        session.setAttribute("username", username);
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
        log.debug("Successfully logged in with username " + username + "and password " + password);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect("/login.jsp");
    }
}
