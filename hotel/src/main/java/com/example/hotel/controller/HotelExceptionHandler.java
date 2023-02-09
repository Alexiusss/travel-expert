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

import static com.example.common.util.ValidationUtil.getRootCause;
import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.STACK_TRACE;

@RestControllerAdvice
@Slf4j
public class HotelExceptionHandler extends GlobalExceptionHandler {

    public static final String EXCEPTION_DUPLICATE_EMAIL = "Duplicate email";

    public HotelExceptionHandler(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflict(WebRequest request, DataIntegrityViolationException ex) {
        String rootMsg = getRootCause(ex).getMessage();
            if (rootMsg.toLowerCase().contains("hotel_email_unique")){
                log.warn("Conflict at request  {}: {}", request, rootMsg);
                return createResponseEntity(request, ErrorAttributeOptions.of(), EXCEPTION_DUPLICATE_EMAIL, HttpStatus.CONFLICT);
            }
        log.error("Conflict at request " + request, getRootCause(ex));
        return createResponseEntity(request, ErrorAttributeOptions.of(STACK_TRACE), rootMsg, HttpStatus.CONFLICT);
    }
}