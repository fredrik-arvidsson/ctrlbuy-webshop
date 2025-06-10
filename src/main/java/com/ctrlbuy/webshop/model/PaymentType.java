package com.ctrlbuy.webshop.model;

/**
 * Enum för typ av betalning
 */
public enum PaymentType {
    PAYMENT("Betalning", "Standard betalning för order"),
    REFUND("Återbetalning", "Fullständig återbetalning"),
    PARTIAL_REFUND("Delvis återbetalning", "Partiell återbetalning"),
    AUTHORIZATION("Auktorisering", "Auktorisering av betalning"),
    CAPTURE("Avräkning", "Avräkning av auktoriserad betalning"),
    CHARGEBACK("Återkrav", "Återkrav från kund"),
    ADJUSTMENT("Justering", "Manuell justering av betalning");

    private final String displayName;
    private final String description;

    PaymentType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRefundType() {
        return this == REFUND || this == PARTIAL_REFUND;
    }

    public boolean isPaymentType() {
        return this == PAYMENT || this == AUTHORIZATION || this == CAPTURE;
    }
}