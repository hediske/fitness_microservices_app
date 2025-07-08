package com.fitness.hediske.services;

import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fitness.hediske.dto.AuthenticationRequest;
import com.fitness.hediske.dto.AuthenticationResponse;
import com.fitness.hediske.dto.RegisterRequest;
import com.fitness.hediske.dto.RegisterResponse;
import com.fitness.hediske.entities.User;
import com.fitness.hediske.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String jwtToken = jwtService.generateToken(user);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }

    
    
    }
    public RegisterResponse register(RegisterRequest request) {

        try{

            if(userRepository.existsByUsername(request.getUsername())) {
                return RegisterResponse.builder()
                        .message("Username already exists")
                        .build();
            }
            if(userRepository.existsByEmail(request.getEmail())) {
                return RegisterResponse.builder()
                        .message("Email already exists")
                        .build();
            }


            // create a new user

            User user = User.builder()
                            .username(request.getUsername())
                            .email(request.getEmail())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .gender(request.getGender())
                            .birthDate(request.getBirthDate())
                            .height(request.getHeight())
                            .weight(request.getWeight())
                            .fitnessLevel(request.getFitnessLevel())
                            .roles(Set.of("USER"))
                            .enabled(true)
                            .credentialsNonExpired(true)
                            .accountNonExpired(true)
                            .accountNonLocked(true)
                            .build();

            userRepository.save(user);
            return RegisterResponse.builder()
                    .message("User registered successfully")
                    .userId(user.getId())
                    .build();

        }

        catch (Exception e) {
            return RegisterResponse.builder()
                    .message("Error registering user: " + e.getMessage())
                    .build();
        }


    }




}