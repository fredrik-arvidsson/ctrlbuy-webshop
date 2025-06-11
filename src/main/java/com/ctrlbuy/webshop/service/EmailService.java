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
     * Send verification email with just email and token
     * @param email Email address
     * @param token Verification token
     * @return true if email was sent successfully
     */
    boolean sendVerificationEmail(String email, String token);

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
     * Send password reset email with just email and token
     * @param email Email address
     * @param resetToken Reset token
     * @return true if email was sent successfully
     */
    boolean sendPasswordResetEmail(String email, String resetToken);

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
     * Send order confirmation email
     * @param email Customer email
     * @param order The order object
     * @return true if email was sent successfully
     */
    boolean sendOrderConfirmation(String email, Order order);

    /**
     * Send notification to user when their account has been permanently deleted
     * @param deletedUser The user whose account was deleted
     * @param adminUsername The admin who performed the deletion
     * @param reason Optional reason for deletion
     */
    void sendAccountDeletionNotification(User deletedUser, String adminUsername, String reason);

    /**
     * Send notification to user when their account has been deactivated
     * @param deactivatedUser The user whose account was deactivated
     * @param adminUsername The admin who performed the deactivation
     * @param reason Optional reason for deactivation
     */
    void sendAccountDeactivationNotification(User deactivatedUser, String adminUsername, String reason);

    /**
     * Send notification to user when their account has been reactivated
     * @param reactivatedUser The user whose account was reactivated
     * @param adminUsername The admin who performed the reactivation
     */
    void sendAccountReactivationNotification(User reactivatedUser, String adminUsername);

    /**
     * Test email connection
     * @return true if connection works
     */
    boolean testEmailConnection();

    /**
     * Check if email service is configured
     * @return true if configured
     */
    boolean isConfigured();
}