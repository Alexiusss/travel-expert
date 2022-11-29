package com.example.image.config;

import com.example.common.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageConfig {
    @Autowired
    private void setMapper(ObjectMapper objectMapper) {
        JsonUtil.setMapper(objectMapper);
    }
}