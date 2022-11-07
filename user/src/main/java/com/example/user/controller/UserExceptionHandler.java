package com.example.user.controller;

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
    public ResponseEntity<?> conflict(WebRequest request, DataIntegrityViolationException ex) {
        String rootMsg = getRootCause(ex).getMessage();
        for (Map.Entry<String, String> entry: CONSTRAINTS_MAP.entrySet()) {
            if (rootMsg.toLowerCase().contains(entry.getKey().toLowerCase())){
                log.warn("Conflict at request  {}: {}", request, rootMsg);
                return createResponseEntity(request, ErrorAttributeOptions.of(), entry.getValue(), HttpStatus.CONFLICT);
            }
        }
        log.error("Conflict at request " + request, getRootCause(ex));
        return createResponseEntity(request, ErrorAttributeOptions.of(STACK_TRACE), rootMsg, HttpStatus.CONFLICT);
    }
}