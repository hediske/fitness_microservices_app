package com.hediske.api_gateway.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public List<GroupedOpenApi> apis(RouteDefinitionLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        locator.getRouteDefinitions().collectList().block().stream()
                .filter(route -> route.getId().matches(".*-service"))
                .forEach(route -> {
                    String name = route.getId().replaceAll("-service", "");
                    groups.add(GroupedOpenApi.builder()
                            .pathsToMatch("/api/" + name + "/**")
                            .group(name)
                            .build());
                });
        return groups;
    }
}