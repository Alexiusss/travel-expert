package com.example.hotel.service;

import com.example.hotel.model.Hotel;
import com.example.hotel.repository.HotelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    public List<Hotel> findAll(){
        return hotelRepository.findAll();
    }
}