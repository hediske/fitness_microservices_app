package com.fitness.hediske.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenIntrospectionRequest {
    @NotBlank
    @Size(min = 1, message = "Token must not be empty")
    private String token;
}