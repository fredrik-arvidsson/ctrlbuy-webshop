package com.ctrlbuy.webshop.exception;

/**
 * Exception som kastas vid betalningsrelaterade fel
 */
public class PaymentException extends RuntimeException {

    private String errorCode;
    private String userMessage;

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public PaymentException(String message, String errorCode, String userMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getUserMessage() {
        return userMessage != null ? userMessage : getMessage();
    }
}