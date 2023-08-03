package com.example.user.controller.kc;

import com.example.common.util.TestKcProfileResolver;
import com.example.user.controller.config.KeycloakTestConfig;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.example.user.util.kc.KcUserTestData.changeKeycloakPorts;
import static com.example.user.util.kc.KcUserTestData.getKeycloakToken;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// https://www.baeldung.com/spring-boot-keycloak-integration-testing
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles(resolver = TestKcProfileResolver.class)
@Import(KeycloakTestConfig.class)
@Sql(value = {"/keycloak/test-data.sql"})
public class KeycloakUserControllerTest {

    private static final String REST_URL = KeycloakUserController.REST_URL + "/";

    @Autowired
    private Keycloak keycloakAdminClient;

    @Container
    static KeycloakContainer keycloakContainer = new KeycloakContainer("keycloak/keycloak:18.0")
            .withExposedPorts(8080)
            .withRealmImportFile("keycloak/realm-export.json");

    @DynamicPropertySource
    static void setUp(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> keycloakContainer.getAuthServerUrl() + "/realms/travel-expert-realm/protocol/openid-connect/certs");
        registry.add("keycloak.auth-server-url", () -> keycloakContainer.getAuthServerUrl());
    }

    @Autowired
    MockMvc mockMvc;

    protected ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .header("Authorization", getKeycloakToken(keycloakAdminClient)))
                .andExpect(status().isOk())
                .andDo(print());
    }
}