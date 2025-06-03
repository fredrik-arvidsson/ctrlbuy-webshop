package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.security.repository.UserRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProfileControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    private ProfileController profileController;

    private User testUser;

    @BeforeEach
    void setUp() {
        profileController = new ProfileController(userRepository);
        
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        
        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void profile_WhenUserIsAuthenticatedAndExists_ShouldReturnProfileView() {
        // Arrange
        String username = "testuser";
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        
        // Set up SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);

        // Act
        String result = profileController.profile(model);

        // Assert
        assertEquals("profile", result);
        verify(model).addAttribute("user", testUser);
        verify(model).addAttribute("username", username);
        verify(model).addAttribute("isAuthenticated", true);
        verify(userRepository).findByUsername(username);
        
        // Clean up
        SecurityContextHolder.clearContext();
    }

    @Test
    void profile_WhenUserIsAuthenticatedButNotExists_ShouldRedirectToLogin() {
        // Arrange
        String username = "nonexistentuser";
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        // Set up SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);

        // Act
        String result = profileController.profile(model);

        // Assert
        assertEquals("redirect:/login", result);
        verify(userRepository).findByUsername(username);
        verify(model, never()).addAttribute(anyString(), any());
        
        // Clean up
        SecurityContextHolder.clearContext();
    }

    @Test
    void profile_WhenAuthenticationIsNull_ShouldRedirectToLogin() {
        // Arrange
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        
        // Set up SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);

        // Act
        String result = profileController.profile(model);

        // Assert
        assertEquals("redirect:/login", result);
        verify(userRepository, never()).findByUsername(anyString());
        verify(model, never()).addAttribute(anyString(), any());
        
        // Clean up
        SecurityContextHolder.clearContext();
    }

    @Test
    void profile_WhenUserIsNotAuthenticated_ShouldRedirectToLogin() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        // Set up SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);

        // Act
        String result = profileController.profile(model);

        // Assert
        assertEquals("redirect:/login", result);
        verify(authentication, never()).getName();
        verify(userRepository, never()).findByUsername(anyString());
        verify(model, never()).addAttribute(anyString(), any());
        
        // Clean up
        SecurityContextHolder.clearContext();
    }

    @Test
    void profile_WhenSecurityContextIsEmpty_ShouldRedirectToLogin() {
        // Arrange - SecurityContext is empty by default from setUp()

        // Act
        String result = profileController.profile(model);

        // Assert
        assertEquals("redirect:/login", result);
        verify(userRepository, never()).findByUsername(anyString());
        verify(model, never()).addAttribute(anyString(), any());
    }
}
