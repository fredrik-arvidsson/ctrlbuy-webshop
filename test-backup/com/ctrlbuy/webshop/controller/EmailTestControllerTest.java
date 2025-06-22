package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Email Test Controller Unit Tests")
class EmailTestControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailTestController emailTestController;

    @Test
    @DisplayName("Test email - Success")
    void testEmailSuccess() throws Exception {
        // Arrange
        String testEmail = "test@example.com";
        doNothing().when(emailService).sendVerificationEmail(testEmail, "test-token-123", "Testanvändare");

        // Act
        String result = emailTestController.testEmail(testEmail);

        // Assert
        assertEquals("E-post skickad till: " + testEmail, result);
        verify(emailService).sendVerificationEmail(testEmail, "test-token-123", "Testanvändare");
        
        System.out.println("✅ Test email success - PASS");
    }

    @Test
    @DisplayName("Test email - Exception")
    void testEmailException() throws Exception {
        // Arrange
        String testEmail = "invalid@example.com";
        String errorMessage = "Email service not available";
        doThrow(new RuntimeException(errorMessage))
            .when(emailService).sendVerificationEmail(testEmail, "test-token-123", "Testanvändare");

        // Act
        String result = emailTestController.testEmail(testEmail);

        // Assert
        assertEquals("FEL vid e-postsändning: " + errorMessage, result);
        verify(emailService).sendVerificationEmail(testEmail, "test-token-123", "Testanvändare");
        
        System.out.println("✅ Test email exception - PASS");
    }
}
