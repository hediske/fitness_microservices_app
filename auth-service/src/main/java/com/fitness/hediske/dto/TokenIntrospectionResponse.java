package com.fitness.hediske.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenIntrospectionResponse {
    private boolean active;
    private String email;
    private String role;
}