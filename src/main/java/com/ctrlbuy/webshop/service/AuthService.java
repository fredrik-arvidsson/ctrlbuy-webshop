package com.ctrlbuy.webshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private LoggingService loggingService;

    // Metoder för autentisering (t.ex. login, register)
    public boolean authenticate(String username, String password) {
        long startTime = System.currentTimeMillis();

        try {
            // Logik för autentisering, exempelvis jämförelse mot en databas
            boolean isAuthenticated = username.equals("admin") && password.equals("admin");

            if (isAuthenticated) {
                loggingService.logUserAction(username, "LOGIN", "Successful authentication");
            } else {
                loggingService.logUserAction(username, "LOGIN_FAILED", "Authentication failed");
            }

            return isAuthenticated;

        } catch (Exception e) {
            loggingService.logError("authenticate", e);
            return false;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logPerformance("authenticate", duration);
        }
    }

    // Ny metod för user registration med monitoring
    public boolean registerUser(String username, String password, String email) {
        long startTime = System.currentTimeMillis();

        try {
            // Här skulle du normalt spara till databas
            // För demo-syfte, låt oss bara validera input
            if (username != null && !username.trim().isEmpty() &&
                    password != null && password.length() >= 6) {

                loggingService.logUserAction(username, "REGISTER",
                        "New user registered with email: " + email);

                return true;
            } else {
                loggingService.logUserAction(username, "REGISTER_FAILED",
                        "Registration failed - invalid input");
                return false;
            }

        } catch (Exception e) {
            loggingService.logError("registerUser", e);
            return false;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logPerformance("registerUser", duration);
        }
    }

    // Hjälpmetod för att logga user actions
    public void logUserActivity(String username, String activity, String details) {
        loggingService.logUserAction(username, activity, details);
    }

    // Metod för att validera user input
    public boolean validateCredentials(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            loggingService.logSystemEvent("VALIDATION", "Empty username provided");
            return false;
        }

        if (password == null || password.length() < 6) {
            loggingService.logSystemEvent("VALIDATION", "Password too short for user: " + username);
            return false;
        }

        return true;
    }
}