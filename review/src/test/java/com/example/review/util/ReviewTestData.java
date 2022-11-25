package com.example.review.util;

import com.example.clients.auth.AuthCheckResponse;
import com.example.clients.review.ReviewResponse;
import com.example.common.util.JsonUtil;
import com.example.common.util.MatcherFactory;
import com.example.review.model.Review;
import com.example.review.model.dto.Rating;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.experimental.UtilityClass;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@UtilityClass
public class ReviewTestData {

    public static final MatcherFactory.Matcher<Review> REVIEW_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Review.class,"createdAt","modifiedAt");
    public static final MatcherFactory.Matcher<Rating> RATING_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Rating.class,"ratingsMap");
    public static final MatcherFactory.Matcher<ReviewResponse> REVIEW_RESPONSE_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(ReviewResponse.class);

    public static final String REVIEW1_ID = "11";
    public static final String REVIEW2_ID = "12";
    public static final String REVIEW3_ID = "13";
    public static final String NOT_FOUND_ID = "1000";
    public static final String NOT_FOUND_MESSAGE = String.format("Entity with id=%s not found", NOT_FOUND_ID);
    public static String NOT_BLANK = "must not be blank";
    public static final Instant REVIEW_INSTANT = Timestamp.valueOf("2022-08-23 19:06:03.621013").toInstant();
    public static final String ADMIN_ID = "1";
    public static final String USER_ID = "2";
    public static final String MODER_ID = "3";
    public static final String ITEM_1_ID = "1";
    public static final Set<String> REVIEW1_LIKES = Set.of(ADMIN_ID, USER_ID);
    public static final ReviewResponse ADMIN_REVIEW_COUNT = new ReviewResponse(ADMIN_ID, 1L);
    public static final ReviewResponse MODER_REVIEW_COUNT = new ReviewResponse(MODER_ID, 1L);
    public static final AuthCheckResponse AUTH_ADMIN_RESPONSE = new AuthCheckResponse("1", List.of("ADMIN", "MODERATOR", "USER"));
    public static final AuthCheckResponse AUTH_USER_RESPONSE = new AuthCheckResponse(USER_ID, List.of("USER"));

    public static final Review REVIEW1 = new Review(REVIEW1_ID, null, null, true, 0, "review #1", "review #1 description", 5, null, ADMIN_ID, "2", REVIEW1_LIKES);
    public static final Review REVIEW2 = new Review(REVIEW2_ID, null, null, false, 0, "review #2", "review #2 description", 4, null, USER_ID, ITEM_1_ID, Set.of());
    public static final Review REVIEW3 = new Review(REVIEW3_ID, REVIEW_INSTANT, null, true, 0, "review #3", "review #3 description", 3, null, MODER_ID, ITEM_1_ID, Set.of());
    public static final Rating ITEM_1_RATING = new Rating(ITEM_1_ID, 3.0, Map.of(1 , 0,2 , 0, 3, 1,4, 0, 5 ,0));
    public static final Rating USER_RATING = new Rating(USER_ID, 4.0, Map.of(1 , 0,2 , 0, 3, 0,4, 1, 5 ,0));

    public static Review getNew() {
        return new Review(null, null, null, true, 0, "New review", "New review description", 2, null, USER_ID, "2", Set.of());
    }

    public static Review getUpdated() {
        return new Review(REVIEW2_ID, null, null, true, 0, "Updated title", "Updated description", 2, null, USER_ID, "1", Set.of());
    }

    public static void stubAdminAuth() {
        stubFor(WireMock.get(urlMatching("/api/v1/auth/validate"))
                .willReturn(okForContentType("application/json", JsonUtil.writeValue(AUTH_ADMIN_RESPONSE))));
    }

    public static void stubUserAuth() {
        stubFor(WireMock.get(urlMatching("/api/v1/auth/validate"))
                .willReturn(okForContentType("application/json", JsonUtil.writeValue(AUTH_USER_RESPONSE))));
    }

    public static void stubUnAuth() {
        stubFor(WireMock.get(urlMatching("/api/v1/auth/validate"))
                .willReturn(unauthorized()));
    }
}