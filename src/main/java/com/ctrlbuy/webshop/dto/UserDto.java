package com.ctrlbuy.webshop.dto;

public class UserDto {
    private Long id;
    private String username;
    private String email;

    // Konstruktörer
    public UserDto() {}

    public UserDto(Long id, String username, String email) {
        this.setId(id);
        this.setUsername(username);
        this.setEmail(email);
    }

    // Getter/Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
