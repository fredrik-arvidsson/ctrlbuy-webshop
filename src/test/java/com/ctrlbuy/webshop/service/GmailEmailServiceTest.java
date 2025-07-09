package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.entity.Order;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.impl.GmailEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GmailEmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private GmailEmailService gmailEmailService;

    private User testUser;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");

        // Create test order
        testOrder = new Order();
        testOrder.setId(123L);
        testOrder.setTotalAmount(new BigDecimal("299.99").doubleValue()); // ✅ FIXED: Convert BigDecimal to Double
        testOrder.setStatus(Order.OrderStatus.PENDING);

        System.out.println("✅ Test setup complete");
    }

    @Test
    void sendPasswordResetEmail_WithUser_ShouldSendEmail() {
        // Arrange
        String resetToken = "reset-token-123";

        // Act
        gmailEmailService.sendPasswordResetEmail(testUser, resetToken);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        System.out.println("✅ Password reset email sent with User object");
    }

    @Test
    void sendPasswordResetEmail_WithEmailAndFirstName_ShouldSendEmail() {
        // Arrange
        String email = "customer@example.com";
        String resetToken = "reset-token-456";
        String firstName = "John";

        // Act
        gmailEmailService.sendPasswordResetEmail(email, resetToken, firstName);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        System.out.println("✅ Password reset email sent with email and firstName");
    }

    @Test
    void sendPasswordResetEmail_WithEmailOnly_ShouldReturnTrue() {
        // Arrange
        String email = "test@example.com";
        String resetToken = "token-789";

        // Act
        boolean result = gmailEmailService.sendPasswordResetEmail(email, resetToken);

        // Assert
        assertTrue(result);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        System.out.println("✅ Password reset email with boolean return - SUCCESS");
    }

    @Test
    void sendPasswordResetEmail_WithException_ShouldReturnFalse() {
        // Arrange
        String email = "test@example.com";
        String token = "token";
        doThrow(new RuntimeException("SMTP Error")).when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        boolean result = gmailEmailService.sendPasswordResetEmail(email, token);

        // Assert
        assertFalse(result);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        System.out.println("✅ Exception handling verified - returns false on failure");
    }

    @Test
    void sendVerificationEmail_WithUser_ShouldSendEmail() {
        // Arrange
        String verificationToken = "verify-token-123";

        // Act
        gmailEmailService.sendVerificationEmail(testUser, verificationToken);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        System.out.println("✅ Verification email sent with User object");
    }

    @Test
    void sendVerificationEmail_WithEmailAndFirstName_ShouldSendEmail() {
        // Arrange
        String email = "newuser@example.com";
        String token = "verify-456";
        String firstName = "Jane";

        // Act
        gmailEmailService.sendVerificationEmail(email, token, firstName);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        System.out.println("✅ Verification email sent with email and firstName");
    }

    @Test
    void sendVerificationEmail_WithEmailOnly_ShouldReturnTrue() {
        // Arrange
        String email = "verify@example.com";
        String token = "verify-789";

        // Act
        boolean result = gmailEmailService.sendVerificationEmail(email, token);

        // Assert
        assertTrue(result);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        System.out.println("✅ Verification email with boolean return - SUCCESS");
    }

    @Test
    void sendOrderConfirmationEmail_WithUser_ShouldSendEmail() {
        // Arrange
        String orderNumber = "CB20250708001";

        // Act
        gmailEmailService.sendOrderConfirmationEmail(testUser, orderNumber);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        System.out.println("✅ Order confirmation email sent with User and order number");
    }

    @Test
    void sendOrderConfirmation_WithOrder_ShouldSendEmail() {
        // Arrange
        String email = "customer@example.com";

        // Act
        gmailEmailService.sendOrderConfirmation(testOrder, email);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        System.out.println("✅ Order confirmation sent with Order object");
    }

    @Test
    void sendOrderConfirmation_WithEmailAndOrder_ShouldReturnTrue() {
        // Arrange
        String email = "customer@example.com";

        // Act
        boolean result = gmailEmailService.sendOrderConfirmation(email, testOrder);

        // Assert
        assertTrue(result);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        System.out.println("✅ Order confirmation with boolean return - SUCCESS");
    }

    @Test
    void sendWelcomeEmail_ShouldSendEmail() {
        // Act
        gmailEmailService.sendWelcomeEmail(testUser);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        System.out.println("✅ Welcome email sent successfully");
    }

    @Test
    void sendAccountDeletionNotification_ShouldSendEmail() {
        // Arrange
        String adminUsername = "admin";
        String reason = "Terms violation";

        // Act
        gmailEmailService.sendAccountDeletionNotification(testUser, adminUsername, reason);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        System.out.println("✅ Account deletion notification sent");
    }

    @Test
    void sendAccountDeactivationNotification_ShouldSendEmail() {
        // Arrange
        String adminUsername = "admin";
        String reason = "Suspicious activity";

        // Act
        gmailEmailService.sendAccountDeactivationNotification(testUser, adminUsername, reason);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        System.out.println("✅ Account deactivation notification sent");
    }

    @Test
    void sendAccountReactivationNotification_ShouldSendEmail() {
        // Arrange
        String adminUsername = "admin";

        // Act
        gmailEmailService.sendAccountReactivationNotification(testUser, adminUsername);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        System.out.println("✅ Account reactivation notification sent");
    }
}