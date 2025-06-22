package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.service.PasswordResetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PasswordResetControllerTest {

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private PasswordResetController passwordResetController;

    @BeforeEach
    void setUp() {
        reset(passwordResetService, model, redirectAttributes);
    }

    @Test
    void showForgotPasswordForm_ShouldReturnForgotPasswordView() {
        // Act
        String result = passwordResetController.showForgotPasswordForm();

        // Assert
        assertEquals("forgot-password", result);
    }

    @Test
    void processForgotPassword_WithValidEmail_ShouldReturnSuccessMessage() {
        // Arrange
        String email = "test@example.com";
        when(passwordResetService.requestPasswordReset("test@example.com")).thenReturn(true);

        // Act
        String result = passwordResetController.processForgotPassword(email, redirectAttributes);

        // Assert
        assertEquals("redirect:/forgot-password", result);
        verify(passwordResetService).requestPasswordReset("test@example.com");
        verify(redirectAttributes).addFlashAttribute("success", 
                "Om e-postadressen finns i vårt system har vi skickat instruktioner för lösenordsåterställning.");
        verify(redirectAttributes, never()).addFlashAttribute(eq("error"), any());
    }

    @Test
    void processForgotPassword_WithValidEmailButServiceReturnsFalse_ShouldReturnErrorMessage() {
        // Arrange
        String email = "test@example.com";
        when(passwordResetService.requestPasswordReset("test@example.com")).thenReturn(false);

        // Act
        String result = passwordResetController.processForgotPassword(email, redirectAttributes);

        // Assert
        assertEquals("redirect:/forgot-password", result);
        verify(passwordResetService).requestPasswordReset("test@example.com");
        verify(redirectAttributes).addFlashAttribute("error", "Ett fel uppstod. Försök igen senare.");
        verify(redirectAttributes, never()).addFlashAttribute(eq("success"), any());
    }

    @Test
    void processForgotPassword_WithNullEmail_ShouldReturnValidationError() {
        // Act
        String result = passwordResetController.processForgotPassword(null, redirectAttributes);

        // Assert
        assertEquals("redirect:/forgot-password", result);
        verify(passwordResetService, never()).requestPasswordReset(any());
        verify(redirectAttributes).addFlashAttribute("error", "Vänligen ange en giltig e-postadress.");
    }

    @Test
    void processForgotPassword_WithEmptyEmail_ShouldReturnValidationError() {
        // Act
        String result = passwordResetController.processForgotPassword("", redirectAttributes);

        // Assert
        assertEquals("redirect:/forgot-password", result);
        verify(passwordResetService, never()).requestPasswordReset(any());
        verify(redirectAttributes).addFlashAttribute("error", "Vänligen ange en giltig e-postadress.");
    }

    @Test
    void processForgotPassword_WithEmailWithoutAtSign_ShouldReturnValidationError() {
        // Act
        String result = passwordResetController.processForgotPassword("invalidemail", redirectAttributes);

        // Assert
        assertEquals("redirect:/forgot-password", result);
        verify(passwordResetService, never()).requestPasswordReset(any());
        verify(redirectAttributes).addFlashAttribute("error", "Vänligen ange en giltig e-postadress.");
    }

    @Test
    void processForgotPassword_WithEmailWithWhitespace_ShouldTrimAndLowercase() {
        // Arrange
        String email = "  TEST@EXAMPLE.COM  ";
        when(passwordResetService.requestPasswordReset("test@example.com")).thenReturn(true);

        // Act
        String result = passwordResetController.processForgotPassword(email, redirectAttributes);

        // Assert
        assertEquals("redirect:/forgot-password", result);
        verify(passwordResetService).requestPasswordReset("test@example.com");
        verify(redirectAttributes).addFlashAttribute("success", 
                "Om e-postadressen finns i vårt system har vi skickat instruktioner för lösenordsåterställning.");
    }

    @Test
    void processForgotPassword_WhenServiceThrowsException_ShouldReturnTechnicalError() {
        // Arrange
        String email = "test@example.com";
        when(passwordResetService.requestPasswordReset("test@example.com"))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        String result = passwordResetController.processForgotPassword(email, redirectAttributes);

        // Assert
        assertEquals("redirect:/forgot-password", result);
        verify(passwordResetService).requestPasswordReset("test@example.com");
        verify(redirectAttributes).addFlashAttribute("error", "Ett tekniskt fel uppstod. Försök igen senare.");
    }

    @Test
    void showResetPasswordForm_WithValidToken_ShouldReturnResetPasswordView() {
        // Arrange
        String token = "valid-token-123";
        when(passwordResetService.isValidResetToken(token)).thenReturn(true);

        // Act
        String result = passwordResetController.showResetPasswordForm(token, model);

        // Assert
        assertEquals("reset-password", result);
        verify(passwordResetService).isValidResetToken(token);
        verify(model).addAttribute("token", token);
        verify(model, never()).addAttribute(eq("error"), any());
    }

    @Test
    void showResetPasswordForm_WithInvalidToken_ShouldReturnErrorView() {
        // Arrange
        String token = "invalid-token";
        when(passwordResetService.isValidResetToken(token)).thenReturn(false);

        // Act
        String result = passwordResetController.showResetPasswordForm(token, model);

        // Assert
        assertEquals("reset-password-error", result);
        verify(passwordResetService).isValidResetToken(token);
        verify(model).addAttribute("error", "Ogiltigt eller utgånget återställningstoken. Begär en ny länk.");
        verify(model, never()).addAttribute("token", token);
    }

    @Test
    void showResetPasswordForm_WhenServiceThrowsException_ShouldReturnErrorView() {
        // Arrange
        String token = "problematic-token";
        when(passwordResetService.isValidResetToken(token))
                .thenThrow(new RuntimeException("Token validation error"));

        // Act
        String result = passwordResetController.showResetPasswordForm(token, model);

        // Assert
        assertEquals("reset-password-error", result);
        verify(passwordResetService).isValidResetToken(token);
        verify(model).addAttribute("error", "Ett tekniskt fel uppstod. Försök igen senare.");
    }

    @Test
    void processResetPassword_WithValidData_ShouldResetPasswordAndRedirectToLogin() {
        // Arrange
        String token = "valid-token";
        String password = "newpassword123";
        String confirmPassword = "newpassword123";
        when(passwordResetService.isValidResetToken(token)).thenReturn(true);
        when(passwordResetService.resetPassword(token, password)).thenReturn(true);

        // Act
        String result = passwordResetController.processResetPassword(token, password, confirmPassword, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/login", result);
        verify(passwordResetService).isValidResetToken(token);
        verify(passwordResetService).resetPassword(token, password);
        verify(redirectAttributes).addFlashAttribute("success", 
                "Ditt lösenord har återställts! Du kan nu logga in med ditt nya lösenord.");
        verify(model, never()).addAttribute(eq("error"), any());
    }

    @Test
    void processResetPassword_WithInvalidToken_ShouldReturnErrorView() {
        // Arrange
        String token = "invalid-token";
        String password = "newpassword123";
        String confirmPassword = "newpassword123";
        when(passwordResetService.isValidResetToken(token)).thenReturn(false);

        // Act
        String result = passwordResetController.processResetPassword(token, password, confirmPassword, model, redirectAttributes);

        // Assert
        assertEquals("reset-password-error", result);
        verify(passwordResetService).isValidResetToken(token);
        verify(passwordResetService, never()).resetPassword(any(), any());
        verify(model).addAttribute("error", "Ogiltigt eller utgånget återställningstoken. Begär en ny länk.");
    }

    @Test
    void processResetPassword_WithShortPassword_ShouldReturnValidationError() {
        // Arrange
        String token = "valid-token";
        String password = "123"; // Too short
        String confirmPassword = "123";
        when(passwordResetService.isValidResetToken(token)).thenReturn(true);

        // Act
        String result = passwordResetController.processResetPassword(token, password, confirmPassword, model, redirectAttributes);

        // Assert
        assertEquals("reset-password", result);
        verify(passwordResetService).isValidResetToken(token);
        verify(passwordResetService, never()).resetPassword(any(), any());
        verify(model).addAttribute("error", "Lösenordet måste vara minst 6 tecken långt.");
        verify(model).addAttribute("token", token);
    }

    @Test
    void processResetPassword_WithNullPassword_ShouldReturnValidationError() {
        // Arrange
        String token = "valid-token";
        String password = null;
        String confirmPassword = "password123";
        when(passwordResetService.isValidResetToken(token)).thenReturn(true);

        // Act
        String result = passwordResetController.processResetPassword(token, password, confirmPassword, model, redirectAttributes);

        // Assert
        assertEquals("reset-password", result);
        verify(passwordResetService).isValidResetToken(token);
        verify(passwordResetService, never()).resetPassword(any(), any());
        verify(model).addAttribute("error", "Lösenordet måste vara minst 6 tecken långt.");
        verify(model).addAttribute("token", token);
    }

    @Test
    void processResetPassword_WithPasswordMismatch_ShouldReturnValidationError() {
        // Arrange
        String token = "valid-token";
        String password = "password123";
        String confirmPassword = "differentPassword";
        when(passwordResetService.isValidResetToken(token)).thenReturn(true);

        // Act
        String result = passwordResetController.processResetPassword(token, password, confirmPassword, model, redirectAttributes);

        // Assert
        assertEquals("reset-password", result);
        verify(passwordResetService).isValidResetToken(token);
        verify(passwordResetService, never()).resetPassword(any(), any());
        verify(model).addAttribute("error", "Lösenorden matchar inte.");
        verify(model).addAttribute("token", token);
    }

    @Test
    void processResetPassword_WhenResetPasswordFails_ShouldReturnError() {
        // Arrange
        String token = "valid-token";
        String password = "newpassword123";
        String confirmPassword = "newpassword123";
        when(passwordResetService.isValidResetToken(token)).thenReturn(true);
        when(passwordResetService.resetPassword(token, password)).thenReturn(false);

        // Act
        String result = passwordResetController.processResetPassword(token, password, confirmPassword, model, redirectAttributes);

        // Assert
        assertEquals("reset-password", result);
        verify(passwordResetService).isValidResetToken(token);
        verify(passwordResetService).resetPassword(token, password);
        verify(model).addAttribute("error", "Kunde inte återställa lösenordet. Försök igen.");
        verify(model).addAttribute("token", token);
    }

    @Test
    void processResetPassword_WhenServiceThrowsException_ShouldReturnTechnicalError() {
        // Arrange
        String token = "valid-token";
        String password = "newpassword123";
        String confirmPassword = "newpassword123";
        when(passwordResetService.isValidResetToken(token)).thenReturn(true);
        when(passwordResetService.resetPassword(token, password))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        String result = passwordResetController.processResetPassword(token, password, confirmPassword, model, redirectAttributes);

        // Assert
        assertEquals("reset-password", result);
        verify(passwordResetService).isValidResetToken(token);
        verify(passwordResetService).resetPassword(token, password);
        verify(model).addAttribute("error", "Ett tekniskt fel uppstod. Försök igen senare.");
        verify(model).addAttribute("token", token);
    }

    @Test
    void processResetPassword_WithPasswordWithWhitespace_ShouldHandleCorrectly() {
        // Arrange
        String token = "valid-token";
        String password = "  password123  "; // Password with whitespace - should NOT be trimmed for security
        String confirmPassword = "  password123  ";
        when(passwordResetService.isValidResetToken(token)).thenReturn(true);
        when(passwordResetService.resetPassword(token, password)).thenReturn(true);

        // Act
        String result = passwordResetController.processResetPassword(token, password, confirmPassword, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/login", result);
        verify(passwordResetService).resetPassword(token, password); // Should use password as-is, including whitespace
        verify(redirectAttributes).addFlashAttribute("success", 
                "Ditt lösenord har återställts! Du kan nu logga in med ditt nya lösenord.");
    }
}
