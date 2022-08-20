package com.example.review;

import com.example.clients.auth.AuthCheckResponse;
import com.example.clients.auth.AuthClient;
import com.example.review.model.Review;
import com.example.review.repository.ReviewRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static com.example.common.util.ValidationUtil.assureIdConsistent;
import static com.example.common.util.ValidationUtil.checkNew;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AuthClient authClient;

    public Page<Review> getAllPaginated(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    public Review get(String id) {
        return reviewRepository.getExisted(id);
    }

    public Review create(Review review) {
        Assert.notNull(review, "review must not be null");
        checkNew(review);
        return reviewRepository.save(review);
    }

    public void delete(String id) {
        reviewRepository.deleteExisted(id);
    }

    public void update(String id, Review review) {
        assureIdConsistent(review, review.id());
        Review reviewFromDB = reviewRepository.getExisted(id);
        reviewFromDB.setTitle(review.getTitle());
        reviewFromDB.setDescription(review.getDescription());
        reviewFromDB.setRating(review.getRating());
        reviewFromDB.setFilename(reviewFromDB.getFilename());
    }

    public ResponseEntity<Review> checkAuth(String authorization, Review review) {
        AuthCheckResponse authCheckResponse;
        try {
            authCheckResponse = authClient.isAuth(authorization);
        } catch (FeignException.Unauthorized e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!authCheckResponse.getAuthorities().contains("ADMIN") ||
                !authCheckResponse.getAuthorities().contains("MODER") ||
                !authCheckResponse.getUserId().equals(review.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok().build();
    }
}