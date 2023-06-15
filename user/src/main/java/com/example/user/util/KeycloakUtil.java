package com.example.user.util;

import com.example.user.model.dto.UserDTO;
import org.keycloak.OAuth2Constants;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private static Keycloak keycloak;
    private static RealmResource realmResource;
    private static UsersResource usersResource;

    @PostConstruct
    public Keycloak intKeycloak() {
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

    public Response createKeycloakUser(UserDTO user) {
        return usersResource.create(createUserRepresentation(user));
    }

    public void addRoles(String userId, List<String> roles) {
        List<RoleRepresentation> kcRoles = new ArrayList<>();
        roles.forEach(role -> {
            RoleRepresentation roleRep = realmResource.roles().get(role).toRepresentation();
            kcRoles.add(roleRep);
        });
        UserResource uniqueUserResource = usersResource.get(userId);
        uniqueUserResource.roles().realmLevel().add(kcRoles);
    }

    public void updateKeycloakUser(UserDTO user, String userId) {
        UserResource userResource = usersResource.get(userId);
        userResource.update(createUserRepresentation(user));
    }

    public UserRepresentation findUserById(String userId) {
        return usersResource.get(userId).toRepresentation();
    }

    public List<UserRepresentation> searchKeycloakUsers(String text) {
        return usersResource.searchByAttributes(text);
    }
    public List<UserRepresentation> findAll() {
        return usersResource.list();
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
        kcUser.setEnabled(true);
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
