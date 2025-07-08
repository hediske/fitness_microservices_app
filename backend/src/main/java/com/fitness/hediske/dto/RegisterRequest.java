package com.fitness.hediske.dto;

import java.time.LocalDate;
import com.fitness.hediske.enums.FitnessLevel;
import com.fitness.hediske.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Email  is required")
    @Email(message = "Email should be valid")
    private String email;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String password;
    private LocalDate birthDate;
    private Double height;
    private Double weight;
    private FitnessLevel fitnessLevel;
}
