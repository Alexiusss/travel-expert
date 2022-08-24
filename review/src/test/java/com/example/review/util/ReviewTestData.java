package com.example.review.util;

import com.example.clients.auth.AuthCheckResponse;
import com.example.common.util.JsonUtil;
import com.example.review.model.Review;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.experimental.UtilityClass;
import com.example.common.util.MatcherFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@UtilityClass
public class ReviewTestData {

    public static final MatcherFactory.Matcher<Review> REVIEW_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Review.class,"createdAt","modifiedAt");

    public static final String REVIEW1_ID = "11";
    public static final String REVIEW2_ID = "12";
    public static final String REVIEW3_ID = "13";
    public static final Instant REVIEW_INSTANT = Timestamp.valueOf("2022-08-23 19:06:03.621013").toInstant();
    public static final AuthCheckResponse AUTH_ADMIN_RESPONSE = new AuthCheckResponse("1", List.of("ADMIN", "MODERATOR", "USER"));
    public static final AuthCheckResponse AUTH_USER_RESPONSE = new AuthCheckResponse("2", List.of("USER"));
    public static final AuthCheckResponse UN_AUTH_RESPONSE = new AuthCheckResponse("", List.of());

    public static final Review REVIEW1 = new Review(REVIEW1_ID, null, null, true, 0, "review #1", "review #1 description", 5, null, "1", "2");
    public static final Review REVIEW2 = new Review(REVIEW2_ID, null, null, true, 0, "review #2", "review #2 description", 4, null, "2", "1");
    public static final Review REVIEW3 = new Review(REVIEW3_ID, REVIEW_INSTANT, null, false, 0, "review #3", "review #3 description", 3, null, "3", "1");

    public static Review getNew() {
        return new Review(null, null, null, true, 0, "New review", "New review description", 2, null, "2", "2");
    }

    public static Review getUpdated() {
        return new Review(REVIEW2_ID, null, null, true, 0, "Updated title", "Updated description", 2, null, "2", "1");
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
                .willReturn(okForContentType("application/json",JsonUtil.writeValue(UN_AUTH_RESPONSE))));
    }
}