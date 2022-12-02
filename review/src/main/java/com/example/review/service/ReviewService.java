package com.example.review.service;

import com.example.clients.auth.AuthCheckResponse;
import com.example.clients.auth.AuthClient;
import com.example.clients.auth.AuthorDTO;
import com.example.clients.review.ReviewResponse;
import com.example.review.model.Review;
import com.example.review.model.dto.Rating;
import com.example.review.model.dto.ReviewDTO;
import com.example.review.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.common.util.ValidationUtil.assureIdConsistent;
import static com.example.common.util.ValidationUtil.checkNew;

@Service
@AllArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AuthClient authClient;

    public Rating getRatingByItemId(String itemId) {
        List<Review> reviewList = reviewRepository.findAllByItemId(itemId);
        return getRating(itemId, reviewList);
    }

    public Rating getRatingByUserId(String userId) {
        List<Review> reviewList = reviewRepository.findAllByUserId(userId);
        return getRating(userId, reviewList);
    }

    public Rating getRating(String id, List<Review> reviewList) {
        Map<Integer, Integer> ratings = new HashMap<>();
        reviewList.forEach(s -> ratings.merge(s.getRating(), 1, Math::addExact));

        for (int i = 1; i <= 5; i++) {
            ratings.putIfAbsent(i, 0);
        }

        Double averageRating = getAverageRating(reviewList);

        return new Rating(id, averageRating, ratings);
    }

    private Double getAverageRating(List<Review> reviewList) {
        return reviewList.stream()
                .mapToDouble(Review::getRating)
                .average().orElse(0.0);
    }

    public Page<ReviewDTO> getAllPaginated(Pageable pageable, String filter) {
        Page<Review> reviews;
        if (filter.isEmpty()) {
            reviews = reviewRepository.findAll(pageable);
        } else {
            reviews = reviewRepository.findAllWithFilter(pageable, filter);
        }
        return setAuthors(reviews, getUserIds(reviews));
    }

    public Page<ReviewDTO> getAllPaginatedByItemId(Pageable pageable, String id, String filter, String[] ratings) {
        Page<Review> reviews;
        if (filter.isEmpty()) {
            reviews = reviewRepository.findAllByItemId(pageable, id);
        } else {
            reviews = reviewRepository.findAllByItemIdFiltered(pageable, id, filter);
        }
        if (ratings.length > 0) {
            reviews = getAllFilteredByRatings(reviews, pageable, ratings);
        }
        return setAuthors(reviews, getUserIds(reviews));
    }

    private Set<String> getUserIds(Page<Review> reviews) {
        return reviews.get().map(Review::getUserId).collect(Collectors.toSet());
    }

    public Page<ReviewDTO> setAuthors(Page<Review> reviewPage, Set<String> userListIds) {
        List<AuthorDTO> authorList = authClient.getAuthorList(userListIds);
        return reviewPage.map(review -> createReviewDTO(review, authorList));
    }

    private ReviewDTO createReviewDTO (Review review, List<AuthorDTO> authorList) {
        return ReviewDTO
                .builder()
                .id(review.id())
                .createdAt(review.getCreatedAt())
                .title(review.getTitle())
                .description(review.getDescription())
                .fileNames(review.getFileNames())
                .active(review.isActive())
                .rating(review.getRating())
                .likes(review.getLikes())
                .author(getAuthor(authorList, review.getUserId()))
                .build();
    }

    private AuthorDTO getAuthor(List<AuthorDTO> authorList, String id){
        return authorList.stream()
                .filter(author -> author.getAuthorId().equals(id))
                .findAny()
                .orElseThrow();
    }

    public Page<Review> getAllFilteredByRatings(Page<Review> reviews, Pageable pageable, String[] ratings) {
        List<Review> filteredReviews = reviews.get().filter(review -> List.of(ratings).contains(review.getRating().toString())).collect(Collectors.toList());
        return new PageImpl<>(filteredReviews, pageable, filteredReviews.size());
    }

    public List<Review> getAllByUserId(String userId) {
        return reviewRepository.findAllByUserId(userId);
    }

    public Object getAllActiveByUserId(String userId) {
        return reviewRepository.findAllActiveByUserId(userId);
    }

    public Review get(String id) {
        return reviewRepository.getExisted(id);
    }

    public Review getAnyByUserId(String userId) {
        return reviewRepository.getFirstByUserId(userId);
    }

    public Review getAnyByItemId(String userId) {
        return reviewRepository.getFirstByItemId(userId);
    }

    public Double getAverageRatingByItemId(String itemId) {
        return reviewRepository.getAverageRatingByItemId(itemId);
    }

    public Integer getCountByUserId(String userId) {
        return reviewRepository.getCountByUserId(userId);
    }

    public List<ReviewResponse> getActiveReviewsCountByUsersIds(String[] authors) {
        return reviewRepository.getActiveReviewsCountByUsersIds(authors);
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

    @Transactional
    public void like(String reviewId, String userId) {
        Review reviewFromDB = reviewRepository.getExisted(reviewId);
        Set<String> likes = reviewFromDB.getLikes();
        if (likes.contains(userId)) {
            log.info("unlike for review {} from user {}", reviewId, userId);
            likes.remove(userId);
        } else {
            log.info("like for review {} from user {}", reviewId, userId);
            likes.add(userId);
        }
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

    public void deleteAllByUserId(String userId) {
        reviewRepository.deleteAllByUserId(userId);
    }

    public void deleteAllByItemId(String itemId) {
        reviewRepository.deleteAllByItemId(itemId);
    }

}