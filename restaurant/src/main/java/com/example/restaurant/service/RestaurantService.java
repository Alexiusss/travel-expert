package com.example.restaurant.service;

import com.example.clients.auth.AuthCheckResponse;
import com.example.clients.auth.AuthClient;
import com.example.common.util.ValidationUtil;
import com.example.restaurant.model.Restaurant;
import com.example.restaurant.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;

import static com.example.common.util.ValidationUtil.assureIdConsistent;
import static com.example.common.util.ValidationUtil.checkNew;

@Service
@AllArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final AuthClient authClient;
    private final Environment environment;

    public Restaurant create(Restaurant restaurant) {
        Assert.notNull(restaurant, "restaurant must not be null");
        checkNew(restaurant);
        return restaurantRepository.save(restaurant);
    }

    public Page<Restaurant> findAllPaginated(Pageable pageable, String filter) {
        if (filter.isEmpty()) {
            return restaurantRepository.findAll(pageable);
        }
        return restaurantRepository.findAllByName(pageable, filter);
    }

    public Restaurant get(String id) {
        return restaurantRepository.getExisted(id);
    }

    public Restaurant getByName(String name) {
        String restaurantName = name.replaceAll("_", " ");
        return ValidationUtil.checkNotFoundWithName(restaurantRepository.findByName(restaurantName), restaurantName);
    }

    public void delete(String id) {
        restaurantRepository.deleteExisted(id);
    }

    public ResponseEntity<Restaurant> checkAuth(String authorization) {
        AuthCheckResponse authCheckResponse = authClient.isAuth(authorization);
        if (!authCheckResponse.getAuthorities().contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return null;
    }

    @Transactional
    public void update(String id, Restaurant restaurant) {
        assureIdConsistent(restaurant, restaurant.id());
        Restaurant restaurantFromDB = restaurantRepository.getExisted(id);
        restaurantFromDB.setAddress(restaurant.getAddress());
        restaurantFromDB.setEmail(restaurant.getEmail());
        restaurantFromDB.setName(restaurant.getName());
        restaurantFromDB.setPhoneNumber(restaurant.getPhoneNumber());
        restaurantFromDB.setWebsite(restaurant.getWebsite());
        restaurantFromDB.setCuisine(restaurant.getCuisine());
        restaurantFromDB.setFileNames(restaurant.getFileNames());
    }

    public boolean currentProfileName(String envName) {
        return Arrays.asList(environment.getActiveProfiles()).contains(envName);
    }
}