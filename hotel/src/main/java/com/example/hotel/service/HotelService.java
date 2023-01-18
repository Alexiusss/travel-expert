package com.example.hotel.service;

import com.example.hotel.model.Hotel;
import com.example.hotel.repository.HotelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static com.example.common.util.ValidationUtil.checkNew;

@Service
@AllArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    public List<Hotel> findAll(){
        return hotelRepository.findAll();
    }

    public Hotel create(Hotel hotel) {
        Assert.notNull(hotel, "restaurant must not be null");
        checkNew(hotel);
        return hotelRepository.save(hotel);
    }
}