package com.ctrlbuy.webshop.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest {

    @Mock
    private Model model;

    @InjectMocks
    private RegisterController registerController;

    @Test
    void verifyEmail_ShouldReturnMessageView() {
        // Act
        String result = registerController.verifyEmail("any-token", model);

        // Assert
        assertEquals("verification-result", result);

        // ✅ FIX: Matcha exakt vad controllern sätter
        verify(model).addAttribute("message", "Email-verifiering är för närvarande inaktiverad. Alla nya konton aktiveras automatiskt.");
        verify(model).addAttribute("messageType", "info");
        verify(model).addAttribute("showLoginButton", true);
    }

    @Test
    void resendVerification_ShouldReturnMessageView() {
        // Act
        String result = registerController.resendVerification("any@email.com", model);

        // Assert
        assertEquals("resend-verification", result);

        // ✅ FIX: resendVerification sätter INTE title
        verify(model).addAttribute("message", "Email-verifiering är för närvarande inaktiverad. Alla nya konton aktiveras automatiskt vid registrering.");
        verify(model).addAttribute("messageType", "info");
    }

    @Test
    void showResendForm_ShouldReturnMessageView() {
        // Act
        String result = registerController.showResendForm(model);

        // Assert
        assertEquals("resend-verification", result);

        // ✅ showResendForm BEHÅLLER title - detta är rätt!
        verify(model).addAttribute("title", "Email-verifiering inaktiverad");
        verify(model).addAttribute("message", "Email-verifiering är för närvarande inaktiverad. Alla nya konton aktiveras automatiskt vid registrering.");
        verify(model).addAttribute("messageType", "info");
    }
}