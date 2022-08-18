package com.example.review.repository;

import com.example.common.BaseRepository;
import com.example.review.model.Review;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends BaseRepository<Review> {
}