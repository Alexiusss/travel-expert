package com.example.restaurant_advisor_react.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@UtilityClass
public class UserUtil {
    //https://docs.spring.io/spring-security/site/docs/current/reference/html5/#authentication-password-storage-dpe
    public static final PasswordEncoder PASSWORD_ENCODER =  PasswordEncoderFactories.createDelegatingPasswordEncoder();
}