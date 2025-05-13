package com.ctrlbuy.webshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

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
}