package com.example.review.controller;

import com.example.common.util.TestProfileResolver;
import com.example.review.repository.ReviewRepository;
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

import java.util.Arrays;
import java.util.List;

import static com.example.common.util.JsonUtil.asParsedJson;
import static com.example.common.util.JsonUtil.writeValue;
import static com.example.review.util.ReviewTestData.*;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles(resolver = TestProfileResolver.class)
@Sql(value = {"/data.sql"})
public class CommonReviewControllerTest {

    protected static final String REST_URL = ReviewController.REST_URL + "/";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ReviewRepository reviewRepository;

    protected ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
    }

    @Test
    void getCountByUserId() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_ID + "/user/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("1"));
    }

    @Test
    void getActiveReviewsCountByUsersIds() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "user/reviewCountActive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(List.of(ADMIN_ID, MODER_ID))))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(REVIEW_RESPONSE_MATCHER.contentJson(List.of(ADMIN_REVIEW_COUNT, MODER_REVIEW_COUNT)));
    }

    @Test
    void getByItemId() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + ITEM_1_ID + "/rating"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("3.5"));
    }

    @Test
    void getRatingByItemId() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + ITEM_1_ID + "/item/rating"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RATING_MATCHER.contentJson(ITEM_1_RATING));
    }

    @Test
    void getRatingByUserId() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + USER_ID + "/user/rating"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RATING_MATCHER.contentJson(USER_RATING));
    }

    @Test
    void getAllByItemId() throws Exception {
        String itemId = REVIEW3.getItemId();
        stubAuthorList();
        perform(MockMvcRequestBuilders.get(REST_URL + itemId + "/item")
                .param("size", "20")
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0]", equalTo(asParsedJson(REVIEW_DTO))));

    }

    @Test
    void getAllByUserId() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + USER_ID + "/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(REVIEW_MATCHER.contentJson(List.of(REVIEW2)));
    }

    @Test
    void getAllActiveByUserId() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + USER_ID + "/user/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Arrays.toString(new String[0])));
    }
}