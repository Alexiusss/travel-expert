package com.example.user.util;

import com.example.clients.auth.AuthorDTO;
import com.example.clients.review.ReviewResponse;
import com.example.common.error.ModificationRestrictionException;
import com.example.user.AuthUser;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.model.dto.UserDTO;
import com.example.user.model.kc.UserRepresentationWithRoles;
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
import java.util.stream.Collectors;

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

    public static void checkModificationAllowed(String userId, String idFromToken) {
        if (!userId.equals(idFromToken)) {
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
        return new AuthorDTO(user.getId(), authorName, username, fileName, registeredAt, Set.of(), Set.of(), 0L);
    }

    public static UserDTO getUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .enabled(user.isEnabled())
                .fileName(user.getFileName())
                .roles(convertRoles(user.getRoles()))
                .build();
    }

    public static User convertDtoToUser(UserDTO userDTO) {
        User user = User.builder()
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .username(userDTO.getUsername())
                .enabled(userDTO.isEnabled())
                .fileName(userDTO.getFileName())
                .roles(convertRoles(userDTO.getRoles()))
                .build();
        user.setId(userDTO.getId());
        return user;
    }

    private List<String> convertRoles(Set<Role> roles) {
        return roles.stream()
                .map(Role::getAuthority)
                .collect(Collectors.toList());
    }

    public Set<Role> convertRoles(List<String> roles) {
        return roles.stream()
                .map(role -> Role.valueOf(Role.class, role))
                .collect(Collectors.toSet());
    }

    public static UserDTO getUserDTO(UserRepresentationWithRoles userRepresentation) {
        String fileName;
        if (userRepresentation.getAttributes() != null && userRepresentation.getAttributes().containsKey("fileName")) {
            fileName = userRepresentation.getAttributes().get("fileName").get(0);
        } else {
            fileName = "Empty filename";
        }
        return UserDTO.builder()
                .id(userRepresentation.getId())
                .email(userRepresentation.getEmail())
                .firstName(userRepresentation.getFirstName())
                .lastName(userRepresentation.getLastName())
                .username(userRepresentation.getUsername())
                .enabled(userRepresentation.isEnabled())
                .fileName(fileName)
                .roles(userRepresentation.getRoles())
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

    public static String getAuthUserIdFromPrincipal(Object principal) {
        if (principal instanceof AuthUser) {
            return ((AuthUser) principal).id();
        } else if (principal instanceof Jwt) {
            return ((Jwt) principal).getSubject();
        }
        throw new IllegalArgumentException("Unknown authentication principal type");
    }
}