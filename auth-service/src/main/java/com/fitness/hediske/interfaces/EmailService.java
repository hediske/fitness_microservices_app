package com.fitness.hediske.interfaces;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String verificationToken);
    void sendPasswordResetEmail(String toEmail, String resetToken);
}
