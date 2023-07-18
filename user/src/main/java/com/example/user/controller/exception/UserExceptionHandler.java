package com.example.user.controller.exception;

import com.example.common.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.ws.rs.WebApplicationException;
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
        super(errorAttributes, CONSTRAINTS_MAP);
    }

    @ExceptionHandler({WebApplicationException.class})
    public ResponseEntity<?> conflict(WebRequest request, WebApplicationException ex) {
        if (ex.getResponse().getStatus() == HttpStatus.CONFLICT.value()) {
            return createResponseEntity(request, ErrorAttributeOptions.of(), "Duplicate email or username", HttpStatus.CONFLICT);
        }
        String rootMsg = getRootCause(ex).getMessage();
        log.error("Conflict at request " + request, getRootCause(ex));
        return createResponseEntity(request, ErrorAttributeOptions.of(STACK_TRACE), rootMsg, HttpStatus.CONFLICT);
    }
}