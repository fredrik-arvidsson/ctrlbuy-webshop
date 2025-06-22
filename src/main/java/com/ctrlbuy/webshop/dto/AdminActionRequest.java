// AdminActionRequest.java
package com.ctrlbuy.webshop.dto;

import java.time.LocalDateTime;

public class AdminActionRequest {

    private Long userId;
    private String email;

    // Konstruktorer
    public AdminActionRequest() {}

    public AdminActionRequest(Long userId) {
        this.userId = userId;
    }

    public AdminActionRequest(String email) {
        this.email = email;
    }

    public AdminActionRequest(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "AdminActionRequest{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                '}';
    }
}