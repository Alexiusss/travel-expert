package com.example.image.util;

import com.example.clients.auth.AuthCheckResponse;
import com.example.common.util.JsonUtil;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.experimental.UtilityClass;
import org.assertj.core.util.Arrays;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@UtilityClass
public class ImageTestData {

    public static final AuthCheckResponse AUTH_ADMIN_RESPONSE = new AuthCheckResponse("1", List.of("ADMIN", "MODERATOR", "USER"));
    public static final AuthCheckResponse UN_AUTH_RESPONSE = new AuthCheckResponse("", List.of());

    public static void stubAdminAuth() {
        stubFor(WireMock.get(urlMatching("/api/v1/auth/validate"))
                .willReturn(okForContentType("application/json", JsonUtil.writeValue(AUTH_ADMIN_RESPONSE))));
    }

    public static void stubUnAuth() {
        stubFor(WireMock.get(urlMatching("/api/v1/auth/validate"))
                .willReturn(okForContentType("application/json",JsonUtil.writeValue(UN_AUTH_RESPONSE))));
    }

    public static MockMultipartFile getNewImage(String fileName) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return new MockMultipartFile("files", "image1.jpg", String.valueOf(MediaType.IMAGE_JPEG), loader.getResourceAsStream("/images/image1.jpeg"));
    }
}