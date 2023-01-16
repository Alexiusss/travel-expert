package com.example.hotel.controller;

import com.example.hotel.service.HotelService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping(path = HotelController.REST_URL, produces = APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class HotelController {
    public static final String REST_URL = "/api/v1/hotels";

    private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok().body(hotelService.findAll());
    }
}
