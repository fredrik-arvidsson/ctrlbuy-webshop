package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.model.Order;
import com.ctrlbuy.webshop.security.entity.User;

/**
 * Email service for handling all email communications
 */
public interface EmailService {

    /**
     * Send email verification to newly registered user
     * @param user The user to send verification email to
     * @param token The verification token
     */
    void sendVerificationEmail(User user, String token);

    /**
     * Legacy method: Send verification email with email, token, and firstName
     * @param email Email address
     * @param token Verification token
     * @param firstName User's first name
     */
    void sendVerificationEmail(String email, String token, String firstName);

    /**
     * Send welcome email after successful verification
     * @param user The verified user
     */
    void sendWelcomeEmail(User user);

    /**
     * Send password reset email
     * @param user The user requesting password reset
     * @param resetToken The reset token
     */
    void sendPasswordResetEmail(User user, String resetToken);

    /**
     * Legacy method: Send password reset email with email, token, and firstName
     * @param email Email address
     * @param resetToken Reset token
     * @param firstName User's first name
     */
    void sendPasswordResetEmail(String email, String resetToken, String firstName);

    /**
     * Send order confirmation email
     * @param user The user who placed the order
     * @param orderNumber The order number
     */
    void sendOrderConfirmationEmail(User user, String orderNumber);

    /**
     * Legacy method: Send order confirmation
     * @param order The order object
     * @param email Customer email
     */
    void sendOrderConfirmation(Order order, String email);

    /**
     * Test email connection
     * @return true if connection works
     */
    boolean testEmailConnection();
}