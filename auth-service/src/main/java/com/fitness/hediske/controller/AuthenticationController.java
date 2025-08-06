package com.fitness.hediske.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitness.hediske.dto.AuthenticationRequest;
import com.fitness.hediske.dto.AuthenticationResponse;
import com.fitness.hediske.dto.RegisterRequest;
import com.fitness.hediske.dto.RegisterResponse;
import com.fitness.hediske.dto.TokenIntrospectionRequest;
import com.fitness.hediske.dto.TokenIntrospectionResponse;
import com.fitness.hediske.dto.TokenRefreshRequest;
import com.fitness.hediske.services.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }
    
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authenticationService.refreshToken(request.getRefreshToken()));
    }
    
    @PostMapping("/introspect")
    public ResponseEntity<TokenIntrospectionResponse> introspectToken(@RequestBody TokenIntrospectionRequest request) {
        return ResponseEntity.ok(authenticationService.introspectToken(request.getToken()));
    }

}