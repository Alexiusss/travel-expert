package com.example.user.controller.kc;

import com.example.common.util.TestKcProfileResolver;
import com.example.user.controller.config.KeycloakTestConfig;
import com.example.user.model.dto.UserDTO;
import com.example.user.repository.kc.SubscriptionsRepository;
import com.example.user.util.KeycloakUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

import javax.ws.rs.NotFoundException;
import java.util.List;

import static com.example.common.util.JsonUtil.asParsedJson;
import static com.example.common.util.JsonUtil.writeValue;
import static com.example.user.util.kc.KcUserTestData.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private KeycloakUtil keycloakUtil;

    @Autowired
    private SubscriptionsRepository subscriptionsRepository;

    @Autowired
    private Keycloak keycloakAdminClient;

    private static WireMockServer wireMockServer;

    @Container
    static KeycloakContainer keycloakContainer = new KeycloakContainer("keycloak/keycloak:18.0")
            .withExposedPorts(8080)
            .withRealmImportFile("keycloak/realm-export.json");

    @DynamicPropertySource
    static void setUp(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> keycloakContainer.getAuthServerUrl() + "/realms/travel-expert-realm/protocol/openid-connect/certs");
        registry.add("keycloak.auth-server-url", () -> keycloakContainer.getAuthServerUrl());
    }

    @BeforeAll
    static void init() {
        wireMockServer = new WireMockServer(new WireMockConfiguration().port(7070));
        wireMockServer.start();
        WireMock.configureFor("localhost", 7070);
    }

    @AfterAll
    static void destroy() {
        wireMockServer.stop();
    }

    @Autowired
    MockMvc mockMvc;

    protected ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .param("size", "1")
                .param("page", "2")
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
        perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND_ID)
                .header(HttpHeaders.AUTHORIZATION, getKeycloakToken(keycloakAdminClient)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message", equalTo(NOT_FOUND_MESSAGE)));
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

    @Test
    void getAuthor() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + MODER_ID + "/author")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getKeycloakToken(keycloakAdminClient)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(AUTHOR_DTO_MATCHER.contentJson(MODER_AUTHOR))
                .andDo(print());
    }

    @Test
    void getAuthorByUsername() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + MODER_USERNAME + "/authorByUsername")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getKeycloakToken(keycloakAdminClient)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(AUTHOR_DTO_MATCHER.contentJson(MODER_AUTHOR))
                .andDo(print());
    }

    @Test
    void getAuthorList() throws Exception {
        stubReviewResponse();
        perform(MockMvcRequestBuilders.post(REST_URL + "/authorList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(List.of(MODER_ID, USER_ID)))
                .header(HttpHeaders.AUTHORIZATION, getKeycloakToken(keycloakAdminClient)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(AUTHOR_DTO_MATCHER.contentJson(List.of(MODER_AUTHOR, USER_AUTHOR)));
    }

    @Test
    void deleteUser() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DELETE_USER_ID)
                .header(HttpHeaders.AUTHORIZATION, getKeycloakToken(keycloakAdminClient)))
                .andExpect(status().isNoContent());

        assertThrows(NotFoundException.class, () -> keycloakUtil.findUserById(DELETE_USER_ID));
        Boolean isSubscribersDeleted = subscriptionsRepository.getAllSubscribersById(DELETE_USER_ID).size() == 0;
        Boolean isSubscriptionsDeleted = subscriptionsRepository.getAllSubscriptionsById(DELETE_USER_ID).size() == 0;
        assertTrue(isSubscribersDeleted && isSubscriptionsDeleted);
    }

    @Test
    void deleteForbidden() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + ADMIN_ID)
                .header(HttpHeaders.AUTHORIZATION, getKeycloakToken(keycloakAdminClient)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", equalTo(MODIFICATION_FORBIDDEN_MESSAGE)));
    }

    @Test
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + NOT_FOUND_ID)
                .header(HttpHeaders.AUTHORIZATION, getKeycloakToken(keycloakAdminClient)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message", equalTo(NOT_FOUND_MESSAGE)));
    }
}