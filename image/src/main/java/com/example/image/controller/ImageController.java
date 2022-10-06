package com.example.image.controller;

import com.example.image.model.Image;
import com.example.image.service.ImageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("files") MultipartFile[] files) throws Exception {
        List<String> images = imageService.uploadImages(files);
        return ResponseEntity.ok(images);
    }

    @DeleteMapping("/{fileName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String fileName) throws Exception {
        imageService.deleteByFileName(fileName);
    }

    // https://docs.oracle.com/en/cloud/saas/marketing/responsys-develop/API/REST/api-v1.3-lists-listName-members-post-actionDelete.htm
    @PostMapping("/fileNames")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllByFileNames(@RequestBody String[] fileNames, @RequestParam("action") String action) throws Exception {
        imageService.deleteAllByFileName(fileNames, action);
    }
}