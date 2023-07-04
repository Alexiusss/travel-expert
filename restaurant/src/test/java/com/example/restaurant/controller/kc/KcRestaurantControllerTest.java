package com.example.restaurant.controller.kc;

import com.example.restaurant.controller.CommonRestaurantControllerTest;
import com.example.restaurant.model.Restaurant;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.common.util.JsonUtil.writeValue;
import static com.example.restaurant.util.RestaurantTestData.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class KcRestaurantControllerTest extends CommonRestaurantControllerTest {

    @Test
    void createForbidden() throws Exception {
        Restaurant newRestaurant = getNew();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .content(writeValue(newRestaurant))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    public void updateForbidden() throws Exception {
        Restaurant updatedRestaurant = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + updatedRestaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updatedRestaurant)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteForbidden() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL  + RESTAURANT3))
                .andExpect(status().isForbidden());
    }
}