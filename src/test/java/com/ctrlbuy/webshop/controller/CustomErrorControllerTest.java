package com.ctrlbuy.webshop.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.Model;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomErrorControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @InjectMocks
    private CustomErrorController customErrorController;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(request, model);
    }

    @Test
    void handleError_WithAllErrorAttributes_ShouldSetCorrectModelAttributes() {
        // Arrange
        Integer statusCode = 404;
        RuntimeException exception = new RuntimeException("Test exception");
        String errorMessage = "Page not found";
        String requestUri = "/test-page";

        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(statusCode);
        when(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)).thenReturn(exception);
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn(errorMessage);
        when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn(requestUri);
        when(model.containsAttribute("isAuthenticated")).thenReturn(false);

        // Act
        String result = customErrorController.handleError(request, model);

        // Assert
        assertEquals("simple-error", result);
        verify(model).addAttribute("status", statusCode);
        verify(model).addAttribute("error", exception.toString());
        verify(model).addAttribute("message", errorMessage);
        verify(model).addAttribute("path", requestUri);
        verify(model).addAttribute("isAuthenticated", false);
    }

    @Test
    void handleError_WithNullAttributes_ShouldSetDefaultValues() {
        // Arrange
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(null);
        when(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)).thenReturn(null);
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn(null);
        when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn(null);
        when(model.containsAttribute("isAuthenticated")).thenReturn(false);

        // Act
        String result = customErrorController.handleError(request, model);

        // Assert
        assertEquals("simple-error", result);
        verify(model).addAttribute("status", "Unknown");
        verify(model).addAttribute("error", "Unknown error");
        verify(model).addAttribute("message", "No error message available");
        verify(model).addAttribute("path", "Unknown path");
        verify(model).addAttribute("isAuthenticated", false);
    }

    @Test
    void handleError_WithLoginProcessError_ShouldRedirectToLoginWithParam() {
        // Arrange
        String loginProcessUri = "/login-process";
        when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn(loginProcessUri);
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(405);
        when(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)).thenReturn(null);
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn("Method not allowed");

        // Act
        String result = customErrorController.handleError(request, model);

        // Assert
        assertEquals("redirect:/login?methodNotAllowed=true", result);
        
        // Verify that model attributes are still set even though we redirect
        verify(model).addAttribute("status", 405);
        verify(model).addAttribute("error", "Unknown error");
        verify(model).addAttribute("message", "Method not allowed");
        verify(model).addAttribute("path", loginProcessUri);
    }

    @Test
    void handleError_WithLoginProcessInMiddleOfUri_ShouldRedirectToLogin() {
        // Arrange
        String uriWithLoginProcess = "/some/path/login-process/more";
        when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn(uriWithLoginProcess);
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(500);
        when(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)).thenReturn(new Exception("Server error"));
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn("Internal error");

        // Act
        String result = customErrorController.handleError(request, model);

        // Assert
        assertEquals("redirect:/login?methodNotAllowed=true", result);
    }

    @Test
    void handleError_WithIsAuthenticatedAlreadySet_ShouldNotOverride() {
        // Arrange
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(500);
        when(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)).thenReturn(null);
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn("Server Error");
        when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/api/test");
        when(model.containsAttribute("isAuthenticated")).thenReturn(true); // Already set

        // Act
        String result = customErrorController.handleError(request, model);

        // Assert
        assertEquals("simple-error", result);
        verify(model, never()).addAttribute("isAuthenticated", false);
        verify(model).addAttribute("status", 500);
        verify(model).addAttribute("error", "Unknown error");
        verify(model).addAttribute("message", "Server Error");
        verify(model).addAttribute("path", "/api/test");
    }

    @Test
    void handleError_With500Error_ShouldLogAndReturnSimpleError() {
        // Arrange
        Integer statusCode = 500;
        Exception exception = new IllegalStateException("Database connection failed");
        String errorMessage = "Internal Server Error";
        String requestUri = "/api/products";

        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(statusCode);
        when(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)).thenReturn(exception);
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn(errorMessage);
        when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn(requestUri);
        when(model.containsAttribute("isAuthenticated")).thenReturn(false);

        // Act
        String result = customErrorController.handleError(request, model);

        // Assert
        assertEquals("simple-error", result);
        verify(model).addAttribute("status", statusCode);
        verify(model).addAttribute("error", exception.toString());
        verify(model).addAttribute("message", errorMessage);
        verify(model).addAttribute("path", requestUri);
    }

    @Test
    void handleError_WithEmptyStringUri_ShouldNotRedirectToLogin() {
        // Arrange
        String emptyUri = "";
        when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn(emptyUri);
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(404);
        when(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)).thenReturn(null);
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn("Not found");
        when(model.containsAttribute("isAuthenticated")).thenReturn(false);

        // Act
        String result = customErrorController.handleError(request, model);

        // Assert
        assertEquals("simple-error", result);
        verify(model).addAttribute("path", emptyUri);
    }

    @Test
    void handleError_WithComplexException_ShouldHandleToString() {
        // Arrange
        Exception complexException = new RuntimeException("Complex error with details", 
                                        new IllegalArgumentException("Root cause"));
        when(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)).thenReturn(complexException);
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(400);
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn("Bad request");
        when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/test");
        when(model.containsAttribute("isAuthenticated")).thenReturn(false);

        // Act
        String result = customErrorController.handleError(request, model);

        // Assert
        assertEquals("simple-error", result);
        verify(model).addAttribute("error", complexException.toString());
        verify(model).addAttribute("status", 400);
        verify(model).addAttribute("message", "Bad request");
        verify(model).addAttribute("path", "/test");
    }
}
