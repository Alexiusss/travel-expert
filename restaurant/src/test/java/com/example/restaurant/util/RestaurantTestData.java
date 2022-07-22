package com.example.restaurant.util;

import com.example.restaurant.controller.MatcherFactory;
import com.example.restaurant.model.Restaurant;
import lombok.experimental.UtilityClass;



@UtilityClass
public class RestaurantTestData {

    public static final MatcherFactory.Matcher<Restaurant> RESTAURANT_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Restaurant.class,"createdAt","modifiedAt");

    public static final String RESTAURANT1_ID = "1";
    public static final String RESTAURANT2_ID = "2";
    public static final String RESTAURANT3_ID = "3";
    public static final String NOT_FOUND_ID = "1000";
    public static final String NOT_FOUND_MESSAGE = String.format("Entity with id=%s not found", NOT_FOUND_ID);

    public static final Restaurant RESTAURANT1 = new Restaurant(RESTAURANT1_ID, null, null, 0, "restaurant1", "restaurant1 cuisine", null, "restaurant1@gmail.com", "restaurant1 address", "+1111111111", "restaurant1.com");
    public static final Restaurant RESTAURANT2 = new Restaurant(RESTAURANT2_ID, null, null, 0, "restaurant2", "restaurant2 cuisine", null, "restaurant2@gmail.com", "restaurant2 address", "+2222222222", "restaurant2.com");
    public static final Restaurant RESTAURANT3 = new Restaurant(RESTAURANT3_ID, null, null, 0, "restaurant3", "restaurant3 cuisine", null, "restaurant3@gmail.com", "restaurant3 address", "+3333333333", "restaurant3.com");
}
