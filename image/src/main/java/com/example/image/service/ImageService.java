package com.example.image.service;

import com.example.image.model.Image;
import com.example.image.repository.ImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ImageService {

    private ImageRepository imageRepository;

    public Image findImageByFileName(String fileName) {
        return imageRepository.findByFileName(fileName);
    }
}