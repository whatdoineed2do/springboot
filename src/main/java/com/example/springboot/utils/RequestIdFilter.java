package com.example.springboot.utils;

import com.example.springboot.utils.annotations.PrefixedUuid;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class RequestIdFilter extends OncePerRequestFilter
{

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        String requestId = PrefixedUuid.generate("txn");
        MDC.put("txnId", requestId);

        try
        {
            log.debug("txn ID: {}", requestId);
            filterChain.doFilter(request, response);
        } finally
        {
            MDC.clear();
        }
    }
}
