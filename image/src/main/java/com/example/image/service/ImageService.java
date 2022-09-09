package com.example.image.service;

import com.example.image.model.Image;
import com.example.image.repository.ImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.example.image.util.ImageUtil.getDefaultImage;

@Service
@AllArgsConstructor
public class ImageService {

    private ImageRepository imageRepository;

    public Image findImageByFileName(String fileName) throws IOException {
       Image image = imageRepository.findByFileName(fileName);
       if (image == null) {
           image = getDefaultImage();
       }
       return image;
    }

    public Image save(Image image) {
        return imageRepository.save(image);
    }
}