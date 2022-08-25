package com.example.review.controller;

import com.example.review.service.ReviewService;
import com.example.review.model.Review;
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

    @GetMapping("/{id}")
    public ResponseEntity<Review> get(@PathVariable String id) {
        log.info("get review {}", id);
        return ResponseEntity.ok(reviewService.get(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestHeader(name = "Authorization", defaultValue = "empty") String authorization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        ResponseEntity<Review> isProhibited = reviewService.checkAuth(authorization, null);
        if (!isProhibited.getStatusCode().is2xxSuccessful()) return isProhibited;

        log.info("get all reviews");
        return ResponseEntity.ok(reviewService.getAllPaginated(PageRequest.of(page, size)));
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Review> delete(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization, @PathVariable String id) {
        log.info("delete review {}", id);

        ResponseEntity<Review> isProhibited = reviewService.checkAuth(authorization, reviewService.get(id));
        if (!isProhibited.getStatusCode().is2xxSuccessful()) return isProhibited;

        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization, @Valid @RequestBody Review review, @PathVariable String id) {

        ResponseEntity<Review> isProhibited = reviewService.checkAuth(authorization ,review);
        if (!isProhibited.getStatusCode().is2xxSuccessful()) return isProhibited;

        reviewService.update(id, review);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> activate(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization, @PathVariable String id) {
        ResponseEntity<Review> isProhibited = reviewService.checkAuth(authorization,null);
        if (!isProhibited.getStatusCode().is2xxSuccessful()) return isProhibited;

        log.info("activate review {}", id);
        reviewService.activate(id);
        return ResponseEntity.noContent().build();
    }

}