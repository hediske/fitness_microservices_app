package com.hediske.workout.config;

import com.hediske.workout.config.UserRoleAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                        .requestMatchers("/api/exercise/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/exercise/**").authenticated()
                        .anyRequest().permitAll())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(userRoleAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
