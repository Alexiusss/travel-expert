package com.example.review.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.example.review.util.ReviewTestData.stubAdminAuth;
import static com.example.review.util.ReviewTestData.stubUnAuth;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReviewControllerTest {

    private static final String REST_URL = ReviewController.REST_URL + "/";

    @Autowired
    private MockMvc mockMvc;

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
    void getAll() throws Exception {
        stubAdminAuth();
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void getAllForBidden() throws Exception {
        stubUnAuth();
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}