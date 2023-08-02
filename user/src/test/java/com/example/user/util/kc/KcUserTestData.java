package com.example.user.util.kc;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.experimental.UtilityClass;
import org.keycloak.admin.client.Keycloak;

@UtilityClass
public class KcUserTestData {

    public static String getKeycloakToken(Keycloak keycloakAdminClient) {
            String access_token = keycloakAdminClient.tokenManager().getAccessToken().getToken();
            return "Bearer " + access_token;
    }

    public static void changeKeycloakPorts(KeycloakContainer keycloak) {
        int port = keycloak.getMappedPort(8080);
        String url = "http://localhost:" + port;
        System.setProperty("keycloak.auth-server-url", url);
        System.setProperty("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", url + "/realms/travel-expert-realm/protocol/openid-connect/certs");
    }
}