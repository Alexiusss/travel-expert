package com.example.restaurant_advisor_react.controller;

import com.example.restaurant_advisor_react.model.dto.AuthRequest;
import com.example.restaurant_advisor_react.util.JsonUtil;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.restaurant_advisor_react.util.UserTestData.ADMIN;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerTest extends AbstractControllerTest {

    private static final String REST_URL = AuthController.AUTH_URL + "/";

    @Test
    void login() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(new AuthRequest(ADMIN.getEmail(), ADMIN.getPassword()))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void invalidLogin() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(new AuthRequest("", ""))))
                .andExpect(status().isUnprocessableEntity());
    }
}