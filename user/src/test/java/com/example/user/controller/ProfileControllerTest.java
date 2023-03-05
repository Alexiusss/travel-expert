package com.example.user.controller;


import com.example.user.model.User;
import com.example.user.servise.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.user.util.UserTestData.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProfileControllerTest extends AbstractControllerTest {

    private static final String REST_URL = ProfileController.REST_URL + '/';

    @Autowired
    UserService userService;

    @Test
    @WithUserDetails(MODER_MAIL)
    void get() throws Exception {
        ResultActions action = perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk());

        User authUser = USER_MATCHER.readFromJson(action);
        USER_MATCHER.assertMatch(authUser, MODER);
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    //  https://stackoverflow.com/questions/38330597/inject-authenticationprincipal-when-unit-testing-a-spring-rest-controller
    @Test
    void update() throws Exception {
        String accessToken = obtainAccessToken(USER_MAIL, USER.getPassword());

        User updatedUser = USER;
        updatedUser.setFirstName("Updated name");
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithPassword(updatedUser, updatedUser.getPassword()))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk());

        USER_MATCHER.assertMatch(userService.get(USER_ID), updatedUser);
    }

    @Test
    void updateInvalid() throws Exception {
        String accessToken = obtainAccessToken(USER_MAIL, USER.getPassword());

        User updatedUser = USER;
        updatedUser.setFirstName("");
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .content(jsonWithPassword(updatedUser, updatedUser.getPassword())))
                .andExpect(status().isUnprocessableEntity());
    }
}