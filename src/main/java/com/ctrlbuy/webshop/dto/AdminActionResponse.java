
package com.ctrlbuy.webshop.dto;

import java.time.LocalDateTime;

public class AdminActionResponse {

    private boolean success;
    private String message;
    private Long userId;
    private LocalDateTime timestamp;

    public AdminActionResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public AdminActionResponse(boolean success, String message, Long userId) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AdminActionResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                '}';
    }
}