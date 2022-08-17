package com.example.review.controller;

import com.example.review.ReviewService;
import com.example.review.model.Review;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping(path = ReviewController.REST_URL, produces = APPLICATION_JSON_VALUE )
public class ReviewController {
    static final String REST_URL = "/api/v1/reviews";

    private final ReviewService reviewService;

    @GetMapping
    public Page<Review> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return reviewService.getAllPaginated(PageRequest.of(page, size));
    }
}