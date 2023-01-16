package com.example.hotel.repository;

import com.example.common.BaseRepository;
import com.example.hotel.model.Hotel;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends BaseRepository<Hotel> {
}