package com.example.user.controller;


import com.example.user.model.User;
import com.example.user.servise.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.user.util.UserTestData.*;
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

    @Test
    @WithUserDetails(USER_MAIL)
    void update() throws Exception {
        User updatedUser = USER;
        updatedUser.setFirstName("Updated name");
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithPassword(updatedUser, updatedUser.getPassword())))
                .andExpect(status().isOk());

        USER_MATCHER.assertMatch(userService.get(USER_ID), updatedUser);
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void updateInvalid() throws Exception {
        User updatedUser = USER;
        updatedUser.setFirstName("");
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updatedUser, updatedUser.getPassword())))
                .andExpect(status().isUnprocessableEntity());
    }
}