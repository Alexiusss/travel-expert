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

    @Query("SELECT r FROM Review r " +
            "WHERE lower(r.title) LIKE lower(concat('%', :filter,'%')) " +
            "OR lower(r.description) LIKE lower(concat('%', :filter,'%')) ")
    Page<Review> findAllWithFilter(Pageable pageable, String filter);

    @Query("SELECT r FROM Review r " +
            "WHERE r.itemId=?1 " +
            "AND r.active=true")
    List<Review> findAllByItemId(String id);

    @Query("SELECT r FROM Review r " +
            "WHERE r.itemId=?1 " +
            "AND r.active=true")
    Page<Review> findAllByItemId(Pageable pageable, String id);

    @Query("SELECT r FROM Review r " +
            "WHERE r.itemId=:id " +
            "AND r.active=true " +
            "AND (lower(r.title) LIKE lower(concat('%', :filter,'%')) " +
            "OR lower(r.description) LIKE lower(concat('%', :filter,'%'))) ")
    Page<Review> findAllByItemIdFiltered(Pageable pageable, String id, String filter);
}