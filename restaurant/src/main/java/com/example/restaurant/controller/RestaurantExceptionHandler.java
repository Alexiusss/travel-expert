package com.example.restaurant.controller;

import com.example.common.GlobalExceptionHandler;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestaurantExceptionHandler extends GlobalExceptionHandler {
    public RestaurantExceptionHandler(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }
}