package com.hediske.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthFilter jwt;

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
                return http
                                .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
                                .cors(cors -> cors.disable()) // Disable CORS for APIs
                                .authorizeExchange(exchanges -> exchanges
                                                .anyExchange().permitAll())
                                .addFilterAt(jwt, SecurityWebFiltersOrder.FIRST)
                                .build();
        }
}