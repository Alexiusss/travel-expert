package com.example.hotel.controller;

import com.example.hotel.model.Hotel;
import com.example.hotel.service.HotelService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping(path = HotelController.REST_URL, produces = APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class HotelController {
    public static final String REST_URL = "/api/v1/hotels";

    private final HotelService hotelService;

    @GetMapping("/{id}")
    public ResponseEntity<Hotel> get(@PathVariable String id) {
        return ResponseEntity.ok(hotelService.get(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok().body(hotelService.findAll());
    }

    @PostMapping
    public ResponseEntity<Hotel> create(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization, @Valid @RequestBody Hotel hotel) {
        ResponseEntity<Hotel> isUnauthorized = hotelService.checkAuth(authorization);
        if (isUnauthorized != null) return isUnauthorized;

        Hotel created = hotelService.create(hotel);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Hotel> delete(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization, @PathVariable String id) {
        ResponseEntity<Hotel> isUnauthorized = hotelService.checkAuth(authorization);
        if (isUnauthorized != null) return isUnauthorized;

        hotelService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
