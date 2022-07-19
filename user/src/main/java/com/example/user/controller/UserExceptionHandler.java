package com.example.user.controller;

import com.example.common.GlobalExceptionHandler;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler extends GlobalExceptionHandler {
    public UserExceptionHandler(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }
}