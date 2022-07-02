package com.example.restaurant_advisor_react.controller;

import com.example.restaurant_advisor_react.model.User;
import com.example.restaurant_advisor_react.model.dto.AuthRequest;
import com.example.restaurant_advisor_react.repository.UserRepository;
import com.example.restaurant_advisor_react.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.restaurant_advisor_react.util.UserTestData.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerTest extends AbstractControllerTest {

    private static final String REST_URL = AuthController.AUTH_URL + "/";

    @Autowired
    private UserRepository userRepository;

    @Test
    void login() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(new AuthRequest(ADMIN_MAIL, ADMIN.getPassword()))))
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(status().isOk());
    }

    @Test
    void invalidLogin() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(new AuthRequest("", ""))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));
    }

    @Test
    void failLogin() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(new AuthRequest(ADMIN_MAIL, "InvalidPassword"))))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));

    }

    @Test
    void registerAndActivate() throws Exception {
        User newUser = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUser, newUser.getPassword())))
                .andExpect(status().isCreated())
                .andDo(print());

        User registeredUser = USER_MATCHER.readFromJson(action);

        perform(MockMvcRequestBuilders.get(REST_URL + "activate/" + registeredUser.getActivationCode()))
                .andExpect(status().isOk())
                .andExpect(content().string("User successfully activated"));

        String newId = registeredUser.getId();
        int newVersion = registeredUser.getVersion();
        newUser.setId(newId);
        newUser.setVersion(newVersion);
        User userFromDB = userRepository.getById(newId);
        newUser.setEnabled(userFromDB.isEnabled());

        USER_MATCHER.assertMatch(userFromDB, newUser);
        assertTrue(userFromDB.isEnabled());
        assertNull(userFromDB.getActivationCode());
    }

    @Test
    void registerInvalid() throws Exception {
        User newUser = getNew();
        newUser.setPassword("");

        perform(MockMvcRequestBuilders.post(REST_URL + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUser, newUser.getPassword())))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void activateInvalid() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "activate/" + "InvalidCode"))
                .andExpect(status().isOk())
                .andExpect(content().string("Activation code not found!"));
    }


    @Test
    void logout() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("logout"))
                .andExpect(header().exists(HttpHeaders.SET_COOKIE));
    }
}