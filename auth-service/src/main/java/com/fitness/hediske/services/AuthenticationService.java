package com.fitness.hediske.services;

import java.util.Set;
import java.util.Optional;
import java.util.List;
import java.util.HashSet;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitness.hediske.dto.*;
import com.fitness.hediske.entities.User;
import com.fitness.hediske.enums.FitnessLevel;
import com.fitness.hediske.enums.Gender;
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
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (!user.isEnabled()) {
                throw new AccountDisabledException("Account is disabled");
            }

            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (DisabledException e) {
            throw new AccountDisabledException("Account is disabled");
        } catch (LockedException e) {
            throw new AccountLockedException("Account is locked");
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        } catch (UsernameNotFoundException e) {
            throw new IllegalArgumentException("User not found");
        } catch (Exception e) {
            log.error("Authentication error for email: {}", request.getEmail(), e);
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        try {
            log.info("Attempting to register user with email: {}", request.getEmail());

            // Validate username and email
            if (userRepository.existsByUserid(request.getUsername())) {
                log.warn("Registration failed - username already exists: {}", request.getUsername());
                throw new UsernameAlreadyExistsException("Username already exists");
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("Registration failed - email already exists: {}", request.getEmail());
                throw new EmailAlreadyExistsException("Email already exists");
            }

            // Create user entity
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

            log.debug("Saving user to database");
            user = userRepository.save(user);
            log.info("User saved with ID: {}", user.getId());

            // Generate and send verification email
            log.debug("Generating verification token");
            String verificationToken = jwtService.generateEmailVerificationToken(user);
            user.setEmailVerificationToken(verificationToken);
            userRepository.save(user);

            log.debug("Sending verification email");
            emailService.sendVerificationEmail(user.getEmail(), verificationToken);
            log.info("Verification email sent to: {}", user.getEmail());

            return RegisterResponse.builder()
                    .message("User registered successfully. Please check your email for verification.")
                    .userId(user.getId())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .build();

        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            // These are expected exceptions, just rethrow them
            throw e;
        } catch (Exception e) {
            log.error("Registration failed for email: {}. Error: {}", request.getEmail(), e.getMessage(), e);
            throw new RegistrationException("Registration failed: " + e.getMessage(), e);
        }
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        try {
            String username = jwtService.extractUsername(refreshToken);
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (!jwtService.isTokenValid(refreshToken, user)) {
                throw new InvalidTokenException("Invalid refresh token");
            }

            String newAccessToken = jwtService.generateToken(user);

            return AuthenticationResponse.builder()
                    .token(newAccessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (Exception e) {
            log.error("Token refresh error", e);
            throw new TokenRefreshException("Failed to refresh token: " + e.getMessage());
        }
    }

    @Transactional
    public void verifyEmail(String verificationToken) {
        try {
            String email = jwtService.extractUsername(verificationToken);
            System.out.println(email);
            System.out.println("asbaa");
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (!jwtService.isEmailVerificationTokenValid(verificationToken, user)) {
                throw new InvalidTokenException("Invalid or expired verification token");
            }

            if (!verificationToken.equals(user.getEmailVerificationToken())) {
                throw new InvalidTokenException("Token mismatch");
            }

            // Activate the user account
            user.setEnabled(true);
            user.setEmailVerified(true);
            user.setEmailVerificationToken(null);
            userRepository.save(user);

        } catch (Exception e) {
            log.error("Email verification error", e);
            throw new VerificationException("Email verification failed: " + e.getMessage());
        }
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getEmailVerified()) {
            throw new VerificationException("Email is already verified");
        }

        String newVerificationToken = jwtService.generateEmailVerificationToken(user);
        user.setEmailVerificationToken(newVerificationToken);
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), newVerificationToken);
    }

    public String initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String resetToken = jwtService.generatePasswordResetToken(user);
        user.setPasswordResetToken(resetToken);
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        return resetToken;
    }

    public void resetPassword(String token, String newPassword) {
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!jwtService.isPasswordResetTokenValid(token, user)) {
            throw new InvalidTokenException("Invalid or expired password reset token");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        userRepository.save(user);
    }
}