package com.example.restaurant_advisor_react.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;
import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

public class ModificationRestrictionException extends AppException{
    public static final String EXCEPTION_MODIFICATION_RESTRICTION = "modification is forbidden";
    public ModificationRestrictionException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, EXCEPTION_MODIFICATION_RESTRICTION, ErrorAttributeOptions.of(MESSAGE));
    }
}