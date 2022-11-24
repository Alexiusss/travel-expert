package com.example.review.repository;

import com.example.clients.review.ReviewResponse;
import com.example.common.BaseRepository;
import com.example.review.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReviewRepository extends BaseRepository<Review> {

    @Query("SELECT AVG(r.rating) FROM Review r " +
            "WHERE r.itemId=?1")
    Double getAverageRatingByItemId(String itemId);

    @Query("SELECT COUNT(r.id) FROM Review r " +
            "WHERE r.userId=?1 " +
            "AND r.active=true")
    Integer getCountByUserId(String userId);

    @Query("SELECT DISTINCT NEW com.example.clients.review.ReviewResponse("+
            "r.userId, COUNT(r.id))   " +
            "FROM Review r " +
            "WHERE r.userId IN ?1 AND r.active=true " +
            "GROUP BY r.userId")
    List<ReviewResponse> getActiveReviewsCountByUsersIds(String[] authors);

    //    https://stackoverflow.com/a/46013654/548473
    @Override
    @EntityGraph(attributePaths = "likes", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT r FROM Review r " +
            "ORDER BY r.createdAt DESC")
    Page<Review> findAll(Pageable pageable);

    Review getFirstByUserId(String userId);

    Review getFirstByItemId(String itemId);

    @Transactional
    void deleteAllByUserId(String userId);

    @Transactional
    void deleteAllByItemId(String itemId);

    @EntityGraph(attributePaths = "likes", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT r FROM Review r " +
            "WHERE lower(r.title) LIKE lower(concat('%', :filter,'%')) " +
            "OR lower(r.description) LIKE lower(concat('%', :filter,'%')) " +
            "ORDER BY r.createdAt DESC")
    Page<Review> findAllWithFilter(Pageable pageable, String filter);

    @Query("SELECT r FROM Review r " +
            "WHERE r.itemId=?1 " +
            "AND r.active=true " +
            "ORDER BY r.createdAt DESC")
    List<Review> findAllByItemId(String id);

    @EntityGraph(attributePaths = "likes", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT r FROM Review r " +
            "WHERE r.userId=?1")
    List<Review> findAllByUserId(String userId);

    @EntityGraph(attributePaths = "likes", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT r FROM Review r " +
            "WHERE r.userId=?1 AND r.active=true")
    List<Review> findAllActiveByUserId(String userId);

    @EntityGraph(attributePaths = "likes", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT r FROM Review r " +
            "WHERE r.itemId=?1 " +
            "AND r.active=true " +
            "ORDER BY r.createdAt DESC")
    Page<Review> findAllByItemId(Pageable pageable, String id);

    @EntityGraph(attributePaths = "likes", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT r FROM Review r " +
            "WHERE r.itemId=:id " +
            "AND r.active=true " +
            "AND (lower(r.title) LIKE lower(concat('%', :filter,'%')) " +
            "OR lower(r.description) LIKE lower(concat('%', :filter,'%'))) " +
            "ORDER BY r.createdAt DESC")
    Page<Review> findAllByItemIdFiltered(Pageable pageable, String id, String filter);
}