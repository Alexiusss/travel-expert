package com.example.review.controller;

import com.example.common.util.JsonUtil;
import com.example.review.model.Review;
import com.example.review.repository.ReviewRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.example.common.util.JsonUtil.asParsedJson;
import static com.example.review.util.ReviewTestData.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = {"/data.sql"})
public class ReviewControllerTest {

    private static final String REST_URL = ReviewController.REST_URL + "/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewRepository reviewRepository;

    private ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
    }

    @BeforeAll
    static void init() {
        WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().port(7077));
        wireMockServer.start();
        WireMock.configureFor("localhost", 7077);
    }

    @Test
    void get() throws Exception {
        stubAdminAuth();
        perform(MockMvcRequestBuilders.get(REST_URL + REVIEW1_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(REVIEW_MATCHER.contentJson(REVIEW1));
    }

    @Test
    void getAllPaginated() throws Exception {
        stubAdminAuth();
        perform(MockMvcRequestBuilders.get(REST_URL)
                .param("size", "2")
                .param("page", "1"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0]", equalTo(asParsedJson(REVIEW3))));
    }

    @Test
    void getAllForbidden() throws Exception {
        stubUnAuth();
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    void create() throws Exception {
        Review newReview = getNew();
        stubUserAuth();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newReview)))
                .andExpect(status().isCreated());

        Review created = REVIEW_MATCHER.readFromJson(action);
        String newId = created.getId();
        newReview.setId(newId);
        REVIEW_MATCHER.assertMatch(created, newReview);
        REVIEW_MATCHER.assertMatch(reviewRepository.getExisted(newId), created);
    }

    @Test
    void update() throws Exception {
        Review updated = getUpdated();
        updated.setTitle("Updated title");
        updated.setRating(1);

        stubUserAuth();
        perform(MockMvcRequestBuilders.put(REST_URL + REVIEW2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print());

        REVIEW_MATCHER.assertMatch(reviewRepository.getExisted(REVIEW2_ID), updated);
    }

    @Test
    void delete() throws Exception {
        stubAdminAuth();
        perform(MockMvcRequestBuilders.delete(REST_URL + REVIEW2_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void activate() throws Exception {
        stubAdminAuth();
        perform(MockMvcRequestBuilders.patch(REST_URL + REVIEW3_ID))
                .andExpect(status().isNoContent());

        assertNotEquals(REVIEW3.isActive(), reviewRepository.getExisted(REVIEW3_ID).isActive());
    }
}