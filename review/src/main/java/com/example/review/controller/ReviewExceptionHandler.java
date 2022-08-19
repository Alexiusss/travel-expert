package com.example.review.controller;

import com.example.common.GlobalExceptionHandler;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReviewExceptionHandler extends GlobalExceptionHandler {
    public ReviewExceptionHandler(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }
}
