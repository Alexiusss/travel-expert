package com.example.review.controller;

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
public class ReviewExceptionHandler extends GlobalExceptionHandler {

    public static final String EXCEPTION_DUPLICATE_REVIEW = "Duplicate review";
    public static final String REVIEW_UNIQUE_CONSTRAINT = "review_user_unique";

    public ReviewExceptionHandler(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflict(WebRequest request, DataIntegrityViolationException ex) {
        String rootMsg = ValidationUtil.getRootCause(ex).getMessage();
        String message = "";
        if (rootMsg.contains(REVIEW_UNIQUE_CONSTRAINT)) {
            message = EXCEPTION_DUPLICATE_REVIEW;
        }
        return createResponseEntity(request, ErrorAttributeOptions.of(), message, HttpStatus.CONFLICT);
    }
}
