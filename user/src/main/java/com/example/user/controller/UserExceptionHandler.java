package com.example.user.controller;

import com.example.common.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class UserExceptionHandler extends GlobalExceptionHandler {

    public static final String EXCEPTION_DUPLICATE_EMAIL = "Duplicate email";
    public static final String EXCEPTION_DUPLICATE_USERNAME = "username has already been taken";

    public static final Map<String, String> CONSTRAINTS_MAP = Map.of(
            "user_email_unique", EXCEPTION_DUPLICATE_EMAIL,
            "username_unique", EXCEPTION_DUPLICATE_USERNAME
    );

    public UserExceptionHandler(ErrorAttributes errorAttributes) {
        super(errorAttributes, CONSTRAINTS_MAP);
    }
}