package com.ctrlbuy.webshop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Diagnostic Controller Unit Tests")
class DiagnosticControllerTest {

    @Mock
    private Model model;

    @InjectMocks
    private DiagnosticController diagnosticController;

    @Test
    @DisplayName("Diagnostic test endpoint")
    void testDiagnosticTest() {
        // Act
        String viewName = diagnosticController.test(model);

        // Assert
        assertEquals("home", viewName);
        verify(model).addAttribute("message", "Diagnostics test is working!");
        
        System.out.println("✅ Diagnostic test endpoint - PASS");
    }
}
