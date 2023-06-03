package com.example.user.servise.kc;

import com.example.user.model.dto.UserDTO;
import com.example.user.util.KeycloakUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.Response;
import java.util.List;

import static com.example.common.util.ValidationUtil.assureIdConsistent;
import static com.example.common.util.ValidationUtil.checkNew;

@Service
@Slf4j
@AllArgsConstructor
@Profile("kc")
public class KeycloakUserService {

    private final KeycloakUtil keycloakUtil;

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
}