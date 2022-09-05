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

@RestControllerAdvice
public class UserExceptionHandler extends GlobalExceptionHandler {

    public static final String EXCEPTION_DUPLICATE_EMAIL = "Duplicate email";
    public static final String EMAIL_UNIQUE_CONSTRAINT = "user_email_unique";

    public UserExceptionHandler(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    // https://stackoverflow.com/questions/2109476/how-to-handle-dataintegrityviolationexception-in-spring/42422568#42422568
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflict(WebRequest request,DataIntegrityViolationException ex) {
        String rootMsg = ValidationUtil.getRootCause(ex).getMessage();
        String message = "";
        if (rootMsg.contains(EMAIL_UNIQUE_CONSTRAINT)) {
            message = EXCEPTION_DUPLICATE_EMAIL;
        }
        return createResponseEntity(request, ErrorAttributeOptions.of(), message, HttpStatus.CONFLICT);
    }
}