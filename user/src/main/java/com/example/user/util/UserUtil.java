package com.example.user.util;

import com.example.clients.auth.AuthorDTO;
import com.example.clients.review.ReviewResponse;
import com.example.common.error.ModificationRestrictionException;
import com.example.user.model.User;
import com.example.user.model.dto.UserDTO;
import lombok.experimental.UtilityClass;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@UtilityClass
public class UserUtil {
    //https://docs.spring.io/spring-security/site/docs/current/reference/html5/#authentication-password-storage-dpe
    public static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

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

    public static AuthorDTO getAuthorDTO(UserRepresentation user) {
        String username = user.getUsername();
        String authorName = user.getFirstName() + " " + user.getLastName();
        String fileName = user.getAttributes().get("fileName") != null ? user.getAttributes().get("fileName").get(0) : "Empty";
        Instant registeredAt = Instant.ofEpochMilli(user.getCreatedTimestamp());
//        Set<String> subscribers = user.getSubscribers();
//        Set<String> subscriptions = user.getSubscriptions();
        return new AuthorDTO(user.getId(), authorName, username, fileName, registeredAt, Set.of(), Set.of(), 0L);
    }

    public static UserDTO convertUserRepresentationToUserDTO(UserRepresentation userRepresentation) {
        return UserDTO.builder()
                .id(userRepresentation.getId())
                .email(userRepresentation.getEmail())
                .firstName(userRepresentation.getFirstName())
                .lastName(userRepresentation.getLastName())
                .username(userRepresentation.getUsername())
                .enabled(userRepresentation.isEnabled())
                .fileName(userRepresentation.getAttributes().get("fileName").get(0))
                .roles(List.of("USER"))
                .build();
    }

    public static void addRoles(UserDTO user, Jwt jwt) {
        Map<String, List<String>> realmAccess;
        if ((realmAccess = (Map<String, List<String>>) jwt.getClaims().get("realm_access")) != null) {
            user.setRoles(realmAccess.get("roles"));
        }
    }

    public static Consumer<AuthorDTO> setReviewsCount(List<ReviewResponse> list) {
        return author ->
                list.stream()
                        .filter(item -> item.getId().equals(author.getAuthorId()))
                        .findAny()
                        .ifPresent(response -> author.setReviewsCount(response.getCount()));
    }
}