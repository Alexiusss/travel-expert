package com.example.hotel.controller;

import com.example.common.util.JsonUtil;
import com.example.hotel.model.Hotel;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.example.hotel.controller.HotelExceptionHandler.EXCEPTION_DUPLICATE_EMAIL;
import static com.example.hotel.util.HotelTestData.*;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = {"/data.sql"})
class HotelControllerTest {

    private static final String REST_URL = HotelController.REST_URL + "/";

    @Autowired
    private MockMvc mockMvc;

    private ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
    }

    @BeforeAll
    static void init() {
        WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().port(7070));
        wireMockServer.start();
        WireMock.configureFor("localhost", 7070);
    }

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + HOTEL_1_ID))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(HOTEL_MATCHER.contentJson(HOTEL_1));
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND_ID))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", equalTo(NOT_FOUND_MESSAGE)));
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void create() throws Exception {
        stubAuth();
        Hotel newHotel = getNew();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newHotel)))
                .andExpect(status().isCreated());
    }

    @Test
    void createInvalid() throws Exception {
        stubAuth();
        Hotel newHotel = getNew();
        newHotel.setEmail("");
        newHotel.setName("");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newHotel)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createWithInvalidEmailFormat() throws Exception {
        stubAuth();
        Hotel newHotel = getNew();
        newHotel.setEmail("invalid_email");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newHotel)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", containsStringIgnoringCase(INVALID_EMAIL_FORMAT)));
    }

    @Test
    public void createForbidden() throws Exception {
        stubUnAuth();
        Hotel newHotel = getNew();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newHotel)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void createWithDuplicateEmail() throws Exception {
        Hotel duplicateHotel = getNew();
        duplicateHotel.setEmail(HOTEL_1_EMAIL);
        stubAuth();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicateHotel)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message", containsStringIgnoringCase(EXCEPTION_DUPLICATE_EMAIL)));
    }

    @Test
    void update() throws Exception {
        stubAuth();
        Hotel updatedHotel = HOTEL_2;
        updatedHotel.setName("Updated name");
        updatedHotel.setAddress("Updated address");
        updatedHotel.setPhoneNumber("+1 (234) 567-89-10");
        perform(MockMvcRequestBuilders.put(REST_URL + HOTEL_2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedHotel)))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateInvalid() throws Exception {
        stubAuth();
        Hotel updatedHotel = HOTEL_2;
        updatedHotel.setName("");
        updatedHotel.setEmail("");
        perform(MockMvcRequestBuilders.put(REST_URL + HOTEL_2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedHotel)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateWithInvalidEmailFormat() throws Exception {
        stubAuth();
        Hotel updatedHotel = HOTEL_3;
        updatedHotel.setEmail("invalid_email");
        perform(MockMvcRequestBuilders.put(REST_URL + HOTEL_3_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedHotel)))
                .andExpect(jsonPath("message", containsStringIgnoringCase(INVALID_EMAIL_FORMAT)));
    }

    @Test
    public void updateForbidden() throws Exception {
        stubUnAuth();
        Hotel updatedHotel = HOTEL_2;
        updatedHotel.setName("Updated name");
        updatedHotel.setAddress("Updated address");
        updatedHotel.setPhoneNumber("+1 (234) 567-89-10");
        perform(MockMvcRequestBuilders.put(REST_URL + HOTEL_2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedHotel)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void updateWithDuplicateEmail() throws Exception {
        stubAuth();
        Hotel updatedHotel = HOTEL_3;
        updatedHotel.setEmail(HOTEL_1_EMAIL);
        updatedHotel.setPhoneNumber("+1 (234) 567-89-10");
        perform(MockMvcRequestBuilders.put(REST_URL + HOTEL_3_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedHotel)))
                .andExpect(jsonPath("message", containsStringIgnoringCase(EXCEPTION_DUPLICATE_EMAIL)));
    }


    @Test
    void delete() throws Exception {
        stubAuth();
        perform(MockMvcRequestBuilders.delete(REST_URL + HOTEL_3_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteNotFound() throws Exception {
        stubAuth();
        perform(MockMvcRequestBuilders.delete(REST_URL + NOT_FOUND_ID))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", equalTo(NOT_FOUND_MESSAGE)))
                .andDo(print());
    }

    @Test
    public void deleteForbidden() throws Exception {
        stubUnAuth();
        perform(MockMvcRequestBuilders.delete(REST_URL  + HOTEL_3_ID))
                .andExpect(status().isForbidden());
    }
}