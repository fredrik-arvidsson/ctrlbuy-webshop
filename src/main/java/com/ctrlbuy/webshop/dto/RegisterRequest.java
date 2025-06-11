package com.ctrlbuy.webshop.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Förnamn är obligatoriskt")
    @Size(min = 2, max = 50, message = "Förnamn måste vara mellan 2-50 tecken")
    @Pattern(regexp = "^[a-zA-ZåäöÅÄÖ\\s-]+$", message = "Förnamn får endast innehålla bokstäver, mellanslag och bindestreck")
    private String firstName;

    @NotBlank(message = "Efternamn är obligatoriskt")
    @Size(min = 2, max = 50, message = "Efternamn måste vara mellan 2-50 tecken")
    @Pattern(regexp = "^[a-zA-ZåäöÅÄÖ\\s-]+$", message = "Efternamn får endast innehålla bokstäver, mellanslag och bindestreck")
    private String lastName;

    @NotBlank(message = "Användarnamn är obligatoriskt")
    @Size(min = 3, max = 30, message = "Användarnamn måste vara mellan 3-30 tecken")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Användarnamn får endast innehålla bokstäver, siffror, understreck, punkt och bindestreck")
    private String username;

    @NotBlank(message = "E-postadress är obligatorisk")
    @Email(message = "Ange en giltig e-postadress")
    @Size(max = 100, message = "E-postadress får inte vara längre än 100 tecken")
    private String email;

    @NotBlank(message = "Lösenord är obligatoriskt")
    @Size(min = 6, max = 128, message = "Lösenord måste vara mellan 6-128 tecken")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).*$",
            message = "Lösenord måste innehålla minst en bokstav och en siffra")
    private String password;

    @NotBlank(message = "Bekräfta ditt lösenord")
    private String confirmPassword;

    @AssertTrue(message = "Du måste acceptera användarvillkoren för att registrera dig")
    private boolean acceptTerms = false;

    // ✅ Förbättrade hjälpmetoder

    /**
     * Få hela namnet formaterat
     */
    public String getFullName() {
        if (firstName == null && lastName == null) {
            return "";
        }
        if (firstName == null) {
            return lastName.trim();
        }
        if (lastName == null) {
            return firstName.trim();
        }
        return firstName.trim() + " " + lastName.trim();
    }

    /**
     * Kontrollera om lösenorden matchar
     */
    public boolean passwordsMatch() {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }

    /**
     * Validera lösenordsstyrka
     */
    public boolean hasStrongPassword() {
        if (password == null || password.length() < 6) {
            return false;
        }

        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        return hasLetter && hasDigit && (password.length() >= 8 || hasSpecialChar);
    }

    /**
     * Trimma alla fält för säkrare hantering
     */
    public void trimAllFields() {
        if (firstName != null) {
            firstName = firstName.trim();
        }
        if (lastName != null) {
            lastName = lastName.trim();
        }
        if (username != null) {
            username = username.trim().toLowerCase();
        }
        if (email != null) {
            email = email.trim().toLowerCase();
        }
        // Obs: Trimma INTE lösenord då mellanslag kan vara avsiktliga
    }

    /**
     * Validera att alla obligatoriska fält är ifyllda
     */
    public boolean hasAllRequiredFields() {
        return firstName != null && !firstName.trim().isEmpty() &&
                lastName != null && !lastName.trim().isEmpty() &&
                username != null && !username.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() &&
                password != null && !password.isEmpty() &&
                confirmPassword != null && !confirmPassword.isEmpty();
    }

    /**
     * Få användarnamnet i lowercase för konsistens
     */
    public String getUsernameLowercase() {
        return username != null ? username.toLowerCase() : null;
    }

    /**
     * Få e-posten i lowercase för konsistens
     */
    public String getEmailLowercase() {
        return email != null ? email.toLowerCase() : null;
    }

    /**
     * Kontrollera om användarnamnet innehåller förbjudna ord
     */
    public boolean hasValidUsername() {
        if (username == null) {
            return false;
        }

        String lowerUsername = username.toLowerCase();
        String[] forbiddenWords = {"admin", "administrator", "root", "system", "test", "demo", "guest", "user"};

        for (String forbidden : forbiddenWords) {
            if (lowerUsername.contains(forbidden)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Rensa känslig data (lösenord) för loggning
     */
    public RegisterRequest sanitized() {
        RegisterRequest sanitized = new RegisterRequest();
        sanitized.firstName = this.firstName;
        sanitized.lastName = this.lastName;
        sanitized.username = this.username;
        sanitized.email = this.email;
        sanitized.acceptTerms = this.acceptTerms;
        // Sätt lösenord till null för säker loggning
        sanitized.password = null;
        sanitized.confirmPassword = null;
        return sanitized;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", acceptTerms=" + acceptTerms +
                ", passwordSet=" + (password != null && !password.isEmpty()) +
                ", confirmPasswordSet=" + (confirmPassword != null && !confirmPassword.isEmpty()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegisterRequest that = (RegisterRequest) o;

        if (acceptTerms != that.acceptTerms) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        return email != null ? email.equals(that.email) : that.email == null;
    }

    @Override
    public int hashCode() {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (acceptTerms ? 1 : 0);
        return result;
    }
}