package com.example.image.controller;

import com.example.image.model.Image;
import com.example.image.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation(summary = "Get an image by its id")
    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) throws Exception {
        Image image = imageService.findImageByFileName(fileName);
        return ResponseEntity.ok().contentType(MediaType.valueOf(image.getFileType())).body(image.getData());
    }

    @Operation(summary = "Upload the image set")
    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("files") MultipartFile[] files) throws Exception {
        List<String> images = imageService.uploadImages(files);
        return ResponseEntity.ok(images);
    }

    @Operation(summary = "Delete a picture by its id")
    @DeleteMapping("/{fileName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String fileName) throws Exception {
        imageService.deleteByFileName(fileName);
    }

    @Operation(summary = "Delete a group of pictures per ids", description = "A JWT token is required to access this API")
    @SecurityRequirement(name = "Bearer Authentication")
    // https://docs.oracle.com/en/cloud/saas/marketing/responsys-develop/API/REST/api-v1.3-lists-listName-members-post-actionDelete.htm
    @PostMapping("/fileNames")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAllByFileNames(@RequestHeader(name = "Authorization", defaultValue = "empty") String authorization,
                                     @RequestBody String[] fileNames,
                                     @RequestParam("action") String action) throws Exception {
        ResponseEntity<?> isProhibited = imageService.checkAuth(authorization);
        if (!isProhibited.getStatusCode().is2xxSuccessful()) return isProhibited;

        imageService.deleteAllByFileName(fileNames, action);
        return ResponseEntity.noContent().build();
    }
}