package com.example.image.util;

import lombok.experimental.UtilityClass;
import org.assertj.core.util.Arrays;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

@UtilityClass
public class ImageTestData {

    public static MockMultipartFile getImage() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return new MockMultipartFile("files", "image1.jpg", String.valueOf(MediaType.IMAGE_JPEG), loader.getResourceAsStream("/images/image1.jpeg"));
    }
}