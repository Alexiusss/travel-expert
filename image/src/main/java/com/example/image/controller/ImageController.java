package com.example.image.controller;

import com.example.image.model.Image;
import com.example.image.service.ImageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = ImageController.REST_URL, produces = APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class ImageController {

    static final String REST_URL = "/api/v1/images";

    private ImageService imageService;

    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) throws Exception {
        Image image = imageService.findImageByFileName(fileName);
        return ResponseEntity.ok().contentType(MediaType.valueOf(image.getFileType())).body(image.getData());
    }
}