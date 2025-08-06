package com.hediske.api_gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
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
            "/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**"
    // ... other public routes
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (publicRoutes.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
            return chain.filter(exchange);
        }

        List<String> authHeaders = exchange.getRequest().getHeaders().get("Authorization");

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

                    exchange.getRequest().mutate()
                            .header("X-User-Email", response.getEmail())
                            .header("X-User-Role", response.getRole())
                            .build();

                    return chain.filter(exchange);
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
