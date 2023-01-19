package com.example.hotel.controller;

import com.example.common.GlobalExceptionHandler;
import org.springframework.boot.web.servlet.error.ErrorAttributes;

public class HotelExceptionHandler extends GlobalExceptionHandler {
    public HotelExceptionHandler(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }
}