package com.example.restaurant_advisor_react.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.restaurant_advisor_react.controller.UserController.REST_URL;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class UserControllerTest extends AbstractControllerTest {

    @Test
    void get() throws Exception {
       perform(MockMvcRequestBuilders.get(REST_URL))
               .andDo(print());
    }
}