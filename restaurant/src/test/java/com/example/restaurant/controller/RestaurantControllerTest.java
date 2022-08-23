package com.example.restaurant.controller;

import com.example.common.util.JsonUtil;
import com.example.restaurant.model.Restaurant;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.example.common.util.JsonUtil.asParsedJson;
import static com.example.common.util.JsonUtil.writeValue;
import static com.example.restaurant.util.RestaurantTestData.*;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = {"/data.sql"})
class RestaurantControllerTest {

    private static final String REST_URL = RestaurantController.REST_URL + "/";

    @Autowired
    private MockMvc mockMvc;

    private ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
    }

    @BeforeAll
    static void init() {
        WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().port(7070));
        wireMockServer.start();
        WireMock.configureFor("localhost", 7070);
    }

    @Test
    public void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + RESTAURANT1_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(RESTAURANT_MATCHER.contentJson(RESTAURANT1));
    }

    @Test
    public void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND_ID))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", equalTo(NOT_FOUND_MESSAGE)));
    }

    @Test
    public void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .param("size", "2")
                .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0]", equalTo(asParsedJson(RESTAURANT3))));
    }

    @Test
    public void create() throws Exception {
        Restaurant newRestaurant = getNew();
        stubAdminAuth();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newRestaurant)))
                .andExpect(status().isCreated());

    }

    @Test
    public void createInvalid() throws Exception {
        Restaurant invalidRestaurant = getNew();
        invalidRestaurant.setName("");
        stubAdminAuth();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(invalidRestaurant)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", containsStringIgnoringCase(NOT_BLANK)));
    }

    @Test
    public void createForbidden() throws Exception {
        Restaurant newRestaurant = getNew();
        stubUnAuth();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newRestaurant)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createWithDuplicateMail() throws Exception{
        Restaurant duplicateRestaurant = getNew();
        duplicateRestaurant.setEmail(RESTAURANT1.getEmail());
        stubAdminAuth();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(duplicateRestaurant)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message", containsStringIgnoringCase(DUPLICATE_EMAIL)));
    }

    @Test
    public void update() throws Exception {
        Restaurant updatedRestaurant = getUpdated();
        stubAdminAuth();
        perform(MockMvcRequestBuilders.put(REST_URL + updatedRestaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updatedRestaurant)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateInvalid() throws Exception {
        Restaurant invalidRestaurant = getUpdated();
        invalidRestaurant.setName("");
        stubAdminAuth();
        perform(MockMvcRequestBuilders.put(REST_URL + invalidRestaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(invalidRestaurant)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", containsStringIgnoringCase(NOT_BLANK)));

    }

    @Test
    public void updateForbidden() throws Exception {
        Restaurant updatedRestaurant = getUpdated();
        stubUnAuth();
        perform(MockMvcRequestBuilders.put(REST_URL + updatedRestaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updatedRestaurant)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void delete() throws Exception {
        stubAdminAuth();
        perform(MockMvcRequestBuilders.delete(REST_URL + RESTAURANT1_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteNotFond() throws Exception {
        stubAdminAuth();
        perform(MockMvcRequestBuilders.delete(REST_URL  + NOT_FOUND_ID))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", equalTo(NOT_FOUND_MESSAGE)));
    }

    @Test
    public void deleteForbidden() throws Exception {
        stubUnAuth();
        perform(MockMvcRequestBuilders.delete(REST_URL  + RESTAURANT3))
                .andExpect(status().isForbidden());
    }
}