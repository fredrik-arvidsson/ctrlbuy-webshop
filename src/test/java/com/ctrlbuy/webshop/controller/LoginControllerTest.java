package com.ctrlbuy.webshop.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginControllerTest {

    @Mock
    private Model model;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        // Clear security context before each test
        SecurityContextHolder.clearContext();
        reset(model);
    }

    @Test
    void login_WhenNotAuthenticated_ShouldReturnLoginViewWithTitle() {
        // Act
        String result = loginController.login(null, null, null, null, model);

        // Assert
        assertEquals("login", result);
        verify(model).addAttribute("title", "Logga in - CtrlBuy");
        verify(model, never()).addAttribute(eq("error"), anyString());
        verify(model, never()).addAttribute(eq("success"), anyString());
        verify(model, never()).addAttribute(eq("registrationPending"), any());
    }

    @Test
    void login_WhenUserIsAlreadyAuthenticated_ShouldRedirectToHome() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        SecurityContextHolder.setContext(securityContext);

        // Act
        String result = loginController.login(null, null, null, null, model);

        // Assert
        assertEquals("redirect:/", result);
        verify(model, never()).addAttribute(anyString(), any());
        
        // Clean up
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_WhenAnonymousUser_ShouldReturnLoginView() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        SecurityContextHolder.setContext(securityContext);

        // Act
        String result = loginController.login(null, null, null, null, model);

        // Assert
        assertEquals("login", result);
        verify(model).addAttribute("title", "Logga in - CtrlBuy");
        
        // Clean up
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_WithErrorParameter_ShouldSetErrorMessage() {
        // Act
        String result = loginController.login("true", null, null, null, model);

        // Assert
        assertEquals("login", result);
        verify(model).addAttribute("title", "Logga in - CtrlBuy");
        verify(model).addAttribute("error", "Ogiltigt användarnamn eller lösenord.");
    }

    @Test
    void login_WithLogoutParameter_ShouldSetSuccessMessage() {
        // Act
        String result = loginController.login(null, "true", null, null, model);

        // Assert
        assertEquals("login", result);
        verify(model).addAttribute("title", "Logga in - CtrlBuy");
        verify(model).addAttribute("success", "Du har loggats ut framgångsrikt.");
    }

    @Test
    void login_WithMethodNotAllowedParameter_ShouldSetErrorMessage() {
        // Act
        String result = loginController.login(null, null, "true", null, model);

        // Assert
        assertEquals("login", result);
        verify(model).addAttribute("title", "Logga in - CtrlBuy");
        verify(model).addAttribute("error", "Ett tekniskt fel uppstod. Försök igen.");
    }

    @Test
    void login_WithRegistrationPendingParameter_ShouldSetRegistrationPendingFlag() {
        // Act
        String result = loginController.login(null, null, null, "true", model);

        // Assert
        assertEquals("login", result);
        verify(model).addAttribute("title", "Logga in - CtrlBuy");
        verify(model).addAttribute("registrationPending", true);
    }

    @Test
    void login_WithMultipleParameters_ShouldSetAllRelevantAttributes() {
        // Act
        String result = loginController.login("true", "true", "true", "true", model);

        // Assert
        assertEquals("login", result);
        verify(model).addAttribute("title", "Logga in - CtrlBuy");
        verify(model).addAttribute("error", "Ett tekniskt fel uppstod. Försök igen."); // methodNotAllowed overrides error
        verify(model).addAttribute("success", "Du har loggats ut framgångsrikt.");
        verify(model).addAttribute("registrationPending", true);
    }

    @Test
    void login_WhenAuthenticationIsNull_ShouldReturnLoginView() {
        // Arrange
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act
        String result = loginController.login(null, null, null, null, model);

        // Assert
        assertEquals("login", result);
        verify(model).addAttribute("title", "Logga in - CtrlBuy");
        
        // Clean up
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_WhenAuthenticationIsNotAuthenticated_ShouldReturnLoginView() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        SecurityContextHolder.setContext(securityContext);

        // Act
        String result = loginController.login(null, null, null, null, model);

        // Assert
        assertEquals("login", result);
        verify(model).addAttribute("title", "Logga in - CtrlBuy");
        verify(authentication, never()).getName(); // Should not check name if not authenticated
        
        // Clean up
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_WithEmptyStringParameters_ShouldTriggerConditionals() {
        // Act
        String result = loginController.login("", "", "", "", model);

        // Assert
        assertEquals("login", result);
        verify(model).addAttribute("title", "Logga in - CtrlBuy");
        // Empty strings are not null, so they trigger the conditionals
        verify(model).addAttribute("error", "Ett tekniskt fel uppstod. Försök igen."); // methodNotAllowed overrides error
        verify(model).addAttribute("success", "Du har loggats ut framgångsrikt.");
        verify(model).addAttribute("registrationPending", true);
    }

    @Test
    void loginProcessError_ShouldRedirectToLoginWithMethodNotAllowedParam() {
        // Act
        String result = loginController.loginProcessError();

        // Assert
        assertEquals("redirect:/login?methodNotAllowed=true", result);
    }

    @Test
    void login_WithValidAuthenticatedUser_ShouldRedirectRegardlessOfParameters() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("validuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        SecurityContextHolder.setContext(securityContext);

        // Act - even with error parameters, should redirect if authenticated
        String result = loginController.login("true", "true", "true", "true", model);

        // Assert
        assertEquals("redirect:/", result);
        verify(model, never()).addAttribute(anyString(), any()); // No attributes should be set
        
        // Clean up
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_WithSpecificParameterValues_ShouldHandleCorrectly() {
        // Test with specific values instead of just "true"
        // Act
        String result = loginController.login("invalid", "success", "method", "pending", model);

        // Assert
        assertEquals("login", result);
        verify(model).addAttribute("title", "Logga in - CtrlBuy");
        verify(model).addAttribute("error", "Ett tekniskt fel uppstod. Försök igen.");
        verify(model).addAttribute("success", "Du har loggats ut framgångsrikt.");
        verify(model).addAttribute("registrationPending", true);
    }
}
