package com.example.review.repository;

import com.example.common.BaseRepository;
import com.example.review.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends BaseRepository<Review> {

    @Query("SELECT AVG(r.rating) FROM Review r " +
            "WHERE r.itemId=?1")
    Integer getRatingByItemId(String itemId);

    @Query("SELECT COUNT(r.id) FROM Review r " +
            "WHERE r.userId=?1 " +
            "AND r.active=true")
    Integer getCountByUserId(String userId);

    @Override
    @Query("SELECT r FROM Review r " +
            "ORDER BY r.createdAt DESC")
    Page<Review> findAll(Pageable pageable);

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

    @Query("SELECT r FROM Review r " +
            "WHERE r.itemId=?1 " +
            "AND r.active=true " +
            "ORDER BY r.createdAt DESC")
    Page<Review> findAllByItemId(Pageable pageable, String id);

    @Query("SELECT r FROM Review r " +
            "WHERE r.itemId=:id " +
            "AND r.active=true " +
            "AND (lower(r.title) LIKE lower(concat('%', :filter,'%')) " +
            "OR lower(r.description) LIKE lower(concat('%', :filter,'%'))) " +
            "ORDER BY r.createdAt DESC")
    Page<Review> findAllByItemIdFiltered(Pageable pageable, String id, String filter);
}