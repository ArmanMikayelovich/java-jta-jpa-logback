package com.energizeglobal.internship.filter;

import com.energizeglobal.internship.util.exception.UnAuthenticatedException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * first filter in filter chain
 */
@Slf4j
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.debug("checking username in authenticated in AuthenticationFilter.");

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        if (req.getSession(false) == null || req.getSession().getAttribute("username") == null) {
            throw new UnAuthenticatedException();
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.debug("Authentication filter initialized.");
    }

    @Override
    public void destroy() {
        log.debug("Authentication filter destroyed.");
    }
}
