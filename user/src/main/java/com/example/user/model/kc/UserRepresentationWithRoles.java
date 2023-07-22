package com.example.user.model.kc;


import lombok.Getter;
import lombok.Setter;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@Getter
@Setter
public class UserRepresentationWithRoles extends UserRepresentation {
    private List<String> roles;
}