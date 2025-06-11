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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Mock
    private RedirectAttributes redirectAttributes;

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

        reset(userService, emailService, model, bindingResult, redirectAttributes);
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
        when(userService.existsByUsernameIncludingInactive(any())).thenReturn(false);
        when(userService.existsByEmailIncludingInactive(any())).thenReturn(false);
        when(userService.registerNewUserWithToken(registerRequest)).thenReturn(mockResult);

        // Act
        String result = registerController.registerUser(registerRequest, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/login", result);
        verify(userService).registerNewUserWithToken(registerRequest);
        verify(emailService).sendVerificationEmail(mockUser, verificationToken);
        verify(redirectAttributes).addFlashAttribute(eq("success"), any(String.class));
    }

    @Test
    void registerUser_WithPasswordMismatch_ShouldAddErrorAndReturnRegisterView() {
        // Arrange
        registerRequest.setConfirmPassword("differentPassword");
        when(bindingResult.hasErrors()).thenReturn(false); // Initially no errors
        when(userService.existsByUsernameIncludingInactive(any())).thenReturn(false);
        when(userService.existsByEmailIncludingInactive(any())).thenReturn(false);

        // Act
        String result = registerController.registerUser(registerRequest, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("register", result);
        verify(bindingResult).rejectValue("confirmPassword", "password.mismatch",
                "Lösenorden matchar inte. Kontrollera att du har skrivit samma lösenord i båda fälten.");
        verify(userService, never()).registerNewUserWithToken(any());
        verify(emailService, never()).sendVerificationEmail(any(User.class), any());
    }

    @Test
    void registerUser_WithExistingUsername_ShouldAddErrorAndReturnRegisterView() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.existsByUsernameIncludingInactive("johndoe")).thenReturn(true);
        when(userService.existsByEmailIncludingInactive(any())).thenReturn(false);

        // Act
        String result = registerController.registerUser(registerRequest, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("register", result);
        verify(bindingResult).rejectValue("username", "username.exists",
                "Detta användarnamn är redan upptaget. Välj ett annat användarnamn.");
        verify(userService, never()).registerNewUserWithToken(any());
        verify(emailService, never()).sendVerificationEmail(any(User.class), any());
    }

    @Test
    void registerUser_WithExistingEmail_ShouldAddErrorAndReturnRegisterView() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.existsByUsernameIncludingInactive(any())).thenReturn(false);
        when(userService.existsByEmailIncludingInactive("test@example.com")).thenReturn(true);

        // Act
        String result = registerController.registerUser(registerRequest, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("register", result);
        verify(bindingResult).rejectValue("email", "email.exists",
                "Denna e-postadress är redan registrerad. Om du har glömt ditt lösenord kan du återställa det här.");
        verify(userService, never()).registerNewUserWithToken(any());
        verify(emailService, never()).sendVerificationEmail(any(User.class), any());
    }

    @Test
    void registerUser_WithValidationErrors_ShouldReturnRegisterView() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(
                new ObjectError("registerRequest", "Förnamn är obligatoriskt")
        ));

        // Act
        String result = registerController.registerUser(registerRequest, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("register", result);
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
        verify(model).addAttribute("showLoginButton", true);
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
        verify(model).addAttribute("showResendButton", true);
    }

    @Test
    void showResendForm_ShouldReturnResendVerificationView() {
        // Act
        String result = registerController.showResendForm(model);

        // Assert
        assertEquals("resend-verification", result);
        verify(model).addAttribute("title", "Skicka verifieringsmail igen");
    }

    @Test
    void resendVerification_WithValidEmail_ShouldSendNewToken() {
        // Arrange
        String email = "test@example.com";
        when(userService.existsByEmailIncludingInactive(email)).thenReturn(true);
        when(userService.createNewVerificationToken(email)).thenReturn("new-token-123");

        // Act
        String result = registerController.resendVerification(email, model);

        // Assert
        assertEquals("resend-verification", result);
        verify(userService).existsByEmailIncludingInactive(email);
        verify(userService).createNewVerificationToken(email);
        verify(model).addAttribute(eq("message"), any(String.class));
        verify(model).addAttribute("messageType", "success");
    }

    @Test
    void resendVerification_WithNonExistentEmail_ShouldReturnError() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userService.existsByEmailIncludingInactive(email)).thenReturn(false);

        // Act
        String result = registerController.resendVerification(email, model);

        // Assert
        assertEquals("resend-verification", result);
        verify(userService).existsByEmailIncludingInactive(email);
        verify(userService, never()).createNewVerificationToken(any());
        verify(model).addAttribute("error", "E-postadressen hittades inte i vårt system.");
        verify(model).addAttribute("messageType", "error");
    }

    @Test
    void checkUsernameAvailability_WithAvailableUsername_ShouldReturnTrue() {
        // Arrange
        String username = "availableuser";
        when(userService.existsByUsernameIncludingInactive(username)).thenReturn(false);

        // Act
        boolean result = registerController.checkUsernameAvailability(username);

        // Assert
        assertEquals(true, result);
        verify(userService).existsByUsernameIncludingInactive(username);
    }

    @Test
    void checkUsernameAvailability_WithTakenUsername_ShouldReturnFalse() {
        // Arrange
        String username = "takenuser";
        when(userService.existsByUsernameIncludingInactive(username)).thenReturn(true);

        // Act
        boolean result = registerController.checkUsernameAvailability(username);

        // Assert
        assertEquals(false, result);
        verify(userService).existsByUsernameIncludingInactive(username);
    }

    @Test
    void checkEmailAvailability_WithAvailableEmail_ShouldReturnTrue() {
        // Arrange
        String email = "available@example.com";
        when(userService.existsByEmailIncludingInactive(email)).thenReturn(false);

        // Act
        boolean result = registerController.checkEmailAvailability(email);

        // Assert
        assertEquals(true, result);
        verify(userService).existsByEmailIncludingInactive(email);
    }

    @Test
    void checkEmailAvailability_WithTakenEmail_ShouldReturnFalse() {
        // Arrange
        String email = "taken@example.com";
        when(userService.existsByEmailIncludingInactive(email)).thenReturn(true);

        // Act
        boolean result = registerController.checkEmailAvailability(email);

        // Assert
        assertEquals(false, result);
        verify(userService).existsByEmailIncludingInactive(email);
    }
}