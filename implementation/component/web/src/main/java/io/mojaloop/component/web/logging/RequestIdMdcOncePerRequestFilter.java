package io.mojaloop.component.web.logging;

import io.mojaloop.component.misc.handy.Snowflake;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdMdcOncePerRequestFilter extends OncePerRequestFilter {

    private static final String REQ_ID_KEY = "REQ_ID";

    private static final String HEADER_REQUEST_ID = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String existing = request.getHeader(HEADER_REQUEST_ID);
        String requestId = String.valueOf(existing != null ? existing : Snowflake.get().nextId());

        MDC.put(REQ_ID_KEY, requestId);
        response.setHeader(HEADER_REQUEST_ID, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(REQ_ID_KEY);
        }
    }

}
