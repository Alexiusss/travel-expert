package com.example.image.service;

import com.example.image.model.Image;
import com.example.image.repository.ImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public Image save(MultipartFile file) throws IOException  {
        Image image = new Image(null, null, null, 0, file.getOriginalFilename(), file.getContentType(), file.getSize(), file.getBytes());
        return imageRepository.save(image);
    }

    public void deleteByFileName(String fileName) {
        imageRepository.deleteByFileName(fileName);
    }

    public List<String> uploadImages(MultipartFile[] files) {
        return Arrays.stream(files).map((MultipartFile file) -> {
            try {
                return save(file).getFileName();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }
}