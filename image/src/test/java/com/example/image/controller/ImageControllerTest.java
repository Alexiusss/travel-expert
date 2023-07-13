package com.example.image.controller;

import com.example.common.util.TestProfileResolver;
import com.example.image.repository.ImageRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static com.example.common.util.JsonUtil.writeValue;
import static com.example.image.util.ImageTestData.*;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles(resolver = TestProfileResolver.class)
@Sql(value = {"/data.sql"})
public class ImageControllerTest {

    private static final String REST_URL = ImageController.REST_URL + "/";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ImageRepository imageRepository;

    private ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
    }

    @BeforeAll
    static void init() {
        WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().port(7077));
        wireMockServer.start();
        WireMock.configureFor("localhost", 7077);
    }

    @Test
    void getImage() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "image1.jpg"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG))
                .andExpect(status().isOk());
    }

    @Test
    void getNotFoundImage() throws Exception{
        perform(MockMvcRequestBuilders.get(REST_URL + "NotFound"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG));
    }

    @Test
    void upload() throws Exception {
        perform(MockMvcRequestBuilders.multipart(REST_URL)
                .file(getNewImage("image3.jpeg"))
                .param("userid", USER_ID))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]", containsString(getNewImage("image1.jpeg").getOriginalFilename())));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "image3.jpg"))
                .andExpect(status().isNoContent());

        assertNull(imageRepository.findByFileName("image3.jpg"));
    }

    @Test
    void deleteAllByFileNames() throws Exception {
        stubAdminAuth();
        perform(MockMvcRequestBuilders.post(REST_URL + "fileNames")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(List.of("image1.jpg", "image2.jpg")))
                .param("action", "delete"))
                .andExpect(status().isNoContent());

        assertEquals(List.of(), imageRepository.findAll());
    }

    @Test
    void deleteForbidden() throws Exception{
        stubUnAuth();
        perform(MockMvcRequestBuilders.post(REST_URL + "fileNames")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(List.of("image1.jpg", "image2.jpg")))
                .param("action", "delete"))
                .andExpect(status().isForbidden());
    }
}