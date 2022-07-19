package com.example.user.util;

import com.example.common.error.ModificationRestrictionException;
import com.example.user.model.User;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

@UtilityClass
public class UserUtil {
    //https://docs.spring.io/spring-security/site/docs/current/reference/html5/#authentication-password-storage-dpe
    public static final PasswordEncoder PASSWORD_ENCODER =  PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public static User prepareToSave(User user) {
        String password = user.getPassword();
        user.setPassword(StringUtils.hasText(password) ? PASSWORD_ENCODER.encode(password) : password);
        user.setEmail(user.getEmail().toLowerCase());
        return user;
    }
    public static void checkModificationAllowed(String id) {
        if (id.equals("1")) {
            throw new ModificationRestrictionException();
        }
    }
}