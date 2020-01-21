package com.energizeglobal.internship.filter;

import com.energizeglobal.internship.dao.UserDao;
import com.energizeglobal.internship.dao.UserDaoJDBCImpl;
import com.energizeglobal.internship.util.exception.UnAuthorizedException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * second filter in filter chain
 */
@Slf4j
public class AdminFilter implements Filter {
    private final UserDao userDao =UserDaoJDBCImpl.getInstance();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("Admin filter initialized");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        final String username = (String) req.getSession().getAttribute("username");
        log.debug("checking username in Admin filter: {}",username);
        if (userDao.isAdmin(username)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        throw new UnAuthorizedException();
    }

    @Override
    public void destroy() {
        log.debug("Admin filter destroyed");
    }
}
