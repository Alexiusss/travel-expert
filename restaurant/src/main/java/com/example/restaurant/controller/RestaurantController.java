package com.example.restaurant.controller;

import com.example.clients.auth.AuthCheckResponse;
import com.example.clients.auth.AuthClient;
import com.example.restaurant.model.Restaurant;
import com.example.restaurant.service.RestaurantService;
import feign.FeignException.Unauthorized;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = RestaurantController.REST_URL, produces = APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class RestaurantController {
    static final String REST_URL = "api/v1/restaurants";

    private final RestaurantService restaurantService;
    private final AuthClient authClient;

    @GetMapping
    public Page<Restaurant> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("get all restaurants");
      return restaurantService.findAllPaginated(PageRequest.of(page, size));
    }

    @PostMapping
    public ResponseEntity<Restaurant> create(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization, @RequestBody Restaurant restaurant) {

        AuthCheckResponse authCheckResponse;
        try {
            authCheckResponse = authClient.isAuth(authorization);
        } catch (Unauthorized e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!authCheckResponse.getAuthorities().contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Restaurant created = restaurantService.create(restaurant);

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }
}