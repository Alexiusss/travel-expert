package com.example.hotel.repository;

import com.example.common.BaseRepository;
import com.example.hotel.model.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends BaseRepository<Hotel> {
    @Query("SELECT h from Hotel h " +
            "WHERE lower(h.name) LIKE lower(concat('%', :name,'%'))")
    Page<Hotel> findAllByName(Pageable pageable, String name);
}