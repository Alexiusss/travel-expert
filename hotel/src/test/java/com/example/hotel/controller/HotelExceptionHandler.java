package com.example.hotel.controller;

import com.example.common.GlobalExceptionHandler;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HotelExceptionHandler extends GlobalExceptionHandler {
    public HotelExceptionHandler(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }
}