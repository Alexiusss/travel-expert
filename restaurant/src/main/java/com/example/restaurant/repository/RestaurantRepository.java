package com.example.restaurant.repository;

import com.example.common.BaseRepository;
import com.example.restaurant.model.Restaurant;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends BaseRepository<Restaurant> {
}