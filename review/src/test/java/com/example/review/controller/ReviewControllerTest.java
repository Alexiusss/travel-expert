package com.example.review.controller;

import com.example.review.model.Review;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static com.example.common.util.JsonUtil.asParsedJson;
import static com.example.common.util.JsonUtil.writeValue;
import static com.example.review.controller.ReviewExceptionHandler.EXCEPTION_DUPLICATE_REVIEW;
import static com.example.review.util.ReviewTestData.*;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ReviewControllerTest extends CommonReviewControllerTest{

    @BeforeAll
    static void init() {
        WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().port(8180));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8180);
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
    void getNotFound() throws Exception {
        stubAdminAuth();
        perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND_ID))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", equalTo(NOT_FOUND_MESSAGE)));
    }

    @Test
    void getAllFiltered() throws Exception {
        stubAdminAuth();
        stubAuthorList();
        perform(MockMvcRequestBuilders.get(REST_URL)
                .param("filter", REVIEW3.getTitle()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0]", equalTo(asParsedJson(REVIEW_DTO))));
    }

    @Test
    void getAllPaginated() throws Exception {
        stubAdminAuth();
        stubAuthorList();
        perform(MockMvcRequestBuilders.get(REST_URL)
                .param("size", "2")
                .param("page", "1"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0]", equalTo(asParsedJson(REVIEW_DTO))));
    }

    @Test
    void getAllUnAuth() throws Exception {
        stubUnAuth();
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create() throws Exception {
        Review newReview = getNew();
        stubUserAuth();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newReview)))
                .andExpect(status().isCreated());

        Review created = REVIEW_MATCHER.readFromJson(action);
        String newId = created.getId();
        newReview.setId(newId);
        REVIEW_MATCHER.assertMatch(created, newReview);
        REVIEW_MATCHER.assertMatch(reviewRepository.getExisted(newId), created);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void createDuplicate() throws Exception {
        Review duplicateReview = getNew();
        duplicateReview.setUserId(REVIEW1.getUserId());
        duplicateReview.setItemId(REVIEW1.getItemId());

        stubAdminAuth();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(duplicateReview)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message", containsStringIgnoringCase(EXCEPTION_DUPLICATE_REVIEW)));
    }

    @Test
    void createInvalid() throws Exception {
        stubAdminAuth();
        Review invalidReview = getNew();
        invalidReview.setTitle("");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(invalidReview)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", containsStringIgnoringCase(NOT_BLANK)));

    }

    @Test
    void update() throws Exception {
        Review updated = getUpdated();
        updated.setTitle("Updated title");
        updated.setRating(1);

        stubUserAuth();
        perform(MockMvcRequestBuilders.put(REST_URL + REVIEW2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andDo(print());

        REVIEW_MATCHER.assertMatch(reviewRepository.getExisted(REVIEW2_ID), updated);
    }

    @Test
    void updateInvalid() throws Exception {
        stubAdminAuth();
        Review invalidReview = getUpdated();
        invalidReview.setTitle("");
        perform(MockMvcRequestBuilders.put(REST_URL + REVIEW2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(invalidReview)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", containsStringIgnoringCase(NOT_BLANK)));
    }

    @Test
    void like() throws Exception {
        stubUserAuth();
        perform(MockMvcRequestBuilders.patch(REST_URL + REVIEW1_ID + "/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(MODER_ID))
                .andExpect(status().isNoContent());

        Set<String> likes = new HashSet<>(REVIEW1_LIKES);
        likes.add(MODER_ID);
        Set<String> likesFromDB = reviewRepository.getExisted(REVIEW1_ID).getLikes();

        assertEquals(likes, likesFromDB);
    }

    @Test
    void unlike() throws Exception {
        stubUserAuth();
        perform(MockMvcRequestBuilders.patch(REST_URL + REVIEW1_ID + "/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(USER_ID))
                .andExpect(status().isNoContent());

        Set<String> likes = new HashSet<>(REVIEW1_LIKES);
        likes.remove(USER_ID);
        Set<String> likesFromDB = reviewRepository.getExisted(REVIEW1_ID).getLikes();

        assertEquals(likes, likesFromDB);
    }

    @Test
    void delete() throws Exception {
        stubAdminAuth();
        perform(MockMvcRequestBuilders.delete(REST_URL + REVIEW2_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAllByUserId() throws Exception {
        stubAdminAuth();
        perform(MockMvcRequestBuilders.delete(REST_URL + USER_ID + "/user"))
                .andExpect(status().isNoContent());

        int countUserReviews = reviewRepository.findAllByUserId(USER_ID).size();
        assertEquals(0, countUserReviews);
    }

    @Test
    void deleteAllByItemId() throws Exception {
        stubAdminAuth();
        perform(MockMvcRequestBuilders.delete(REST_URL + ITEM_1_ID + "/item"))
                .andExpect(status().isNoContent());

        int countItemReviews = reviewRepository.findAllByItemId(ITEM_1_ID).size();
        assertEquals(0, countItemReviews);
    }

    @Test
    void deleteNotFound() throws Exception {
        stubAdminAuth();
        perform(MockMvcRequestBuilders.delete(REST_URL + NOT_FOUND_ID))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", equalTo(NOT_FOUND_MESSAGE)));
    }

    @Test
    void activate() throws Exception {
        stubAdminAuth();
        perform(MockMvcRequestBuilders.patch(REST_URL + REVIEW3_ID))
                .andExpect(status().isNoContent());

        assertNotEquals(REVIEW3.isActive(), reviewRepository.getExisted(REVIEW3_ID).isActive());
    }
}