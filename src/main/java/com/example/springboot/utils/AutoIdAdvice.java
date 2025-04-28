package com.example.springboot.utils;

import com.example.springboot.utils.annotations.AutoGenerateId;
import com.example.springboot.utils.annotations.PrefixedUuid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

@ControllerAdvice
@Component
public class AutoIdAdvice extends RequestBodyAdviceAdapter {

    private final HttpServletRequest request;

    public AutoIdAdvice(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // Intercept all
    }

    public Object afterBodyRead(Object body, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        if (body != null && request.getMethod().equalsIgnoreCase(HttpMethod.POST.name())) {
            AutoGenerateId annotation = body.getClass().getAnnotation(AutoGenerateId.class);
            if (annotation != null) {
                try {
                    var idField = body.getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    if (idField.get(body) == null || ((String) idField.get(body)).isEmpty()) {
                        idField.set(body, PrefixedUuid.generate(annotation.prefix()));
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to set ID", e);
                }
            }
        }
        return body;
    }
}