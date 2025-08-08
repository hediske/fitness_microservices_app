package com.hediske.nutrition.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserRoleAuthenticationFilter userRoleAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable()) // Disable CORS for APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/nutrition/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/nutrition/**").authenticated()
                        .anyRequest().permitAll())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(userRoleAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
