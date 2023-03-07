package com.example.hotel.controller;

import com.example.common.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static com.example.common.util.ValidationUtil.getRootCause;
import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.STACK_TRACE;

@RestControllerAdvice
@Slf4j
public class HotelExceptionHandler extends GlobalExceptionHandler {

    public static final String EXCEPTION_DUPLICATE_EMAIL = "Duplicate email";

    public static final Map<String, String> CONSTRAINTS_MAP = Map.of(
            "hotel_email_unique", EXCEPTION_DUPLICATE_EMAIL
    );

    public HotelExceptionHandler(ErrorAttributes errorAttributes) {
        super(errorAttributes, CONSTRAINTS_MAP);
    }
}