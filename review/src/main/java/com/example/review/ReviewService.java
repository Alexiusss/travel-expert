package com.example.review;

import com.example.review.model.Review;
import com.example.review.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Page<Review> getAllPaginated(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }
}