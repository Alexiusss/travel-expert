package com.example.image.util;

import com.example.image.model.Image;
import lombok.experimental.UtilityClass;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;

@UtilityClass
public class ImageUtil {

    public static Image getDefaultImage() throws IOException {
        InputStream inputStream = getResourceFileAsInputStream();
        byte[] data = FileCopyUtils.copyToByteArray(inputStream);
        Image image = new Image();
        image.setFileType("image/jpeg");
        image.setData(data);
        return image;
    }

    private static InputStream getResourceFileAsInputStream() {
        ClassLoader classLoader = Image.class.getClassLoader();
        return classLoader.getResourceAsStream("images/not_found_image.jpg");
    }
}