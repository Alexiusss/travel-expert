package com.example.review.service;

import com.example.clients.auth.AuthCheckResponse;
import com.example.clients.auth.AuthClient;
import com.example.review.model.Review;
import com.example.review.model.dto.Rating;
import com.example.review.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.common.util.ValidationUtil.assureIdConsistent;
import static com.example.common.util.ValidationUtil.checkNew;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AuthClient authClient;

    public Rating getRating(String itemId) {
        Map<Integer, Integer> ratings = new HashMap<>();
        List<Review> reviewList = reviewRepository.findAllByItemId(itemId);

        reviewList.forEach(s -> ratings.merge(s.getRating(), 1, Math::addExact));

        for (int i = 1; i <= 5; i++) {
            ratings.putIfAbsent(i, 0);
        }

        Double averageRating = getAverageRating(reviewList);

        return new Rating(itemId, averageRating, ratings);
    }

    private Double getAverageRating(List<Review> reviewList) {
        return reviewList.stream()
                .mapToDouble(Review::getRating)
                .average().orElse(0.0);
    }

    public Page<Review> getAllPaginated(Pageable pageable, String filter) {
        if (filter.isEmpty()) {
            return reviewRepository.findAll(pageable);
        }
        return reviewRepository.findAllWithFilter(pageable, filter);
    }

    public Page<Review> getAllPaginatedByItemId(Pageable pageable, String id, String filter, String[] ratings) {
        Page<Review> reviews;
        if (filter.isEmpty()) {
            reviews = reviewRepository.findAllByItemId(pageable, id);
        } else {
            reviews = reviewRepository.findAllByItemIdFiltered(pageable, id, filter);
        }
        if (ratings.length > 0) {

            reviews = getAllFilteredByRatings(reviews, pageable, ratings);
        }
        return reviews;
    }

    public Page<Review> getAllFilteredByRatings(Page<Review> reviews, Pageable pageable, String[] ratings) {
        List<Review> filteredReviews = reviews.get().filter(review -> List.of(ratings).contains(review.getRating().toString())).collect(Collectors.toList());
        return new PageImpl<>(filteredReviews, pageable, filteredReviews.size());
    }

    public Review get(String id) {
        return reviewRepository.getExisted(id);
    }

    public Integer getRatingByItemId(String itemId) {
        return reviewRepository.getRatingByItemId(itemId);
    }

    public Integer getCountByUserId(String userId) {
        return reviewRepository.getCountByUserId(userId);
    }

    public Review create(Review review) {
        Assert.notNull(review, "review must not be null");
        checkNew(review);
        return reviewRepository.save(review);
    }

    public void delete(String id) {
        reviewRepository.deleteExisted(id);
    }

    @Transactional
    public void update(String id, Review review) {
        assureIdConsistent(review, review.id());
        Review reviewFromDB = reviewRepository.getExisted(id);
        reviewFromDB.setActive(review.isActive());
        reviewFromDB.setTitle(review.getTitle());
        reviewFromDB.setDescription(review.getDescription());
        reviewFromDB.setRating(review.getRating());
        reviewFromDB.setFileNames(review.getFileNames());
    }

    public ResponseEntity<Review> checkAuth(String authorization, Review review) {
        AuthCheckResponse authCheckResponse = authClient.isAuth(authorization);

        if ((!authCheckResponse.getAuthorities().contains("ADMIN") ||
                !authCheckResponse.getAuthorities().contains("MODERATOR")) &&
                (review != null && !authCheckResponse.getUserId().equals(review.getUserId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok().build();
    }

    @Transactional
    public void activate(String id) {
        Review reviewFromDb = get(id);
        boolean active = reviewFromDb.isActive();
        reviewFromDb.setActive(!active);
    }
}