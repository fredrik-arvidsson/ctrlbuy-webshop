package com.ctrlbuy.webshop.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResult {

    private boolean success;
    private String message;
    private Long userId;  // ID för den skapade användaren (om lyckad)
    private String errorCode; // Specifik felkod för frontend-hantering

    /**
     * Skapa ett lyckat resultat
     */
    public static RegistrationResult success(String message) {
        return new RegistrationResult(true, message, null, null);
    }

    /**
     * Skapa ett lyckat resultat med användar-ID
     */
    public static RegistrationResult success(String message, Long userId) {
        return new RegistrationResult(true, message, userId, null);
    }

    /**
     * Skapa ett misslyckat resultat
     */
    public static RegistrationResult failure(String message) {
        return new RegistrationResult(false, message, null, null);
    }

    /**
     * Skapa ett misslyckat resultat med felkod
     */
    public static RegistrationResult failure(String message, String errorCode) {
        return new RegistrationResult(false, message, null, errorCode);
    }

    /**
     * Kontrollera om registreringen lyckades
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Kontrollera om registreringen misslyckades
     */
    public boolean isFailure() {
        return !success;
    }

    @Override
    public String toString() {
        return "RegistrationResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", userId=" + userId +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }
}