package com.example.springboot.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

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

    private String message;
    private List<String> errors;


    public ApiError(final HttpStatus status, final String message, final String error) {
        this(status, message, Arrays.asList(error));
    }
}
