package com.hediske.nutrition.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {
    private String allowedOrigin;

    public String getAllowedOrigin() {
        System.out.println("Allowed Origin: " + allowedOrigin);
        return allowedOrigin;
    }

    public void setAllowedOrigin(String allowedOrigin) {
        this.allowedOrigin = allowedOrigin;
    }
}
