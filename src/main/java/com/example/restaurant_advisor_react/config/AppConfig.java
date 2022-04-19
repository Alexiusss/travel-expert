package com.example.restaurant_advisor_react.config;

import com.example.restaurant_advisor_react.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Autowired
    private void setMapper(ObjectMapper objectMapper) {
        JsonUtil.setMapper(objectMapper);
    }
}