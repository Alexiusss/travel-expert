package com.example.review.repository;

import com.example.common.BaseRepository;
import com.example.review.model.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends BaseRepository<Review> {
    @Query("SELECT r FROM Review r " +
            "WHERE r.itemId=?1 " +
            "AND r.active=true")
    List<Review> findAllByItemId(String id);
}