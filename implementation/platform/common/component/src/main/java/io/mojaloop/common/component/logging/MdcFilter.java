package io.mojaloop.common.component.logging;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

public class MdcFilter implements Filter {

    @Override
    public void destroy() {
        // Cleanup logic, if needed
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {

        try {

            MDC.put("REQ_ID", String.valueOf(UUID.randomUUID().toString()));
            filterChain.doFilter(servletRequest, servletResponse);

        } finally {
            MDC.clear();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic, if needed
    }

}
