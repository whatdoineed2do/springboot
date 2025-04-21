package com.example.springboot.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiError
{
    @JsonIgnore // this gets displayed a string
    private HttpStatus httpStatus;

    @JsonProperty(index=1)
    public int  getStatus()
    { return httpStatus.value(); }

    private String path;

    private String message;
    private List<String> errors;

    public ApiError(final WebRequest request, final HttpStatus status, final String message, final String error) {
        this( status,
                (request instanceof ServletWebRequest ? ((ServletWebRequest) request).getRequest().getRequestURI() : "<unknown>"),
                message, Arrays.asList(error));
    }

    public ApiError(final WebRequest request, final HttpStatus status, final String message, final List<String> errors) {
        this( status,
                (request instanceof ServletWebRequest ? ((ServletWebRequest) request).getRequest().getRequestURI() : "<unknown>"),
                message, errors);
    }

    public ApiError(final HttpServletRequest request, final HttpStatus status, final String message, final String error) {
        this(status, request.getRequestURI(), message, Arrays.asList(error));
    }

    public ApiError(final HttpServletRequest request, final HttpStatus status, final String message, final List<String> errors) {
        this(status, request.getRequestURI(), message, errors);
    }
}
