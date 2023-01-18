package com.example.hotel.util;

import com.example.hotel.model.Hotel;

import java.util.List;

public class HotelTestData {

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