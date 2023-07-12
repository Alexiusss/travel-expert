package com.example.review.controller.kc;

import com.example.review.controller.CommonReviewControllerTest;
import com.example.review.model.Review;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.common.test.KeycloakTestData.stubKeycloak;
import static com.example.common.test.TestJwtTokenGenerator.generateAccessToken;
import static com.example.common.test.TestJwtTokenGenerator.generateJwk;
import static com.example.common.util.JsonUtil.asParsedJson;
import static com.example.common.util.JsonUtil.writeValue;
import static com.example.review.util.ReviewTestData.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("kc")
public class KcReviewControllerTest extends CommonReviewControllerTest {

    private static RsaJsonWebKey rsaJsonWebKey;
    private static String adminAccessToken;
    private static String userAccessToken;

    @BeforeAll
    static void init() throws JoseException {
        rsaJsonWebKey = generateJwk();
        adminAccessToken = generateAccessToken(rsaJsonWebKey, List.of("ADMIN", "USER")) ;
        userAccessToken = generateAccessToken(rsaJsonWebKey, List.of("USER")) ;

        WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().port(8180));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8180);
    }


    @Test
    void get() throws Exception {
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.get(REST_URL + REVIEW1_ID)
                .header("Authorization", String.format("Bearer %s", adminAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(REVIEW_MATCHER.contentJson(REVIEW1));
    }

    @Test
    void getNotFound() throws Exception {
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND_ID)
                .header("Authorization", String.format("Bearer %s", adminAccessToken)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", equalTo(NOT_FOUND_MESSAGE)));
    }

    @Test
    void getAllFiltered() throws Exception {
        stubKeycloak(rsaJsonWebKey);
        stubAuthorList();
        perform(MockMvcRequestBuilders.get(REST_URL)
                .header("Authorization", String.format("Bearer %s", adminAccessToken))
                .param("filter", REVIEW3.getTitle()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0]", equalTo(asParsedJson(REVIEW_DTO))));
    }

    @Test
    void getAllPaginated() throws Exception {
        stubKeycloak(rsaJsonWebKey);
        stubAuthorList();
        perform(MockMvcRequestBuilders.get(REST_URL)
                .header("Authorization", String.format("Bearer %s", adminAccessToken))
                .param("size", "2")
                .param("page", "1"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0]", equalTo(asParsedJson(REVIEW_DTO))));
    }

    @Test
    void getAllForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isForbidden());
    }

    @Test
    void create() throws Exception {
        Review newReview = getNew();
        stubKeycloak(rsaJsonWebKey);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .header("Authorization", String.format("Bearer %s", userAccessToken))
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
    void update() throws Exception {
        Review updated = getUpdated();
        updated.setTitle("Updated title");
        updated.setRating(1);

        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.put(REST_URL + REVIEW2_ID)
                .header("Authorization", String.format("Bearer %s", adminAccessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andDo(print());

        REVIEW_MATCHER.assertMatch(reviewRepository.getExisted(REVIEW2_ID), updated);
    }

    @Test
    void like() throws Exception {
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.patch(REST_URL + REVIEW1_ID + "/like")
                .header("Authorization", String.format("Bearer %s", userAccessToken))
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
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.patch(REST_URL + REVIEW1_ID + "/like")
                .header("Authorization", String.format("Bearer %s", userAccessToken))
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
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.delete(REST_URL + REVIEW2_ID)
                .header("Authorization", String.format("Bearer %s", userAccessToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAllByUserId() throws Exception {
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.delete(REST_URL + USER_ID + "/user")
                .header("Authorization", String.format("Bearer %s", adminAccessToken)))
                .andExpect(status().isNoContent());

        int countUserReviews = reviewRepository.findAllByUserId(USER_ID).size();
        assertEquals(0, countUserReviews);
    }

    @Test
    void deleteAllByItemId() throws Exception {
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.delete(REST_URL + ITEM_1_ID + "/item")
                .header("Authorization", String.format("Bearer %s", adminAccessToken)))
                .andExpect(status().isNoContent());

        int countItemReviews = reviewRepository.findAllByItemId(ITEM_1_ID).size();
        assertEquals(0, countItemReviews);
    }

    @Test
    void activate() throws Exception {
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.patch(REST_URL + REVIEW3_ID)
                .header("Authorization", String.format("Bearer %s", adminAccessToken)))
                .andExpect(status().isNoContent());

        assertNotEquals(REVIEW3.isActive(), reviewRepository.getExisted(REVIEW3_ID).isActive());
    }
}