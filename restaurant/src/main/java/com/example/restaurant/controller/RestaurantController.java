package com.example.restaurant.controller;

import com.example.restaurant.model.Restaurant;
import com.example.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping(path = RestaurantController.REST_URL, produces = APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class RestaurantController {
    static final String REST_URL = "/api/v1/restaurants";

    private final RestaurantService restaurantService;

    @Operation(summary = "Get a restaurant by its id")
    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> get(@PathVariable String id) {
        log.info("get restaurant {}", id);
        return ResponseEntity.ok(restaurantService.get(id));
    }

    @Operation(summary = "Get a restaurant by its name")
    @GetMapping("/{name}/name")
    public ResponseEntity<Restaurant> getByName(@PathVariable String name) {
        log.info("get restaurant {}", name);
        return ResponseEntity.ok(restaurantService.getByName(name));
    }

    @Operation(summary = "Return a list of restaurants and filtered according the query parameters")
    @GetMapping
    public Page<Restaurant> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String filter
    ) {
        log.info("get all restaurants");
        return restaurantService.findAllPaginated(PageRequest.of(page, size), filter);
    }

    @Operation(summary = "Create a new restaurant", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<Restaurant> create(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization, @Valid @RequestBody Restaurant restaurant) {

        if (restaurantService.currentProfileName("!kc")) {
            ResponseEntity<Restaurant> isUnauthorized = restaurantService.checkAuth(authorization);
            if (isUnauthorized != null) return isUnauthorized;
        }

        Restaurant created = restaurantService.create(restaurant);

        log.info("create new restaurant {}", created);

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Operation(summary = "Delete a restaurant by its id", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<Restaurant> delete(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization, @PathVariable String id) {
        log.info("delete restaurant {}", id);

        if (restaurantService.currentProfileName("!kc")) {
            ResponseEntity<Restaurant> isUnauthorized = restaurantService.checkAuth(authorization);
            if (isUnauthorized != null) return isUnauthorized;
        }

        restaurantService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a restaurant by its id", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization,
                                    @Valid @RequestBody Restaurant restaurant, @PathVariable String id) {

        if (restaurantService.currentProfileName("!kc")) {
            ResponseEntity<Restaurant> isUnauthorized = restaurantService.checkAuth(authorization);
            if (isUnauthorized != null) return isUnauthorized;
        }

        restaurantService.update(id, restaurant);

        return ResponseEntity.noContent().build();
    }
}