package com.example.SanChoi247.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class Cloudinaryconfig {
    private final String CLOUD_NAME = "dvwn7bgut";
    private final String API_KEY = "689811945747513";
    private final String API_SECRET = "2u0KnzVka52GBgy13-ni17FcX80";

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", API_KEY);
        config.put("api_secret", API_SECRET);

        return new Cloudinary(config);
    }
}
