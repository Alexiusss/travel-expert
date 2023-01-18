package com.example.hotel.controller;

import com.example.common.util.JsonUtil;
import com.example.hotel.model.Hotel;
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

import static com.example.hotel.util.HotelTestData.getNew;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void create() throws Exception {
        Hotel newHotel = getNew();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newHotel)))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}