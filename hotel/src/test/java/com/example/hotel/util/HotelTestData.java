package com.example.hotel.util;

import com.example.clients.auth.AuthCheckResponse;
import com.example.common.util.JsonUtil;
import com.example.common.util.MatcherFactory;
import com.example.hotel.model.Hotel;
import com.github.tomakehurst.wiremock.client.WireMock;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class HotelTestData {

    public static final MatcherFactory.Matcher<Hotel> HOTEL_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Hotel.class, "createdAt", "modifiedAt");

    public static final String HOTEL_1_ID ="1";
    public static final String HOTEL_2_ID ="2";
    public static final String HOTEL_3_ID ="3";
    public static final String NOT_FOUND_ID ="1111";
    public static final String NOT_FOUND_MESSAGE = String.format("Entity with id=%s not found", NOT_FOUND_ID);public static final AuthCheckResponse AUTH_ADMIN_RESPONSE = new AuthCheckResponse("1", List.of("ADMIN", "MODERATOR", "USER"));

    public static final Hotel HOTEL_1 = new Hotel(HOTEL_1_ID , null, null, 0, "Hotel1 name", "Hotel1 address", "hotel1@gmail.com", "+1111111111", "hotel1.com", 5, "Hotel1 description", List.of("Sea view", "Kitchen"), List.of("single", "double"), List.of(), List.of("English", "Russian"),  List.of(), List.of("hotel1.jpg"));

    public static Hotel getNew() {
        Hotel hotel = new Hotel();
        hotel.setName("New hotel");
        hotel.setAddress("New hotel address");
        hotel.setEmail("newHotelEmail@gmail.com");
        hotel.setPhoneNumber("+1 (234) 567-89-10");
        hotel.setWebsite("new-hotel.com");
        hotel.setHotelClass(5);
        hotel.setDescription("New hotel description");
        hotel.setRoomFeatures(List.of("Sea view", "Big bed"));
        hotel.setLanguagesUsed(List.of("English", "Turkey", "Russian"));
        hotel.setFileNames(List.of("newHotelImage1.jpg", "newHotelImage2.jpg"));
        return hotel;
    }

    public static void stubAuth() {
        stubFor(WireMock.get(urlMatching("/api/v1/auth/validate"))
                .willReturn(okForContentType("application/json", JsonUtil.writeValue(AUTH_ADMIN_RESPONSE))));
    }
}