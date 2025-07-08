package com.ctrlbuy.webshop.dto;

public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private boolean acceptTerms;

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public boolean isAcceptTerms() { return acceptTerms; }
    public void setAcceptTerms(boolean acceptTerms) { this.acceptTerms = acceptTerms; }

    // Utility methods
    public boolean hasAllRequiredFields() {
        return username != null && email != null && password != null && 
               firstName != null && lastName != null && acceptTerms;
    }

    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }

    public void trimAllFields() {
        if (username != null) username = username.trim();
        if (email != null) email = email.trim();
        if (firstName != null) firstName = firstName.trim();
        if (lastName != null) lastName = lastName.trim();
    }

    public RegisterRequest sanitized() {
        trimAllFields();
        return this;
    }
}
