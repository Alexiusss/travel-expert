package com.example.user.controller;

import com.example.common.util.TestProfileResolver;
import com.example.user.filter.JwtFilter;
import com.example.user.model.dto.AuthRequest;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.example.common.util.JsonUtil.writeValue;
import static com.example.user.controller.AuthController.AUTH_URL;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles(resolver = TestProfileResolver.class)
@Sql(value = {"/data.sql"})
class AbstractControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    protected MockMvc mockMvc;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void init() {
        wireMockServer = new WireMockServer(new WireMockConfiguration().port(7070));
        wireMockServer.start();
        WireMock.configureFor("localhost", 7070);
    }

    @AfterAll
    static void destroy() {
        wireMockServer.stop();
    }

    // https://docs.spring.io/spring-security/site/docs/4.0.x/reference/htmlsingle/#test-mockmvc
    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .addFilter(jwtFilter)
                .build();
    }

    protected ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
    }

    //  https://www.baeldung.com/oauth-api-testing-with-spring-mvc
    protected String obtainAccessToken(String email, String password) throws Exception {
        ResultActions result = perform(MockMvcRequestBuilders.post(AUTH_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(new AuthRequest(email, password))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("accessToken").toString();
    }
}
