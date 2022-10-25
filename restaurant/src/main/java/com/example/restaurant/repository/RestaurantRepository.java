package com.example.restaurant.repository;

import com.example.common.BaseRepository;
import com.example.restaurant.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends BaseRepository<Restaurant> {
    //  https://stackoverflow.com/a/69678361
    @Query("SELECT r FROM Restaurant r WHERE" +
            " lower(r.name) LIKE lower(concat('%', :name,'%'))")
    Page<Restaurant> findAllByName(Pageable pageable, String name);

    Optional<Restaurant> findByName(String name);
}