package com.example.springboot.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class SvcException extends RuntimeException {
    @Getter
    private long  id;
}
