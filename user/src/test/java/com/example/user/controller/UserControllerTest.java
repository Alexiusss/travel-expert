package com.example.user.controller;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.example.common.error.ModificationRestrictionException.EXCEPTION_MODIFICATION_RESTRICTION;
import static com.example.common.util.JsonUtil.asParsedJson;
import static com.example.user.controller.UserExceptionHandler.EXCEPTION_DUPLICATE_EMAIL;
import static com.example.user.controller.UserExceptionHandler.EXCEPTION_DUPLICATE_USERNAME;
import static com.example.user.util.UserTestData.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest extends AbstractControllerTest {

    private static final String REST_URL = UserController.REST_URL + "/";

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void getAllPaginated() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .param("size", "2")
                .param("page", "1"))
                .andExpect(jsonPath("$.content[0]", equalTo(asParsedJson(USER))));
    }

    @Test
    void getAllUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(USER_MATCHER.contentJson(ADMIN));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAuthor() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + USER_ID + "/author"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(AUTHOR_MATCHER.contentJson(AUTHOR));
    }

    @Test
    void getAuthorList() throws Exception {
        stubReviewResponse();
        perform(MockMvcRequestBuilders.post(REST_URL + "authorList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Arrays.toString(new String[]{USER_ID})))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(AUTHOR_MATCHER.contentJson(List.of(AUTHOR)));
    }

    @Test
    void getByAuthorUsername() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + USERNAME + "/authorByUsername"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(AUTHOR_MATCHER.contentJson(AUTHOR));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND_ID))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", equalTo(NOT_FOUND_MESSAGE)));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + USER_ID)
                .with(csrf()))
                .andExpect(status().isNoContent());
        assertFalse(userRepository.findById(USER_ID).isPresent());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void create() throws Exception {
        User newUser = getNewUser();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUser, newUser.getPassword()))
                .with(csrf()))
                .andExpect(status().isCreated());

        User created = USER_MATCHER.readFromJson(action);
        String newId = created.getId();
        int newVersion = created.getVersion();
        newUser.setId(newId);
        newUser.setVersion(newVersion);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(userRepository.getById(newId), newUser);
    }

    @Test
    void createUnAuth() throws Exception {
        User newUser = getNewUser();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUser, newUser.getPassword()))
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    // https://github.com/spring-projects/spring-boot/issues/5993#issuecomment-221550622
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @WithUserDetails(ADMIN_MAIL)
    void createWithDuplicateEmail() throws Exception {
        User newUser = getNewUser();
        newUser.setEmail(USER_MAIL);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUser, newUser.getPassword()))
                .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message", equalTo(EXCEPTION_DUPLICATE_EMAIL)));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @WithUserDetails(ADMIN_MAIL)
    void createWithDuplicateUsername() throws Exception {
        User newUser = getNewUser();
        newUser.setUsername(USERNAME);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUser, newUser.getPassword()))
                .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message", equalTo(EXCEPTION_DUPLICATE_USERNAME)));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void createInvalid() throws Exception {
        User invalidUser = getNewUser();
        invalidUser.setEmail("");
        invalidUser.setFirstName("");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(invalidUser, invalidUser.getPassword()))
                .with(csrf()))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void updateInvalid() throws Exception {
        User invalidUser = USER;
        invalidUser.setFirstName("");
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(invalidUser, invalidUser.getPassword()))
                .with(csrf()))
                .andExpect(status().isUnprocessableEntity());

    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void updateForbidden() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + ADMIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(ADMIN, "newAminPassword"))
                .with(csrf()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", equalTo(EXCEPTION_MODIFICATION_RESTRICTION)));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void deleteForbidden() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + ADMIN_ID)
                .with(csrf()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", equalTo(EXCEPTION_MODIFICATION_RESTRICTION)));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void disableForbidden() throws Exception{
        perform(MockMvcRequestBuilders.patch(REST_URL + ADMIN_ID)
                .with(csrf())
                .param("enable", "false"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", equalTo(EXCEPTION_MODIFICATION_RESTRICTION)));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void enable() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + USER_ID)
                .with(csrf())
                .param("enable", "false"))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.getById(USER_ID).isEnabled(), "false");
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void update() throws Exception {
        User updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isOk());

        USER_MATCHER.assertMatch(userRepository.getById(USER_ID), getUpdated());
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void subscribe() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "subscribe/" + MODER_ID))
                .andExpect(status().isOk());

        Set<String> adminSubscriptions = userRepository.findByIdWithSubscriptions(USER_ID).get().getSubscriptions();
        assertTrue(adminSubscriptions.contains(MODER_ID));

        Set<String> userSubscribers = userRepository.findByIdWithSubscriptions(MODER_ID).get().getSubscribers();
        assertTrue(userSubscribers.contains(USER_ID));
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void unSubscribe() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "unSubscribe/" + ADMIN_ID))
                .andExpect(status().isOk());

        Set<String> userSubscriptions = userRepository.findByIdWithSubscriptions(USER_ID).get().getSubscriptions();
        assertFalse(userSubscriptions.contains(ADMIN_ID));

        Set<String> adminSubscribers = userRepository.findByIdWithSubscriptions(ADMIN_ID).get().getSubscribers();
        assertFalse(adminSubscribers.contains(USER_ID));
    }
}