package com.example.user.controller.kc;

import com.example.common.util.TestKcProfileResolver;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.example.user.util.kc.KcUserTesData.getBearerToken;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// https://www.baeldung.com/spring-boot-keycloak-integration-testing
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles(resolver = TestKcProfileResolver.class)
public class KeycloakUserControllerTest {

    private static final String REST_URL = KeycloakUserController.REST_URL + "/";

    @Container
    static KeycloakContainer keycloak = new KeycloakContainer("keycloak/keycloak:18.0")
            .withExposedPorts(8080)
            .withRealmImportFile("keycloak/realm-export.json");

    @BeforeAll
    static void setUp() {
        keycloak.start();
        changeKeycloakPorts();
    }

    @Autowired
    MockMvc mockMvc;

    protected ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private static void changeKeycloakPorts() {
        int port = keycloak.getMappedPort(8080);
        String url = "http://localhost:" + port;
        System.setProperty("keycloak.auth-server-url", url + "/auth");
        System.setProperty("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", url + "/realms/travel-expert-realm/protocol/openid-connect/certs");
    }
}