package com.example.user.util;

import com.example.clients.auth.AuthorDTO;
import com.example.common.error.ModificationRestrictionException;
import com.example.user.model.User;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Set;

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

    public static AuthorDTO getAuthorDTO(User user) {
        String username = user.getUsername();
        String authorName = user.getFirstName() + " " + user.getLastName();
        String fileName = user.getFileName() != null ? user.getFileName() : "Empty";
        Instant registeredAt = user.getCreatedAt();
        Set<String> subscribers = user.getSubscribers();
        Set<String> subscriptions = user.getSubscriptions();
        return new AuthorDTO(user.getId(), authorName, username, fileName, registeredAt, subscribers, subscriptions, 0L);
    }
}