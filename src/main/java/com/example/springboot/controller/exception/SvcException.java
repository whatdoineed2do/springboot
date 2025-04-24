package com.example.springboot.controller.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class SvcException extends RuntimeException {
    private long  id;
}
