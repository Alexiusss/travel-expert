package com.example.user.servise.kc;

import com.example.clients.auth.AuthorDTO;
import com.example.clients.review.ReviewClient;
import com.example.clients.review.ReviewResponse;
import com.example.user.model.dto.UserDTO;
import com.example.user.util.KeycloakUtil;
import com.example.user.util.UserUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.common.util.ValidationUtil.assureIdConsistent;
import static com.example.common.util.ValidationUtil.checkNew;
import static com.example.user.util.UserUtil.*;

@Service
@Slf4j
@AllArgsConstructor
@Profile("kc")
public class KeycloakUserService {

    private final KeycloakUtil keycloakUtil;
    private final ReviewClient reviewClient;

    @Transactional
    public UserDTO saveUser(UserDTO user, List<String> roles) {
        checkNew(user);
        Response response = keycloakUtil.createKeycloakUser(user);
        String userId = CreatedResponseUtil.getCreatedId(response);
        user.setId(userId);
        keycloakUtil.addRoles(userId, roles);
        return user;
    }

    @Transactional
    public UserDTO updateUser(UserDTO user, String id) {
        assureIdConsistent(user, user.id());
        keycloakUtil.updateKeycloakUser(user, id);
        return user;
    }

    public void deleteUser(String id) {
        keycloakUtil.deleteKeycloakUser(id);
    }

    public UserDTO get(String id) {
        UserRepresentation userRepresentation = keycloakUtil.findUserById(id);
        return convertUserRepresentationToUserDTO(userRepresentation);
    }

    public List<UserDTO> getAll(String filter) {
        List<UserRepresentation> userRepresentations;
        if (filter.length() > 0) {
            userRepresentations = keycloakUtil.searchKeycloakUsers(filter);
        } else {
            userRepresentations = keycloakUtil.findAll();
        }
        return userRepresentations.stream()
                .map(UserUtil::convertUserRepresentationToUserDTO)
                .collect(Collectors.toList());
    }

    public AuthorDTO getAuthorById(String id) {
        return getAuthorDTO(keycloakUtil.findUserById(id));
    }

    public AuthorDTO getAuthorByUserName(String username) {
        return getAuthorDTO(keycloakUtil.findByUserName(username));
    }

    public List<AuthorDTO> getAllAuthorsById(String[] authors) {
        List<String> authorsList = List.of(authors);
        List<ReviewResponse> list = reviewClient.getActiveList(authors);

        return keycloakUtil.findAll()
                .stream().filter(userRepresentation -> authorsList.contains(userRepresentation.getId()))
                .map(UserUtil::getAuthorDTO)
                .peek(setReviewsCount(list))
                .collect(Collectors.toList());
    }
}