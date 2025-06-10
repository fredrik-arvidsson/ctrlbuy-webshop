package com.ctrlbuy.webshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Förnamn får inte vara tomt")
    @Size(min = 2, max = 30, message = "Förnamn måste vara mellan 2 och 30 tecken")
    private String firstName;

    @NotBlank(message = "Efternamn får inte vara tomt")
    @Size(min = 2, max = 30, message = "Efternamn måste vara mellan 2 och 30 tecken")
    private String lastName;

    @NotBlank(message = "Användarnamn får inte vara tomt")
    @Size(min = 3, max = 20, message = "Användarnamn måste vara mellan 3 och 20 tecken")
    private String username;

    @NotBlank(message = "E-postadress får inte vara tom")
    @Email(message = "Ogiltigt e-postformat")
    private String email;

    @NotBlank(message = "Lösenord får inte vara tomt")
    @Size(min = 6, message = "Lösenordet måste vara minst 6 tecken långt")
    private String password;

    @NotBlank(message = "Bekräfta lösenord får inte vara tomt")
    private String confirmPassword;

    // ✅ Hjälpmetoder

    /**
     * Få hela namnet
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Kontrollera om lösenorden matchar
     */
    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }

    /**
     * Trimma alla fält för säkrare hantering
     */
    public void trimAllFields() {
        if (firstName != null) firstName = firstName.trim();
        if (lastName != null) lastName = lastName.trim();
        if (username != null) username = username.trim();
        if (email != null) email = email.trim().toLowerCase();
        if (password != null) password = password.trim();
        if (confirmPassword != null) confirmPassword = confirmPassword.trim();
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}