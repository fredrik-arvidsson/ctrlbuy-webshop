package com.ctrlbuy.webshop.dto;

import com.ctrlbuy.webshop.security.entity.User;

/**
 * ✅ UPPDATERAD: RegistrationResult som stödjer båda användningsfallen
 * - Ursprunglig: User + token
 * - Ny: boolean success + message
 */
public class RegistrationResult {
    private final User user;
    private final String token;
    private final boolean success;
    private final String message;

    // ✅ Ursprunglig konstruktor (User + token)
    public RegistrationResult(User user, String token) {
        this.user = user;
        this.token = token;
        this.success = (user != null);
        this.message = null;
    }

    // ✅ Ny konstruktor (boolean + message) - för UserService
    public RegistrationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.user = null;
        this.token = null;
    }

    // ✅ Getters för ursprungliga fält
    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    // ✅ Getters för nya fält
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    // ✅ Utility method för att kontrollera om registreringen lyckades
    public boolean hasUser() {
        return user != null;
    }

    public boolean hasToken() {
        return token != null && !token.isEmpty();
    }

    public boolean hasMessage() {
        return message != null && !message.isEmpty();
    }
}