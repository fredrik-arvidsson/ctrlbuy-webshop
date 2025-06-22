package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HomeControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    void setUp() {
        System.out.println("⭐ HomeController test setup complete");
    }

    @Test
    void shouldShowHomePage() {
        // Given
        when(productService.getPopularProducts()).thenReturn(new ArrayList<>());
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(new ArrayList<>()));

        // When
        String result = homeController.home(model, authentication, request);

        // Then
        assertEquals("home", result);
        System.out.println("✅ Show home page - PASS");
    }

    @Test
    void shouldShowHomePageWithAuthentication() {
        // Given
        when(authentication.getName()).thenReturn("testuser");
        when(productService.getPopularProducts()).thenReturn(new ArrayList<>());
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(new ArrayList<>()));

        // When
        String result = homeController.home(model, authentication, request);

        // Then
        assertEquals("home", result);
        System.out.println("✅ Show home page with authentication - PASS");
    }

    @Test
    void shouldRedirectWelcomeToHome() {
        // When
        String result = homeController.welcome(model);

        // Then
        assertEquals("redirect:/", result);
        System.out.println("✅ Redirect welcome to home - PASS");
    }

    @Test
    void shouldShowAboutPage() {
        // When
        String result = homeController.about(model);

        // Then
        assertEquals("about", result);
        verify(model).addAttribute("title", "Om oss - CtrlBuy");
        System.out.println("✅ Show about page - PASS");
    }

    @Test
    void shouldRedirectToUserProfilWhenAuthenticated() {
        // Given
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.isAuthenticated()).thenReturn(true);

        // When
        String result = homeController.minProfil(authentication, session);

        // Then
        assertEquals("redirect:/user/profil", result);
        System.out.println("✅ Redirect to user/profil when authenticated - PASS");
    }

    @Test
    void shouldRedirectToUserProfilWhenNotAuthenticated() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(false);

        // When
        String result = homeController.minProfil(authentication, session);

        // Then
        assertEquals("redirect:/user/profil", result);
        System.out.println("✅ Redirect to user/profil when not authenticated - PASS");
    }
}