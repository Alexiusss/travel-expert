package com.example.user.controller;

import com.example.common.GlobalExceptionHandler;
import com.example.common.util.ValidationUtil;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@RestControllerAdvice
public class UserExceptionHandler extends GlobalExceptionHandler {

    public static final String EXCEPTION_DUPLICATE_EMAIL = "Duplicate email";
    public static final String EXCEPTION_DUPLICATE_USERNAME = "username has already been taken";

    public static final Map<String, String> CONSTRAINTS_MAP = Map.of(
            "user_email_unique", EXCEPTION_DUPLICATE_EMAIL,
            "username_unique", EXCEPTION_DUPLICATE_USERNAME
    );

    public UserExceptionHandler(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    // https://stackoverflow.com/questions/2109476/how-to-handle-dataintegrityviolationexception-in-spring/42422568#42422568
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflict(WebRequest request,DataIntegrityViolationException ex) {
        String rootMsg = ValidationUtil.getRootCause(ex).getMessage();
        String message = "";
        for (Map.Entry<String, String> entry: CONSTRAINTS_MAP.entrySet()) {
            if (rootMsg.toLowerCase().contains(entry.getKey().toLowerCase())){
                message = entry.getValue();
            }
        }
        return createResponseEntity(request, ErrorAttributeOptions.of(), message, HttpStatus.CONFLICT);
    }
}