package com.ctrlbuy.webshop.enums;

/**
 * PaymentType enum för olika betalningsmetoder och transaktionstyper
 * ✅ RAILWAY-KOMPATIBEL: Uppdaterad med befintliga typer + nya funktioner
 */
public enum PaymentType {

    // Kort-baserade betalningar
    CREDIT_CARD("Credit Card", "Kreditkort", true),
    DEBIT_CARD("Debit Card", "Bankkort", true),

    // Digital betalningar
    PAYPAL("PayPal", "PayPal", false),
    STRIPE("Stripe", "Stripe", false),
    MOBILE_PAYMENT("Mobile Payment", "Mobilbetalning", false),
    CRYPTOCURRENCY("Cryptocurrency", "Kryptovaluta", false),

    // Traditionella betalningar
    BANK_TRANSFER("Bank Transfer", "Bankgiro", false),
    CASH("Cash", "Kontant", false),

    // Specialbetalningar
    GIFT_CARD("Gift Card", "Presentkort", false),

    // Återbetalningar och justeringar
    REFUND("Refund", "Återbetalning", false);

    private final String displayName;
    private final String swedishName;
    private final boolean requiresCard;

    PaymentType(String displayName, String swedishName, boolean requiresCard) {
        this.displayName = displayName;
        this.swedishName = swedishName;
        this.requiresCard = requiresCard;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSwedishName() {
        return swedishName;
    }

    public boolean requiresCard() {
        return requiresCard;
    }

    /**
     * Kontrollera om det är en kortbaserad betalning
     */
    public boolean isCardBased() {
        return this == CREDIT_CARD || this == DEBIT_CARD;
    }

    /**
     * Kontrollera om det är en digital betalning
     */
    public boolean isDigital() {
        return this == PAYPAL || this == STRIPE || this == MOBILE_PAYMENT ||
                this == CRYPTOCURRENCY;
    }

    /**
     * Kontrollera om det är en omedelbar betalning
     */
    public boolean isInstant() {
        return this == CREDIT_CARD || this == DEBIT_CARD ||
                this == PAYPAL || this == STRIPE ||
                this == MOBILE_PAYMENT || this == CASH;
    }

    /**
     * Kontrollera om det kräver verifiering
     */
    public boolean requiresVerification() {
        return this == BANK_TRANSFER || this == CRYPTOCURRENCY;
    }

    /**
     * Kontrollera om det är en återbetalningstyp
     */
    public boolean isRefundType() {
        return this == REFUND;
    }

    /**
     * Kontrollera om betalningstypen stöder återbetalningar
     */
    public boolean supportsRefunds() {
        return this != CASH && this != CRYPTOCURRENCY && this != GIFT_CARD;
    }

    /**
     * Kontrollera om det är en säker betalningsmetod
     */
    public boolean isSecure() {
        return this == CREDIT_CARD || this == DEBIT_CARD ||
                this == PAYPAL || this == STRIPE ||
                this == BANK_TRANSFER;
    }

    /**
     * Hämta processing fee i procent (exempel)
     */
    public double getProcessingFeePercent() {
        switch (this) {
            case CREDIT_CARD:
                return 2.9; // 2.9%
            case DEBIT_CARD:
                return 1.5; // 1.5%
            case PAYPAL:
                return 3.4; // 3.4%
            case STRIPE:
                return 2.9; // 2.9%
            case MOBILE_PAYMENT:
                return 2.0; // 2.0%
            case BANK_TRANSFER:
                return 0.5; // 0.5%
            case CRYPTOCURRENCY:
                return 1.0; // 1.0%
            case CASH:
            case GIFT_CARD:
            case REFUND:
                return 0.0; // Ingen avgift
            default:
                return 0.0;
        }
    }

    /**
     * Hämta maximal transaktionsgräns (exempel i SEK)
     */
    public long getMaxTransactionLimit() {
        switch (this) {
            case CREDIT_CARD:
                return 100000; // 100,000 SEK
            case DEBIT_CARD:
                return 50000;  // 50,000 SEK
            case PAYPAL:
                return 200000; // 200,000 SEK
            case STRIPE:
                return 150000; // 150,000 SEK
            case MOBILE_PAYMENT:
                return 25000;  // 25,000 SEK
            case BANK_TRANSFER:
                return 1000000; // 1,000,000 SEK
            case CRYPTOCURRENCY:
                return 500000;  // 500,000 SEK
            case CASH:
                return 10000;   // 10,000 SEK
            case GIFT_CARD:
                return 5000;    // 5,000 SEK
            case REFUND:
                return Long.MAX_VALUE; // Ingen gräns för återbetalningar
            default:
                return 0;
        }
    }

    /**
     * Kontrollera om betalningstypen är tillgänglig i Sverige
     */
    public boolean isAvailableInSweden() {
        // Alla nuvarande typer är tillgängliga i Sverige
        return true;
    }

    /**
     * Hämta ikon-namn för frontend
     */
    public String getIconName() {
        switch (this) {
            case CREDIT_CARD:
            case DEBIT_CARD:
                return "credit-card";
            case PAYPAL:
                return "paypal";
            case STRIPE:
                return "stripe";
            case BANK_TRANSFER:
                return "bank";
            case CASH:
                return "money-bill";
            case CRYPTOCURRENCY:
                return "bitcoin";
            case MOBILE_PAYMENT:
                return "mobile-alt";
            case GIFT_CARD:
                return "gift";
            case REFUND:
                return "undo";
            default:
                return "credit-card";
        }
    }

    @Override
    public String toString() {
        return swedishName;
    }
}