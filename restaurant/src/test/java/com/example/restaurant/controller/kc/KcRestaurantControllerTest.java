package com.example.restaurant.controller.kc;

import com.example.restaurant.controller.CommonRestaurantControllerTest;
import com.example.restaurant.model.Restaurant;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.example.common.test.KeycloakTestData.stubKeycloak;
import static com.example.common.test.TestJwtTokenGenerator.generateAccessToken;
import static com.example.common.test.TestJwtTokenGenerator.generateJwk;
import static com.example.common.util.JsonUtil.writeValue;
import static com.example.restaurant.util.RestaurantTestData.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("kc")
public class KcRestaurantControllerTest extends CommonRestaurantControllerTest {

    private static RsaJsonWebKey rsaJsonWebKey;
    private static String adminAccessToken;
    private static String userAccessToken;

    @BeforeAll
    static void init() throws JoseException {
        rsaJsonWebKey = generateJwk();
        adminAccessToken = generateAccessToken(rsaJsonWebKey, List.of("ADMIN", "USER")) ;
        userAccessToken = generateAccessToken(rsaJsonWebKey, List.of("USER")) ;

        WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().port(8180));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8180);
    }

    @Test
    void create() throws Exception {
        Restaurant newRestaurant = getNew();
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .header("Authorization", String.format("Bearer %s", adminAccessToken))
                .content(writeValue(newRestaurant))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    void createForbiddenWithUserRole() throws Exception {
        Restaurant newRestaurant = getNew();
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .header("Authorization", String.format("Bearer %s", userAccessToken))
                .content(writeValue(newRestaurant))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    public void update() throws Exception {
        Restaurant updatedRestaurant = getUpdated();
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.put(REST_URL + updatedRestaurant.getId())
                .header("Authorization", String.format("Bearer %s", adminAccessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updatedRestaurant)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void delete() throws Exception {
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.delete(REST_URL + RESTAURANT1_ID)
                .header("Authorization", String.format("Bearer %s", adminAccessToken)))
                .andExpect(status().isNoContent());
    }

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