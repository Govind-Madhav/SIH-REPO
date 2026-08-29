package com.ner.logistics.risk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Data
@Configuration
@ConfigurationProperties(prefix = "ml.prediction")
public class RiskConfig {

    private boolean enabled = false;
    private String baseUrl = "http://localhost:8000";
    private int timeout = 5000;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
