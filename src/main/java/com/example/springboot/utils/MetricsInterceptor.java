package com.example.springboot.utils;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

// curl -X GET http://<server>/actuator/prometheus

@Component
public class MetricsInterceptor implements HandlerInterceptor {

    private final MeterRegistry meterRegistry;

    public MetricsInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.nanoTime());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute("startTime");
        if (startTime == null || !(handler instanceof HandlerMethod handlerMethod)) {
            return;
        }

        long duration = System.nanoTime() - startTime;

        String method = request.getMethod();
        String uri = request.getRequestURI(); // Or use UriTemplate to collapse dynamic IDs
        int status = response.getStatus();

        Timer.builder("http.server.requests.custom")
                .description("Custom HTTP request timing")
                .tag("method", method)
                .tag("uri", uri)
                .tag("status", String.valueOf(status))
                .register(meterRegistry)
                .record(duration, TimeUnit.NANOSECONDS);
    }
}