package com.example.hotel.service;

import com.example.clients.auth.AuthCheckResponse;
import com.example.clients.auth.AuthClient;
import com.example.hotel.model.Hotel;
import com.example.hotel.repository.HotelRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static com.example.common.util.ValidationUtil.checkNew;

@Service
@AllArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final AuthClient authClient;

    public Hotel get(String id) {
        return hotelRepository.getExisted(id);
    }

    public List<Hotel> findAll(){
        return hotelRepository.findAll();
    }

    public Hotel create(Hotel hotel) {
        Assert.notNull(hotel, "restaurant must not be null");
        checkNew(hotel);
        return hotelRepository.save(hotel);
    }

    public void delete(String id) {
        hotelRepository.deleteExisted(id);
    }

    public ResponseEntity<Hotel> checkAuth(String authorization) {
        AuthCheckResponse authCheckResponse = authClient.isAuth(authorization);
        if (!authCheckResponse.getAuthorities().contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return null;
    }
}