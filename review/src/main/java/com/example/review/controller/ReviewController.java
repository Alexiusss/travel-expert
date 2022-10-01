package com.example.review.controller;

import com.example.review.model.Review;
import com.example.review.model.dto.Rating;
import com.example.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping(path = ReviewController.REST_URL, produces = APPLICATION_JSON_VALUE )
public class ReviewController {
    static final String REST_URL = "/api/v1/reviews";

    private final ReviewService reviewService;

    @Operation(summary = "Get a review by its id")
    @GetMapping("/{id}")
    public ResponseEntity<Review> get(@PathVariable String id) {
        log.info("get review {}", id);
        return ResponseEntity.ok(reviewService.get(id));
    }

    @Operation(summary = "Get a rating by item id")
    @GetMapping("/{itemId}/rating")
    public ResponseEntity<Integer> getByItemId(@PathVariable String itemId) {
        log.info("get review {}", itemId);
        return ResponseEntity.ok(reviewService.getRatingByItemId(itemId));
    }

    @Operation(summary = "Get a rating by item id")
    @GetMapping("/{id}/item/rating")
    public ResponseEntity<Rating> getRating(@PathVariable String id) {
        return ResponseEntity.ok(reviewService.getRating(id));
    }

    @Operation(summary = "Return a list of reviews by item id and filtered according to the query parameters")
    @GetMapping("/{id}/item")
    public ResponseEntity<?> getAllByItemId(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "", required = false) String filter,
            @RequestParam(defaultValue = "", required = false) String[] ratings
            ) {
        log.info("get all reviews for item {}", id);
        return ResponseEntity.ok(reviewService.getAllPaginatedByItemId(PageRequest.of(page, size), id, filter, ratings));
    }

    @Operation(summary = "Return a list of reviews and filtered according the query parameters", description = "A JWT token is required to access this API.")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestHeader(name = "Authorization", defaultValue = "empty") String authorization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String filter
    ) {
        ResponseEntity<Review> isProhibited = reviewService.checkAuth(authorization, null);
        if (!isProhibited.getStatusCode().is2xxSuccessful()) return isProhibited;

        log.info("get all reviews");
        return ResponseEntity.ok(reviewService.getAllPaginated(PageRequest.of(page, size), filter));
    }

    @Operation(summary = "Create a new review", description = "A JWT token is required to access this API.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<Review> create(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization, @Valid @RequestBody Review review) {

        ResponseEntity<Review> isProhibited = reviewService.checkAuth(authorization ,review);
        if (!isProhibited.getStatusCode().is2xxSuccessful()) return isProhibited;

        Review created = reviewService.create(review);

        log.info("add new review {}", created);

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Operation(summary = "Delete a review by its id", description = "A JWT token is required to access this API.")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<Review> delete(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization, @PathVariable String id) {
        log.info("delete review {}", id);

        ResponseEntity<Review> isProhibited = reviewService.checkAuth(authorization, reviewService.get(id));
        if (!isProhibited.getStatusCode().is2xxSuccessful()) return isProhibited;

        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a review by its id", description = "A JWT token is required to access this API.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization, @Valid @RequestBody Review review, @PathVariable String id) {

        ResponseEntity<Review> isProhibited = reviewService.checkAuth(authorization ,review);
        if (!isProhibited.getStatusCode().is2xxSuccessful()) return isProhibited;

        reviewService.update(id, review);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activate a review by its id", description = "A JWT token is required to access this API.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/{id}")
    public ResponseEntity<?> activate(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization, @PathVariable String id) {
        ResponseEntity<Review> isProhibited = reviewService.checkAuth(authorization,null);
        if (!isProhibited.getStatusCode().is2xxSuccessful()) return isProhibited;

        log.info("activate review {}", id);
        reviewService.activate(id);
        return ResponseEntity.noContent().build();
    }

}