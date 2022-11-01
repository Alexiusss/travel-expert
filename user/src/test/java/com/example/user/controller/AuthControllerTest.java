package com.example.user.controller;

import com.example.user.model.User;
import com.example.user.model.dto.AuthRequest;
import com.example.user.model.dto.RegistrationDTO;
import com.example.user.repository.UserRepository;
import com.example.common.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.example.user.controller.UserExceptionHandler.EXCEPTION_DUPLICATE_EMAIL;
import static com.example.user.util.UserTestData.*;
import static org.hamcrest.Matchers.equalTo;
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
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", equalTo("Bad credentials")))
                .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));
    }

    @Test
    void registerAndActivate() throws Exception {
        RegistrationDTO newRegistration = getNewRegistration();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newRegistration, newRegistration.getPassword())))
                .andExpect(status().isCreated());

        User registeredUser = USER_MATCHER.readFromJson(action);

        perform(MockMvcRequestBuilders.post(REST_URL + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(new AuthRequest(newRegistration.getEmail(), newRegistration.getPassword()))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", equalTo("User is disabled")));

        perform(MockMvcRequestBuilders.get(REST_URL + "activate/" + registeredUser.getActivationCode()))
                .andExpect(status().isOk())
                .andExpect(content().string("User successfully activated"));

        String newId = registeredUser.getId();
        User userFromDB = userRepository.getById(newId);

        assertTrue(userFromDB.isEnabled());
        assertNull(userFromDB.getActivationCode());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void registerDuplicateEmail() throws Exception {
        RegistrationDTO duplicateRegistration = getNewRegistration();
        duplicateRegistration.setEmail("user@gmail.com");

        perform(MockMvcRequestBuilders.post(REST_URL + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(duplicateRegistration, duplicateRegistration.getPassword())))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message", equalTo(EXCEPTION_DUPLICATE_EMAIL)));
    }

    @Test
    void registerInvalid() throws Exception {
        RegistrationDTO newRegistration = getNewRegistration();
        newRegistration.setPassword("");

        perform(MockMvcRequestBuilders.post(REST_URL + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newRegistration, newRegistration.getPassword())))
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