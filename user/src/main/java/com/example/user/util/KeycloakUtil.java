package com.example.user.util;

import com.example.user.model.dto.UserDTO;
import com.example.user.model.kc.UserRepresentationWithRoles;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Profile("kc")
public class KeycloakUtil {

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private Keycloak keycloak;
    private RealmResource realmResource;
    private UsersResource usersResource;

    @PostConstruct
    public Keycloak initKeycloak() {
        if (keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                    .realm(realm)
                    .serverUrl(serverUrl)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .build();
            realmResource = keycloak.realm(realm);
            usersResource = realmResource.users();
        }
        return keycloak;
    }

    public UserDTO createKeycloakUser(UserDTO user, List<String> roles) {
        Response response = usersResource.create(createUserRepresentation(user));
        String userId = CreatedResponseUtil.getCreatedId(response);
        UserResource userResource = usersResource.get(userId);
        user.setId(userId);
        addRoleRepresentations(userResource, getRoleRepresentations(roles));
        return user;
    }

    public void updateKeycloakUser(UserDTO user, List<String> roles, String userId) {
        UserResource userResource = usersResource.get(userId);
        if (roles != null && !roles.isEmpty()) {
            updateRoleRepresentations(userResource, getRoleRepresentations(roles));
        }
        userResource.update(createUserRepresentation(user));
    }

    public void addRoleRepresentations(UserResource userResource, List<RoleRepresentation> kcRoles) {
        userResource.roles().realmLevel().add(kcRoles);
    }

    public void updateRoleRepresentations(UserResource userResource, List<RoleRepresentation> kcRoles) {
        List<RoleRepresentation> userRoles = userResource.roles().realmLevel().listEffective();

        List<RoleRepresentation> rolesToAdd = kcRoles.stream()
                .filter(kcRole -> !userRoles.contains(kcRole))
                .collect(Collectors.toList());

        List<RoleRepresentation> rolesToRemove = userRoles.stream()
                .filter(userRole -> !kcRoles.contains(userRole))
                .collect(Collectors.toList());

        if (!rolesToRemove.isEmpty()) {
            userResource.roles().realmLevel().remove(rolesToRemove);
        }

        if (!rolesToAdd.isEmpty()) {
            addRoleRepresentations(userResource, rolesToAdd);
        }
    }

    private List<RoleRepresentation> getRoleRepresentations(List<String> roles) {
        return roles.stream()
                .map(role -> realmResource.roles().get(role).toRepresentation())
                .collect(Collectors.toList());
    }

    public void enableUser(String userId, boolean enable) {
        UserResource userResource = usersResource.get(userId);
        UserRepresentation updatedUser = userResource.toRepresentation();
        updatedUser.setEnabled(enable);
        userResource.update(updatedUser);
    }

    public UserRepresentationWithRoles findUserById(String userId) {
        return createUserRepresentationWithRoles(usersResource.get(userId).toRepresentation());
    }

    public UserRepresentation findByUserName(String username) {
        return usersResource.search(username).get(0);
    }

    public List<UserRepresentationWithRoles> searchKeycloakUsers(String text) {
        return usersResource.searchByAttributes(text).stream()
                .map(this::createUserRepresentationWithRoles)
                .collect(Collectors.toList());
    }

    public List<UserRepresentationWithRoles> findAll() {
        return usersResource.list().stream()
                .map(this::createUserRepresentationWithRoles)
                .collect(Collectors.toList());
    }

    public List<UserRepresentationWithRoles> findAll(int page, int size) {
        return usersResource.list(page, size).stream()
                .map(this::createUserRepresentationWithRoles)
                .collect(Collectors.toList());
    }

    public int getTotalCountOfUsers() {
        return usersResource.count();
    }

    private UserRepresentationWithRoles createUserRepresentationWithRoles(UserRepresentation user) {
        UserRepresentationWithRoles userWithRoles = new UserRepresentationWithRoles();
        userWithRoles.setId(user.getId());
        userWithRoles.setCreatedTimestamp(user.getCreatedTimestamp());
        userWithRoles.setUsername(user.getUsername());
        userWithRoles.setEmail(user.getEmail());
        userWithRoles.setFirstName(user.getFirstName());
        userWithRoles.setLastName(user.getLastName());
        userWithRoles.setEnabled(user.isEnabled());
        userWithRoles.setAttributes(user.getAttributes());

        List<RoleRepresentation> roles = realmResource.users().get(user.getId()).roles().realmLevel().listEffective();
        userWithRoles.setRoles(roles.stream().map(RoleRepresentation::getName).collect(Collectors.toList()));
        return userWithRoles;
    }

    public void deleteKeycloakUser(String userId) {
        usersResource.get(userId).remove();
    }

    private UserRepresentation createUserRepresentation(UserDTO user) {
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getUsername());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setEmail(user.getEmail());
        kcUser.setFirstName(user.getFirstName());
        kcUser.setLastName(user.getLastName());
        kcUser.setEnabled(user.isEnabled());
        kcUser.setAttributes(Map.of("fileName", List.of(user.getFileName())));
        return kcUser;
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }
}
