package com.example.restaurant.service;

import com.example.clients.auth.AuthCheckResponse;
import com.example.clients.auth.AuthClient;
import com.example.restaurant.model.Restaurant;
import com.example.restaurant.repository.RestaurantRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final AuthClient authClient;


    public Restaurant create(Restaurant restaurant) {
        Assert.notNull(restaurant, "restaurant must not be null");
       return restaurantRepository.save(restaurant);
    }

    public Page<Restaurant> findAllPaginated(Pageable pageable) {
        return restaurantRepository.findAll(pageable);
    }

    public Optional<Restaurant> get(String id) {
        return restaurantRepository.findById(id);
    }

    public void delete(String id) {
        restaurantRepository.deleteById(id);
    }

    public ResponseEntity<Restaurant> checkAuth(String authorization) {
        AuthCheckResponse authCheckResponse;
        try {
            authCheckResponse = authClient.isAuth(authorization);
        } catch (FeignException.Unauthorized e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!authCheckResponse.getAuthorities().contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return null;
    }

    @Transactional
    public void update(String id, Restaurant restaurant) {
        Restaurant restaurantFromDB = restaurantRepository.findById(id).get();
        restaurantFromDB.setAddress(restaurant.getAddress());
        restaurantFromDB.setEmail(restaurant.getEmail());
        restaurantFromDB.setName(restaurant.getName());
        restaurantFromDB.setPhone_number(restaurant.getPhone_number());
        restaurantFromDB.setWebsite(restaurant.getWebsite());
        restaurantFromDB.setCuisine(restaurant.getCuisine());
    }
}