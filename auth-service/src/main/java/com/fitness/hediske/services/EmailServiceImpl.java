package com.fitness.hediske.services;

import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.fitness.hediske.exceptions.EmailSendingException;
import com.fitness.hediske.interfaces.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    private final Environment env;

    @Override
    public void sendVerificationEmail(String toEmail, String verificationToken) {
        try {
            String appUrl = env.getProperty("application.base-url", "http://localhost:8080");
            String verificationUrl = appUrl + "/api/auth/verify-email?token=" + verificationToken;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@fitnessapp.com");
            message.setTo(toEmail);
            message.setSubject("Verify your email address");
            message.setText("Please click the following link to verify your email address:\n" + verificationUrl);
            
            mailSender.send(message);
        } catch (Exception e) {
            throw new EmailSendingException("Failed to send verification email", e);
        }
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        // Similar implementation for password reset emails
    }
}