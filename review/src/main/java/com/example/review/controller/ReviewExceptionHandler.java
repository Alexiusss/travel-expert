package com.example.review.controller;

import com.example.common.GlobalExceptionHandler;
import com.example.common.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import static com.example.common.util.ValidationUtil.getRootCause;
import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.STACK_TRACE;

@RestControllerAdvice
@Slf4j
public class ReviewExceptionHandler extends GlobalExceptionHandler {

    public static final String EXCEPTION_DUPLICATE_REVIEW = "Duplicate review";
    public static final String REVIEW_UNIQUE_CONSTRAINT = "review_user_unique";

    public ReviewExceptionHandler(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflict(WebRequest request, DataIntegrityViolationException ex) {
        String rootMsg = getRootCause(ex).getMessage();
        if (rootMsg.contains(REVIEW_UNIQUE_CONSTRAINT)) {
            log.warn("Conflict at request  {}: {}", request, rootMsg);
            return createResponseEntity(request, ErrorAttributeOptions.of(), EXCEPTION_DUPLICATE_REVIEW, HttpStatus.CONFLICT);
        }
        log.error("Conflict at request " + request, getRootCause(ex));
        return createResponseEntity(request, ErrorAttributeOptions.of(STACK_TRACE), rootMsg, HttpStatus.CONFLICT);
    }
}
