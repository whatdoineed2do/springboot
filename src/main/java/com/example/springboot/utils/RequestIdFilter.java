package com.example.springboot.utils;

import com.example.springboot.utils.annotations.PrefixedUuid;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class RequestIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {
        String requestId = PrefixedUuid.generate("req");
        MDC.put("requestId", requestId);

        try {
            log.debug("request ID: {}", requestId);
            request.setAttribute("requestId", requestId);

            // You can access the same UUID anywhere during this request
            processRequest(request);

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private void processRequest(HttpServletRequest request) {
        String currentRequestId = MDC.get("requestId");
        log.debug("processRequest - requestId: {}", currentRequestId);
    }
}
