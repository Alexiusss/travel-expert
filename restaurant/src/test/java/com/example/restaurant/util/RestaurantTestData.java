package com.example.restaurant.util;

import com.example.clients.auth.AuthCheckResponse;
import com.example.common.util.JsonUtil;
import com.example.restaurant.controller.MatcherFactory;
import com.example.restaurant.model.Restaurant;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.experimental.UtilityClass;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


@UtilityClass
public class RestaurantTestData {

    public static final MatcherFactory.Matcher<Restaurant> RESTAURANT_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Restaurant.class,"createdAt","modifiedAt");

    public static final String RESTAURANT1_ID = "1";
    public static final String RESTAURANT2_ID = "2";
    public static final String RESTAURANT3_ID = "3";
    public static final String NOT_FOUND_ID = "1000";
    public static final String NOT_FOUND_MESSAGE = String.format("Entity with id=%s not found", NOT_FOUND_ID);
    public static final AuthCheckResponse AUTH_ADMIN_RESPONSE = new AuthCheckResponse("1", List.of("ADMIN", "MODERATOR", "USER"));
    public static String NOT_BLANK = "must not be blank";
    public static String DUPLICATE_EMAIL = "Duplicate email";

    public static final Restaurant RESTAURANT1 = new Restaurant(RESTAURANT1_ID, null, null, 0, "restaurant1", "restaurant1 cuisine", null, "restaurant1@gmail.com", "restaurant1 address", "+1111111111", "restaurant1.com");
    public static final Restaurant RESTAURANT2 = new Restaurant(RESTAURANT2_ID, null, null, 0, "restaurant2", "restaurant2 cuisine", null, "restaurant2@gmail.com", "restaurant2 address", "+2222222222", "restaurant2.com");
    public static final Restaurant RESTAURANT3 = new Restaurant(RESTAURANT3_ID, null, null, 0, "restaurant3", "restaurant3 cuisine", null, "restaurant3@gmail.com", "restaurant3 address", "+3333333333", "restaurant3.com");

    public static Restaurant getNew() {
        return new Restaurant(null, null, null, 0, "new restaurant", "new restaurant cuisine", null, "new_estaurant@gmail.com", "new restaurant address", "+444444444", "new restaurant.com");
    }

    public static Restaurant getUpdated() {
        return  new Restaurant(RESTAURANT2_ID, null, null, 0, "updated restaurant2", "updated cuisine", null, "restaurant2@gmail.com", "updated restaurant2 address", "+2222222222", "updated_restaurant2.com");
    }

    public static void stubAuth() {
        stubFor(WireMock.get(urlMatching("/api/v1/auth/validate"))
                .willReturn(okForContentType("application/json", JsonUtil.writeValue(AUTH_ADMIN_RESPONSE))));
    }
}
