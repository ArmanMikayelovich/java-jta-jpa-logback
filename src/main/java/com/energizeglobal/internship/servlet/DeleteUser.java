package com.energizeglobal.internship.servlet;

import com.energizeglobal.internship.service.UserService;
import com.energizeglobal.internship.service.UserServiceWithJTA;
import com.energizeglobal.internship.util.exception.IllegalAccessException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class DeleteUser extends HttpServlet {
    private final UserService userService = UserService.getInstance();;


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String loggedInUsername = (String) req.getSession().getAttribute("username");
        final String deletingUsername = req.getParameter("username");
        log.debug("Trying to delete user {}", deletingUsername);
        if (loggedInUsername.equalsIgnoreCase(deletingUsername)) {
            userService.remove(deletingUsername);
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath()+"/");
            log.debug(" user deleted {}", deletingUsername);
        } else if (userService.isAdmin(loggedInUsername)) {
            userService.remove(deletingUsername);
            resp.sendRedirect(req.getContextPath()+"/admin/adminPage.jsp");
        } else {
            log.debug(" cant delete user {}", deletingUsername);
            throw new IllegalAccessException();
        }
    }
}
