package com.fitness.hediske.services;

import java.util.HashSet;
import java.util.List;

import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitness.hediske.dto.*;
import com.fitness.hediske.entities.User;
import com.fitness.hediske.exceptions.*;
import com.fitness.hediske.interfaces.EmailService;
import com.fitness.hediske.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final EmailService emailService;

    public TokenIntrospectionResponse introspectToken(String token) {

        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(
                email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        try {
            if (!jwtService.isTokenValid(token, user)) {
                return TokenIntrospectionResponse.builder()
                        .active(false)
                        .build();
            }

            
            String role = user.getRoles().stream()
                    .findFirst()
                    .orElse("USER");

            return TokenIntrospectionResponse.builder()
                    .active(true)
                    .email(email)
                    .role(role)
                    .build();

        } catch (Exception e) {
            return TokenIntrospectionResponse.builder()
                    .active(false)
                    .build();
        }
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUserid(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .userid(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .gender(request.getGender())
                .birthDate(request.getBirthDate())
                .height(request.getHeight())
                .weight(request.getWeight())
                .fitnessLevel(request.getFitnessLevel())
                .roles(new HashSet<>(List.of("USER")))
                .enabled(false)
                .credentialsNonExpired(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .emailVerified(false)
                .build();

        user = userRepository.save(user);

        String verificationToken = jwtService.generateEmailVerificationToken(user);
        user.setEmailVerificationToken(verificationToken);
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), verificationToken);

        return RegisterResponse.builder()
                .message("User registered successfully. Please check your email for verification.")
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
        } catch (DisabledException e) {
            throw new AccountDisabledException("Account is disabled");
        } catch (LockedException e) {
            throw new AccountLockedException("Account is locked");
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isEnabled()) {
            throw new AccountDisabledException("Account is not verified");
        }

        return AuthenticationResponse.builder()
                .token(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        return AuthenticationResponse.builder()
                .token(jwtService.generateToken(user))
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void verifyEmail(String token) {
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!token.equals(user.getEmailVerificationToken())) {
            throw new InvalidTokenException("Token mismatch");
        }

        if (!jwtService.isEmailVerificationTokenValid(token, user)) {
            throw new InvalidTokenException("Invalid or expired verification token");
        }

        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getEmailVerified()) {
            throw new VerificationException("Email is already verified");
        }

        String newToken = jwtService.generateEmailVerificationToken(user);
        user.setEmailVerificationToken(newToken);
        userRepository.save(user);

        emailService.sendVerificationEmail(email, newToken);
    }

    public String initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtService.generatePasswordResetToken(user);
        user.setPasswordResetToken(token);
        userRepository.save(user);

        emailService.sendPasswordResetEmail(email, token);
        return token;
    }

    public void resetPassword(String token, String newPassword) {
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!jwtService.isPasswordResetTokenValid(token, user)) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        userRepository.save(user);
    }
}
