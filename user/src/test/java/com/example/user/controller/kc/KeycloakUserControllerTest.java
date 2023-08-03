package com.example.user.controller.kc;

import com.example.common.util.TestKcProfileResolver;
import com.example.user.controller.config.KeycloakTestConfig;
import com.example.user.model.dto.UserDTO;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.example.common.util.JsonUtil.asParsedJson;
import static com.example.user.util.kc.KcUserTestData.*;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// https://www.baeldung.com/spring-boot-keycloak-integration-testing
@SpringBootTest
@Testcontainers
@Transactional
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
                .param("size", "2")
                .param("page", "1")
                .header(HttpHeaders.AUTHORIZATION, getKeycloakToken(keycloakAdminClient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0]", equalTo(asParsedJson(MODER_DTO))));
    }

    @Test
    void getUserById() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + USER_ID)
                .header(HttpHeaders.AUTHORIZATION, getKeycloakToken(keycloakAdminClient)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_DTO_MATCHER.contentJson(USER_DTO));
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "1001")
                .header(HttpHeaders.AUTHORIZATION, getKeycloakToken(keycloakAdminClient)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + USER_ID))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    void addUser() throws Exception {
        UserDTO newUser = getNewUser();

        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUser, newUser.getPassword()))
                .header(HttpHeaders.AUTHORIZATION, getKeycloakToken(keycloakAdminClient)))
                .andExpect(status().isCreated())
                .andDo(print());

        UserDTO savedUser = USER_DTO_MATCHER.readFromJson(action);
        newUser.setId(savedUser.getId());

        USER_DTO_MATCHER.assertMatch(savedUser, newUser);
    }
}