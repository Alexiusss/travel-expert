package com.example.review.controller;

import com.example.common.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ReviewExceptionHandler extends GlobalExceptionHandler {

    public static final String EXCEPTION_DUPLICATE_REVIEW = "Duplicate review";

    public static final Map<String, String> CONSTRAINTS_MAP = Map.of(
            "review_user_unique", EXCEPTION_DUPLICATE_REVIEW
    );

    public ReviewExceptionHandler(ErrorAttributes errorAttributes) {
        super(errorAttributes, CONSTRAINTS_MAP);
    }
}
