package com.ctrlbuy.webshop.service;

public enum PaymentType {
    CREDIT_CARD("Credit Card Payment", true),
    DEBIT_CARD("Debit Card Payment", true),
    PAYPAL("PayPal Payment", false),
    STRIPE("Stripe Payment", true),
    BANK_TRANSFER("Bank Transfer", false),
    CASH("Cash Payment", false),
    CRYPTOCURRENCY("Cryptocurrency Payment", false),
    MOBILE_PAYMENT("Mobile Payment", true),
    GIFT_CARD("Gift Card Payment", false);

    private final String displayName;
    private final boolean requiresCardDetails;

    PaymentType(String displayName, boolean requiresCardDetails) {
        this.displayName = displayName;
        this.requiresCardDetails = requiresCardDetails;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean requiresCardDetails() {
        return requiresCardDetails;
    }

    // Helper methods
    public boolean isCardPayment() {
        return this == CREDIT_CARD || this == DEBIT_CARD;
    }

    public boolean isDigitalWallet() {
        return this == PAYPAL || this == MOBILE_PAYMENT;
    }

    public boolean isInstantPayment() {
        return requiresCardDetails || isDigitalWallet();
    }

    public boolean requiresManualVerification() {
        return this == BANK_TRANSFER || this == CRYPTOCURRENCY;
    }
}