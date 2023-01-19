package com.example.hotel.util;

import com.example.common.util.MatcherFactory;
import com.example.hotel.model.Hotel;

import java.util.List;

public class HotelTestData {

    public static final MatcherFactory.Matcher<Hotel> HOTEL_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Hotel.class, "createdAt", "modifiedAt");

    public static final Hotel HOTEL_1 = new Hotel( "1", null, null, 0, "Hotel1 name", "Hotel1 address", "hotel1@gmail.com", "+1111111111", "hotel1.com", 5, "Hotel1 description", List.of("Sea view", "Kitchen"), List.of("single", "double"), List.of(), List.of("English", "Russian"),  List.of(), List.of("hotel1.jpg"));

    public static Hotel getNew() {
        Hotel hotel = new Hotel();
        hotel.setName("New hotel");
        hotel.setAddress("New hotel address");
        hotel.setEmail("newHotelEmail@gmail.com");
        hotel.setPhoneNumber("+1234567890");
        hotel.setWebsite("new-hotel.com");
        hotel.setHotelClass(5);
        hotel.setDescription("New hotel description");
        hotel.setRoomFeatures(List.of("Sea view", "Big bed"));
        hotel.setLanguagesUsed(List.of("English", "Turkey", "Russian"));
        hotel.setFileNames(List.of("newHotelImage1.jpg", "newHotelImage2.jpg"));
        return hotel;
    }
}