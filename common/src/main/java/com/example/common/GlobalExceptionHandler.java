package com.example.common;

import com.example.common.error.AppException;
import com.example.common.error.NotFoundException;
import com.example.common.util.ValidationUtil;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final ErrorAttributes errorAttributes;

    public static final String EXCEPTION_DUPLICATE_EMAIL = "Duplicate email";

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> appException(WebRequest request, AppException ex) {
        return createResponseEntity(request, ex.getOptions(), null, ex.getStatus());
    }

    // https://stackoverflow.com/questions/2109476/how-to-handle-dataintegrityviolationexception-in-spring/42422568#42422568
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflict(WebRequest request) {
        return createResponseEntity(request, ErrorAttributeOptions.of(), EXCEPTION_DUPLICATE_EMAIL, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({EntityNotFoundException.class, BadCredentialsException.class, DisabledException.class})
    public ResponseEntity<?> handleException(WebRequest request) {
        return createResponseEntity(request, ErrorAttributeOptions.of(MESSAGE), null, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(WebRequest request, NotFoundException e) {
        return createResponseEntity(request, ErrorAttributeOptions.of(), e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> handleFeignStatusException(WebRequest request, FeignException ex) {
        return createResponseEntity(request, ErrorAttributeOptions.of(), null, HttpStatus.valueOf(ex.status()));
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception ex, Object body, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        super.handleExceptionInternal(ex, body, headers, status, request);
        return createResponseEntity(request, ErrorAttributeOptions.of(), ValidationUtil.getRootCause(ex).getMessage(), status);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        return handleBindingErrors(ex.getBindingResult(), request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        return handleBindingErrors(ex.getBindingResult(), request);
    }

    private ResponseEntity<Object> handleBindingErrors(BindingResult result, WebRequest request) {
        String msg = result.getFieldErrors().stream()
                .map(fe -> String.format("[%s] %s", fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.joining("\n"));
        return createResponseEntity(request, ErrorAttributeOptions.defaults(), msg, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @SuppressWarnings("unchecked")
    private <T> ResponseEntity<T> createResponseEntity(WebRequest request, ErrorAttributeOptions options, String msg, HttpStatus status) {
        Map<String, Object> body = errorAttributes.getErrorAttributes(request, options);
        if (msg != null) {
            body.put("message", msg);
        }
        body.put("status", status);
        body.put("error", status.getReasonPhrase());
        return (ResponseEntity<T>) ResponseEntity.status(status).body(body);
    }
}