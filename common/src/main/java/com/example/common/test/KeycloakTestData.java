package com.example.common.test;

import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.experimental.UtilityClass;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.RsaJsonWebKey;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@UtilityClass
public class KeycloakTestData {

    private static final String KEYCLOAK_BASE_URL = "http://localhost:7070";
    private static final String KEYCLOAK_REALM = "travel-expert-realm";
    private static final String OPENID_CONFIG = "{\n" +
            "  \"issuer\": \"" + KEYCLOAK_BASE_URL + "/auth/realms/" + KEYCLOAK_REALM + "\",\n" +
            "  \"authorization_endpoint\": \"" + KEYCLOAK_BASE_URL + "/auth/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/auth\",\n" +
            "  \"token_endpoint\": \"" + KEYCLOAK_BASE_URL + "/auth/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/token\",\n" +
            "  \"token_introspection_endpoint\": \"" + KEYCLOAK_BASE_URL + "/auth/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/token/introspect\",\n" +
            "  \"userinfo_endpoint\": \"" + KEYCLOAK_BASE_URL + "/auth/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/userinfo\",\n" +
            "  \"end_session_endpoint\": \"" + KEYCLOAK_BASE_URL + "/auth/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/logout\",\n" +
            "  \"jwks_uri\": \"" + KEYCLOAK_BASE_URL + "/auth/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/certs\",\n" +
            "  \"check_session_iframe\": \"" + KEYCLOAK_BASE_URL + "/auth/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/login-status-iframe.html\",\n" +
            "  \"registration_endpoint\": \"" + KEYCLOAK_BASE_URL + "/auth/realms/" + KEYCLOAK_REALM + "/clients-registrations/openid-connect\",\n" +
            "  \"introspection_endpoint\": \"" + KEYCLOAK_BASE_URL + "/auth/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/token/introspect\"\n" +
            "}";

    public static void stubKeycloak(RsaJsonWebKey rsaJsonWebKey) {
        stubFor(WireMock.get(urlEqualTo(String.format("/realms/%s/.well-known/openid-configuration", KEYCLOAK_REALM)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(OPENID_CONFIG)
                )
        );
        stubFor(WireMock.get(urlEqualTo(String.format("/realms/%s/protocol/openid-connect/certs", KEYCLOAK_REALM)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(new JsonWebKeySet(rsaJsonWebKey).toJson())
                )
        );
    }
}