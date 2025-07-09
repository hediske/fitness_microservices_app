package com.fitness.hediske.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitness.hediske.dto.EmailRequest;
import com.fitness.hediske.dto.PasswordResetRequest;
import com.fitness.hediske.services.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth/password-reset")
@RequiredArgsConstructor
public class PasswordResetController {

    private final AuthenticationService authenticationService;

    @PostMapping("/initiate")
    public ResponseEntity<String> initiatePasswordReset(@RequestBody EmailRequest request) {
        authenticationService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok("Password reset link has been sent to your email");
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        
        authenticationService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password has been reset successfully");
    }
}