package com.energizeglobal.internship.servlet;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class Logout extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.debug("{} logout.", req.getSession().getAttribute("username"));
        req.getSession().invalidate();
        resp.sendRedirect("/");

    }
}
