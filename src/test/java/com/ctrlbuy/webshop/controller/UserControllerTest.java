package com.ctrlbuy.webshop.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        // Setup common test data if needed
    }

    @Test
    void testRegister() {
        // When
        String viewName = userController.register(model);

        // Then
        assertEquals("register", viewName);
        verify(model).addAttribute("title", "Registrera dig - CtrlBuy");
    }

    @Test
    void testLogin() {
        // When
        String viewName = userController.login(model);

        // Then
        assertEquals("login", viewName);
        verify(model).addAttribute("title", "Logga in - CtrlBuy");
    }

    @Test
    void testProfil() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");

        // When
        String viewName = userController.profil(model, authentication);

        // Then
        assertEquals("user-profile", viewName);
        verify(model).addAttribute("title", "Min profil - CtrlBuy");
        verify(model).addAttribute("username", "testuser");
    }

    @Test
    void testProfilNotAuthenticated() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(false);

        // When
        String viewName = userController.profil(model, authentication);

        // Then
        assertEquals("redirect:/user/login", viewName);
        verifyNoInteractions(model);
    }

    @Test
    void testOrders() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(true);

        // When
        String viewName = userController.orders(model, authentication);

        // Then
        assertEquals("coming-soon", viewName);
        verify(model).addAttribute("pageTitle", "Mina beställningar - CtrlBuy");
    }

    @Test
    void testOrdersNotAuthenticated() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(false);

        // When
        String viewName = userController.orders(model, authentication);

        // Then
        assertEquals("redirect:/user/login", viewName);
        verifyNoInteractions(model);
    }

    @Test
    void testFavorites() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(true);

        // When
        String viewName = userController.favorites(model, authentication);

        // Then
        assertEquals("coming-soon", viewName);
        verify(model).addAttribute("pageTitle", "Mina favoriter - CtrlBuy");
    }

    @Test
    void testFavoritesNotAuthenticated() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(false);

        // When
        String viewName = userController.favorites(model, authentication);

        // Then
        assertEquals("redirect:/user/login", viewName);
        verifyNoInteractions(model);
    }

    @Test
    void testSettings() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(true);

        // When
        String viewName = userController.settings(model, authentication);

        // Then
        assertEquals("coming-soon", viewName);
        verify(model).addAttribute("pageTitle", "Inställningar - CtrlBuy");
    }

    @Test
    void testSettingsNotAuthenticated() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(false);

        // When
        String viewName = userController.settings(model, authentication);

        // Then
        assertEquals("redirect:/user/login", viewName);
        verifyNoInteractions(model);
    }
}