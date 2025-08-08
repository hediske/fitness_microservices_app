package com.hediske.api_gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter {

    private final WebClient.Builder webClientBuilder;

    private final List<String> publicRoutes = List.of(
            "/api/auth/**", "/swagger-ui/**", "/api/**/v3/api-docs", "/swagger-resources/**",
            "/webjars/swagger-ui/index.html", "/webjars/**", "/actuator/**", "/swagger-ui.html",
            "/v3/api-docs/**", "/api-docs/**", "/favicon.ico", "/error"
    // ... other public routes
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        System.out.println("JwtAuthFilter: Processing request...");

        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getURI().getPath();

        System.out.println("Request Path: " + path);

        if (publicRoutes.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
            System.out.println("Public route matched, skipping JWT validation for path: " + path);
            return chain.filter(exchange);
        }

        List<String> authHeaders = exchange.getRequest().getHeaders().get("Authorization");

        System.out.println("Authorization Headers: " + authHeaders);
        if (authHeaders == null || authHeaders.isEmpty() || !authHeaders.get(0).startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or malformed Authorization header");
        }

        String token = authHeaders.get(0).substring(7);

        WebClient webClient = webClientBuilder
                .build();

        return webClient.post()
                .uri("lb://auth-service/api/auth/introspect")
                .bodyValue(Map.of("token", token))
                .retrieve()
                .bodyToMono(TokenIntrospectionResponse.class)
                .flatMap(response -> {
                    if (!response.isActive()) {
                        return unauthorized(exchange, "Token invalid or expired");
                    }

                    System.out.println("Token is valid for user: " + response.getEmail());
                    System.out.println("User roles: " + response.getRole());
                    // Set user email and role in headers for downstream services
                    System.out.println("Setting user email and role in headers for downstream services");

                    if (response.getEmail() == null || response.getRole() == null) {
                        return unauthorized(exchange, "Token does not contain user email or role");
                    }

                    if (!response.isActive()) {
                        return unauthorized(exchange, "Token is not active");
                    }

                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-Email", response.getEmail())
                            .header("X-User-Role", response.getRole())
                            .build();

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(mutatedRequest)
                            .build();

                    return chain.filter(mutatedExchange);
                })
                .onErrorResume(e -> unauthorized(exchange, "Error validating token: " + e.getMessage()));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        String errorJson = String.format("{\"error\": \"%s\", \"timestamp\": \"%s\"}", message, Instant.now());
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(errorJson.getBytes(StandardCharsets.UTF_8))));
    }

}
