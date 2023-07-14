package com.example.image.controller.kc;

import com.example.image.controller.CommonImageControllerTest;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.example.common.test.KeycloakTestData.stubKeycloak;
import static com.example.common.test.TestJwtTokenGenerator.generateAccessToken;
import static com.example.common.test.TestJwtTokenGenerator.generateJwk;
import static com.example.common.util.JsonUtil.writeValue;
import static com.example.image.util.ImageTestData.USER_ID;
import static com.example.image.util.ImageTestData.getNewImage;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("kc")
public class KcImageControllerTest extends CommonImageControllerTest {

    private static RsaJsonWebKey rsaJsonWebKey;
    private static String userAccessToken;
    private static String moderAccessToken;
    private static String adminAccessToken;

    @BeforeAll
    static void init() throws JoseException {
        rsaJsonWebKey = generateJwk();
        userAccessToken = generateAccessToken(rsaJsonWebKey, List.of("USER"));
        moderAccessToken = generateAccessToken(rsaJsonWebKey, List.of("MODERATOR","USER"));
        adminAccessToken = generateAccessToken(rsaJsonWebKey, List.of("ADMIN", "USER"));

        WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().port(8180));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8180);
    }

    @Test
    void upload() throws Exception {
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.multipart(REST_URL)
                .file(getNewImage("image3.jpeg"))
                .param("userid", USER_ID)
                .header("Authorization", String.format("Bearer %s", userAccessToken)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]", containsString(getNewImage("image1.jpeg").getOriginalFilename())));
    }

    @Test
    void delete() throws Exception {
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.delete(REST_URL + "image3.jpg")
                .header("Authorization", String.format("Bearer %s", moderAccessToken)))
                .andExpect(status().isNoContent());

        assertNull(imageRepository.findByFileName("image3.jpg"));
    }

    @Test
    void deleteAllByFileNames() throws Exception {
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.post(REST_URL + "fileNames")
                .header("Authorization", String.format("Bearer %s", adminAccessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(List.of("image1.jpg", "image2.jpg")))
                .param("action", "delete"))
                .andExpect(status().isNoContent());

        assertEquals(List.of(), imageRepository.findAll());
    }

    @Test
    void deleteForbidden() throws Exception{
        perform(MockMvcRequestBuilders.post(REST_URL + "fileNames")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(List.of("image1.jpg", "image2.jpg")))
                .param("action", "delete"))
                .andExpect(status().isForbidden());
    }
}