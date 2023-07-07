package com.example.hotel.controller.kc;

import com.example.common.util.JsonUtil;
import com.example.hotel.controller.CommonControllerTest;
import com.example.hotel.model.Hotel;
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
import static com.example.hotel.util.HotelTestData.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("kc")
public class KcHotelControllerTest extends CommonControllerTest {

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
        Hotel newHotel = getNew();
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .header("Authorization", String.format("Bearer %s", adminAccessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newHotel)))
                .andExpect(status().isCreated());
    }

    @Test
    void update() throws Exception {
        stubKeycloak(rsaJsonWebKey);
        Hotel updatedHotel = HOTEL_2;
        updatedHotel.setName("Updated name");
        updatedHotel.setAddress("Updated address");
        updatedHotel.setPhoneNumber("+1 (234) 567-89-10");
        perform(MockMvcRequestBuilders.put(REST_URL + HOTEL_2_ID)
                .header("Authorization", String.format("Bearer %s", adminAccessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedHotel)))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete() throws Exception {
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.delete(REST_URL + HOTEL_3_ID)
                .header("Authorization", String.format("Bearer %s", adminAccessToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void createForbidden() throws Exception {
        Hotel newHotel = getNew();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newHotel)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createForbiddenWithUserRole() throws Exception {
        Hotel newHotel = getNew();
        stubKeycloak(rsaJsonWebKey);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .header("Authorization", String.format("Bearer %s", userAccessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newHotel)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateForbidden() throws Exception {
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
    public void deleteForbidden() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL  + HOTEL_3_ID))
                .andExpect(status().isForbidden());
    }
}
