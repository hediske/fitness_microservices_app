package com.hediske.api_gateway.config;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenIntrospectionResponse {
    private String email;
    private String role;
    private boolean active;

}
