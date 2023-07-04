package com.example.restaurant.controller;

import com.example.common.util.TestProfileResolver;
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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.example.common.util.JsonUtil.asParsedJson;
import static com.example.restaurant.util.RestaurantTestData.*;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles(resolver = TestProfileResolver.class)
@Sql(value = {"/data.sql"})
public class CommonRestaurantControllerTest {

    protected static final String REST_URL = RestaurantController.REST_URL + "/";

    @Autowired
    protected MockMvc mockMvc;

    protected ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
    }

    @Test
    public void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + RESTAURANT1_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(RESTAURANT_MATCHER.contentJson(RESTAURANT1));
    }

    @Test
    public void getByName() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + RESTAURANT1.getName() + "/name"))
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
    public void getNotFoundByName() throws Exception {
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
}
