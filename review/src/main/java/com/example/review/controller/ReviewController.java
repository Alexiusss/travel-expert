package com.example.review.controller;

import com.example.review.ReviewService;
import com.example.review.model.Review;
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
    public Page<Review> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("get all reviews");
        return reviewService.getAllPaginated(PageRequest.of(page, size));
    }

    @PostMapping
    public ResponseEntity<Review> create(@Valid @RequestBody Review review) {

        Review created = reviewService.create(review);

        log.info("add new review {}", created);

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Review> delete(@PathVariable String id) {
        log.info("delete review {}", id);

        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Review review, @PathVariable String id) {

        reviewService.update(id, review);

        return ResponseEntity.noContent().build();
    }

}