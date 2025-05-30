package com.example.springboot.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter
{

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        long startTime = System.currentTimeMillis();
        try
        {
            filterChain.doFilter(request, response);
        } finally
        {
            long   duration = System.currentTimeMillis() - startTime;

            String method   = request.getMethod();
            String uri      = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            int    status   = response.getStatus();
            String clientIp = request.getRemoteAddr();
            String svcid    = response.getHeader("X-svc-id");
            if (svcid == null)
                svcid = "n/a";

            log
                    .info("{}",
                            new JSONObject(Map
                                    .of("requestId", request.getAttribute("requestId"), "svcId", svcid, "timestamp",
                                            Instant.now().toString(), "level", "INFO", "method", method, "uri",
                                            uri != null ? uri : request.getRequestURI(), "status", status, "durationMs",
                                            duration, "clientIp", clientIp, "message", "Request completed")));
        }
    }
}
