package com.example.hotel.controller;

import com.example.hotel.model.Hotel;
import com.example.hotel.service.HotelService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
@Slf4j
@AllArgsConstructor
public class HotelController {
    public static final String REST_URL = "/api/v1/hotels";

    private final HotelService hotelService;

    @GetMapping("/{id}")
    public ResponseEntity<Hotel> get(@PathVariable String id) {
        log.info("get hotel {}", id);
        return ResponseEntity.ok(hotelService.get(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String filter
    ) {
        log.info("get all hotels");
        return ResponseEntity.ok().body(hotelService.findAllPaginated(PageRequest.of(page, size), filter));
    }

    @PostMapping
    public ResponseEntity<Hotel> create(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization, @Valid @RequestBody Hotel hotel) {
        ResponseEntity<Hotel> isUnauthorized = hotelService.checkAuth(authorization);
        if (isUnauthorized != null) return isUnauthorized;

        Hotel created = hotelService.create(hotel);
        log.info("create new hotel {}", created);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hotel> update(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization,@Valid @RequestBody Hotel hotel, @PathVariable String id) {
        ResponseEntity<Hotel> isUnauthorized = hotelService.checkAuth(authorization);
        if (isUnauthorized != null) return isUnauthorized;

        hotelService.update(hotel, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Hotel> delete(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization, @PathVariable String id) {
        ResponseEntity<Hotel> isUnauthorized = hotelService.checkAuth(authorization);
        if (isUnauthorized != null) return isUnauthorized;
        log.info("delete hotel {}", id);
        hotelService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
