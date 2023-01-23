package com.example.hotel.service;

import com.example.clients.auth.AuthCheckResponse;
import com.example.clients.auth.AuthClient;
import com.example.hotel.model.Hotel;
import com.example.hotel.repository.HotelRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

import static com.example.common.util.ValidationUtil.assureIdConsistent;
import static com.example.common.util.ValidationUtil.checkNew;

@Service
@AllArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final AuthClient authClient;

    public Hotel get(String id) {
        return hotelRepository.getExisted(id);
    }

    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    public Hotel create(Hotel hotel) {
        Assert.notNull(hotel, "restaurant must not be null");
        checkNew(hotel);
        return hotelRepository.save(hotel);
    }

    @Transactional
    public void update(Hotel hotel, String id) {
        assureIdConsistent(hotel, id);
        Hotel hotelFromDB = hotelRepository.getExisted(id);
        hotelFromDB.setName(hotel.getName());
        hotelFromDB.setAddress(hotel.getAddress());
        hotelFromDB.setEmail(hotel.getEmail());
        hotelFromDB.setPhoneNumber(hotel.getPhoneNumber());
        hotelFromDB.setWebsite(hotel.getWebsite());
        hotelFromDB.setHotelClass(hotel.getHotelClass());
        hotelFromDB.setDescription(hotel.getDescription());
        hotelFromDB.setRoomFeatures(hotel.getRoomFeatures());
        hotelFromDB.setRoomTypes(hotel.getRoomTypes());
        hotelFromDB.setServicesAndFacilitates(hotel.getServicesAndFacilitates());
        hotelFromDB.setLanguagesUsed(hotel.getLanguagesUsed());
        hotelFromDB.setHotelStyle(hotel.getHotelStyle());
        hotelFromDB.setFileNames(hotel.getFileNames());

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