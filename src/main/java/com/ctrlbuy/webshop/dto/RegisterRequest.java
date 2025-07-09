package com.ctrlbuy.webshop.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Användarnamn är obligatoriskt")
    @Size(min = 3, max = 50, message = "Användarnamn måste vara mellan 3 och 50 tecken")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Användarnamn får endast innehålla bokstäver, siffror, underscore och bindestreck")
    private String username;

    @NotBlank(message = "E-post är obligatorisk")
    @Email(message = "Ogiltig e-postadress")
    @Size(max = 100, message = "E-postadressen är för lång")
    private String email;

    @NotBlank(message = "Förnamn är obligatoriskt")
    @Size(min = 1, max = 100, message = "Förnamn måste vara mellan 1 och 100 tecken")
    private String firstName;

    @NotBlank(message = "Efternamn är obligatoriskt")
    @Size(min = 1, max = 100, message = "Efternamn måste vara mellan 1 och 100 tecken")
    private String lastName;

    @NotBlank(message = "Lösenord är obligatoriskt")
    @Size(min = 6, message = "Lösenord måste vara minst 6 tecken")
    private String password;

    @NotBlank(message = "Bekräfta lösenord")
    private String confirmPassword;

    // Terms and conditions acceptance
    private boolean acceptTerms = false;

    /**
     * Trimma alla textfält för att ta bort extra mellanslag
     */
    public void trimAllFields() {
        if (this.username != null) {
            this.username = this.username.trim();
        }
        if (this.email != null) {
            this.email = this.email.trim();
        }
        if (this.firstName != null) {
            this.firstName = this.firstName.trim();
        }
        if (this.lastName != null) {
            this.lastName = this.lastName.trim();
        }
        // Trimma INTE lösenord - kan innehålla betydelsefulla mellanslag
    }

    /**
     * Kontrollera om lösenorden matchar
     */
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }

    /**
     * Få fullständigt namn
     */
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        if (firstName != null && !firstName.trim().isEmpty()) {
            fullName.append(firstName.trim());
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            if (fullName.length() > 0) {
                fullName.append(" ");
            }
            fullName.append(lastName.trim());
        }
        return fullName.toString();
    }

    /**
     * Validera att alla obligatoriska fält är ifyllda
     */
    public boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() &&
                firstName != null && !firstName.trim().isEmpty() &&
                lastName != null && !lastName.trim().isEmpty() &&
                password != null && password.length() >= 6 &&
                confirmPassword != null && isPasswordMatching() &&
                acceptTerms; // Terms måste vara accepterade
    }

    /**
     * Kontrollera om användarvillkoren är accepterade
     */
    public boolean isAcceptTerms() {
        return acceptTerms;
    }

    /**
     * Sätt användarvillkor
     */
    public void setAcceptTerms(boolean acceptTerms) {
        this.acceptTerms = acceptTerms;
    }

    /**
     * Normalisera e-post (gemener och trimning)
     */
    public void normalizeEmail() {
        if (this.email != null) {
            this.email = this.email.trim().toLowerCase();
        }
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='[PROTECTED]'" +
                ", confirmPassword='[PROTECTED]'" +
                '}';
    }
}