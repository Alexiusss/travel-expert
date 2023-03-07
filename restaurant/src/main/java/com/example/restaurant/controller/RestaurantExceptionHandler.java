package com.example.restaurant.controller;

import com.example.common.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class RestaurantExceptionHandler extends GlobalExceptionHandler {

    public static final String EXCEPTION_DUPLICATE_EMAIL = "Duplicate email";

    public static final Map<String, String> CONSTRAINTS_MAP = Map.of(
            "restaurant_email_unique", EXCEPTION_DUPLICATE_EMAIL
    );

    public RestaurantExceptionHandler(ErrorAttributes errorAttributes) {
        super(errorAttributes, CONSTRAINTS_MAP);
    }
}