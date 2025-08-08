package com.hediske.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

@Configuration
public class GatewayConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public GatewayConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public WebFilter jwtFilter() {
        return jwtAuthFilter;
    }
}