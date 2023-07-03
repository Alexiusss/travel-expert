package com.example.image.service;

import com.example.clients.auth.AuthCheckResponse;
import com.example.clients.auth.AuthClient;
import com.example.image.error.FileNameException;
import com.example.image.model.Image;
import com.example.image.repository.ImageRepository;
import lombok.AllArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.image.util.ImageUtil.getDefaultImage;

@Service
@AllArgsConstructor
public class ImageService {

    private ImageRepository imageRepository;
    private final AuthClient authClient;
    private final Environment env;

    public Image findImageByFileName(String fileName) throws IOException {
       Image image = imageRepository.findByFileName(fileName);
       if (image == null) {
           image = getDefaultImage();
       }
       return image;
    }

    public List<String> uploadImages(MultipartFile[] files, String userId) {
        return Arrays.stream(files).map((MultipartFile file) -> {
            try {
                return save(file, userId).getFileName();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }

    public Image save(MultipartFile file, String userId) throws IOException  {
        String newFileName = generateFileName(file.getOriginalFilename());
        Image image = new Image(null, null, null, 0, newFileName, file.getContentType(), file.getSize(), file.getBytes(), userId);
        return imageRepository.save(image);
    }

    public String generateFileName(String fileName) {
        String cleanPath = StringUtils.cleanPath(fileName);
        if (cleanPath.contains("..")) {
            throw new FileNameException("Filename contains invalid path sequence " + cleanPath);
        }
        String randomNumber = generateNumber();
        String currentDateToAsString = DateTime.now().toString("HHmmss_ddMMyyyy");
        return currentDateToAsString + "_" + randomNumber + "_" + cleanPath;
    }

    public String generateNumber() {
        int min = 1000;
        int max = 999999;
        int random_int = (int) (Math.random() * (max - min + 1) + min);
        return "" + random_int;
    }

    public void deleteByFileName(String fileName) {
        imageRepository.deleteByFileName(fileName);
    }

    public void deleteAllByFileName(String[] fileNames, String action) {
        if (Objects.equals(action, "delete")) {
            imageRepository.deleteAllByFileName(fileNames);
        }
    }

    public ResponseEntity<?> checkAuth(String authorization) {
        AuthCheckResponse authCheckResponse = authClient.isAuth(authorization);

        if ((!authCheckResponse.getAuthorities().contains("ADMIN") &&
                !authCheckResponse.getAuthorities().contains("MODERATOR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok().build();
    }

    public boolean currentProfileName(String profileName) {
        return Arrays.asList(env.getActiveProfiles()).contains(profileName);
    }
}