package com.ctrlbuy.webshop.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.ui.Model;
import org.springframework.ui.ConcurrentModel;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Legal Controller Unit Tests")
class LegalControllerTest {

    private LegalController legalController;
    private Model model;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        legalController = new LegalController();
        model = new ConcurrentModel();
        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
    }

    @Test
    @DisplayName("Should show terms page with correct attributes")
    void testTermsPage() {
        String viewName = legalController.showTerms(model, auth);
        assertEquals("terms", viewName);
        assertEquals("Användarvillkor", model.getAttribute("pageTitle"));
    }

    @Test
    @DisplayName("Should show privacy policy page with correct attributes")
    void testPrivacyPolicyPage() {
        String viewName = legalController.showPrivacyPolicy(model, auth);
        assertEquals("integritetspolicy", viewName);
        assertEquals("Integritetspolicy", model.getAttribute("pageTitle"));
    }

    @Test
    @DisplayName("Should show cookies policy page with correct attributes")
    void testCookiesPolicyPage() {
        String viewName = legalController.showCookiesPolicy(model, auth);
        assertEquals("cookies", viewName);
        assertEquals("Cookies-policy", model.getAttribute("pageTitle"));
    }

    @Test
    @DisplayName("Should show GDPR page with correct attributes")
    void testGdprPage() {
        String viewName = legalController.showGdprInfo(model, auth);
        assertEquals("gdpr", viewName);
        assertEquals("Dina rättigheter enligt GDPR", model.getAttribute("pageTitle"));
    }

    @Test
    @DisplayName("Should show legal index page with correct attributes")
    void testLegalIndexPage() {
        String viewName = legalController.showLegalIndex(model, auth);
        assertEquals("legal/index", viewName);
        assertEquals("Juridisk information", model.getAttribute("pageTitle"));
    }

    @Test
    @DisplayName("Should handle anonymous users")
    void testAnonymousUsers() {
        assertDoesNotThrow(() -> {
            legalController.showTerms(model, null);
            legalController.showPrivacyPolicy(model, null);
            legalController.showCookiesPolicy(model, null);
            legalController.showGdprInfo(model, null);
            legalController.showLegalIndex(model, null);
        });
    }

    @Test
    @DisplayName("Should verify all templates exist")
    void testTemplatesExist() {
        java.io.File termsTemplate = new java.io.File("./src/main/resources/templates/terms.html");
        java.io.File cookiesTemplate = new java.io.File("./src/main/resources/templates/cookies.html");
        java.io.File gdprTemplate = new java.io.File("./src/main/resources/templates/gdpr.html");
        java.io.File legalTemplate = new java.io.File("./src/main/resources/templates/legal/index.html");
        java.io.File privacyTemplate = new java.io.File("./src/main/resources/templates/integritetspolicy.html");

        assertTrue(termsTemplate.exists(), "terms.html template should exist");
        assertTrue(cookiesTemplate.exists(), "cookies.html template should exist");
        assertTrue(gdprTemplate.exists(), "gdpr.html template should exist");
        assertTrue(legalTemplate.exists(), "legal/index.html template should exist");
        assertTrue(privacyTemplate.exists(), "integritetspolicy.html template should exist");
    }
}
