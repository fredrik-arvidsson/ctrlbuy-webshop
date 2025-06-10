package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.dto.RegisterRequest;
import com.ctrlbuy.webshop.dto.RegistrationResult;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.EmailService;
import com.ctrlbuy.webshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegisterControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private RegisterController registerController;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("johndoe");

        reset(userService, emailService, model, bindingResult);
    }

    @Test
    void showRegisterForm_ShouldReturnRegisterViewWithEmptyRegisterRequest() {
        // Act
        String result = registerController.showRegisterForm(model);

        // Assert
        assertEquals("register", result);
        verify(model).addAttribute(eq("registerRequest"), any(RegisterRequest.class));
    }

    @Test
    void registerUser_WithValidData_ShouldRegisterUserAndSendEmailThenRedirect() throws Exception {
        // Arrange
        String verificationToken = "test-token-123";
        User mockUser = User.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();
        RegistrationResult mockResult = new RegistrationResult(mockUser, verificationToken);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.registerNewUserWithToken(registerRequest)).thenReturn(mockResult);

        // Act
        String result = registerController.registerUser(registerRequest, bindingResult, model);

        // Assert
        assertEquals("redirect:/login?registration-pending", result);
        verify(userService).registerNewUserWithToken(registerRequest);
        verify(emailService).sendVerificationEmail(mockUser, verificationToken);
        verify(model, never()).addAttribute(eq("errors"), any());
    }

    @Test
    void registerUser_WithPasswordMismatch_ShouldAddErrorAndReturnRegisterView() {
        // Arrange
        registerRequest.setConfirmPassword("differentPassword");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(
                new ObjectError("registerRequest", "Lösenorden matchar inte")
        ));

        // Act
        String result = registerController.registerUser(registerRequest, bindingResult, model);

        // Assert
        assertEquals("register", result);
        verify(bindingResult).rejectValue("confirmPassword", "error.registerRequest", "Lösenorden matchar inte");
        verify(model).addAttribute(eq("errors"), any(List.class));
        verify(userService, never()).registerNewUserWithToken(any());
        verify(emailService, never()).sendVerificationEmail(any(User.class), any());
    }

    @Test
    void verifyEmail_WithValidToken_ShouldReturnSuccessMessage() {
        // Arrange
        String token = "valid-token-123";
        when(userService.verifyEmail(token)).thenReturn(true);

        // Act
        String result = registerController.verifyEmail(token, model);

        // Assert
        assertEquals("verification-result", result);
        verify(userService).verifyEmail(token);
        verify(model).addAttribute("message", "Ditt konto har bekräftats! Du kan nu logga in.");
        verify(model).addAttribute("messageType", "success");
    }

    @Test
    void verifyEmail_WithInvalidToken_ShouldReturnErrorMessage() {
        // Arrange
        String token = "invalid-token-123";
        when(userService.verifyEmail(token)).thenReturn(false);

        // Act
        String result = registerController.verifyEmail(token, model);

        // Assert
        assertEquals("verification-result", result);
        verify(userService).verifyEmail(token);
        verify(model).addAttribute("message", "Ogiltigt eller utgånget verifieringstoken.");
        verify(model).addAttribute("messageType", "error");
    }
}