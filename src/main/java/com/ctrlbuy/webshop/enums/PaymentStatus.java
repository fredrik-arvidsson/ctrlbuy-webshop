package com.ctrlbuy.webshop.enums;

/**
 * PaymentStatus enum för betalningsstatus med svenska namn och business logic
 * ✅ RAILWAY-KOMPATIBEL: Uppdaterad med befintliga typer + nya funktioner
 */
public enum PaymentStatus {
    PENDING("Pending", "Väntande", "Betalning väntar på att behandlas"),
    PROCESSING("Processing", "Behandlas", "Betalning behandlas för närvarande"),
    COMPLETED("Completed", "Genomförd", "Betalning har genomförts framgångsrikt"),
    FAILED("Failed", "Misslyckad", "Betalning misslyckades"),
    CANCELLED("Cancelled", "Avbruten", "Betalning avbröts av användare eller system"),
    REFUNDED("Refunded", "Återbetald", "Betalning har återbetalats helt"),
    PARTIALLY_REFUNDED("Partially Refunded", "Delvis återbetald", "Betalning har återbetalats delvis");

    private final String code;
    private final String swedishName;
    private final String description;

    PaymentStatus(String code, String swedishName, String description) {
        this.code = code;
        this.swedishName = swedishName;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getSwedishName() {
        return swedishName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Kontrollera om betalningen är lyckad
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }

    /**
     * Kontrollera om betalningen är misslyckad
     */
    public boolean isFailed() {
        return this == FAILED || this == CANCELLED;
    }

    /**
     * Kontrollera om betalningen pågår
     */
    public boolean isInProgress() {
        return this == PENDING || this == PROCESSING;
    }

    /**
     * Kontrollera om betalningen är slutförd (antingen lyckad eller misslyckad)
     */
    public boolean isFinalized() {
        return this == COMPLETED || this == FAILED ||
                this == CANCELLED || this == REFUNDED;
    }

    /**
     * Kontrollera om betalningen kan avbrytas
     */
    public boolean canBeCancelled() {
        return this == PENDING;
    }

    /**
     * Kontrollera om betalningen kan återbetalas
     */
    public boolean canBeRefunded() {
        return this == COMPLETED || this == PARTIALLY_REFUNDED;
    }

    /**
     * Kontrollera om betalningen har någon form av återbetalning
     */
    public boolean hasRefund() {
        return this == REFUNDED || this == PARTIALLY_REFUNDED;
    }

    /**
     * Kontrollera om det är en aktiv betalning (inte avbruten eller återbetald)
     */
    public boolean isActive() {
        return this == PENDING || this == PROCESSING || this == COMPLETED;
    }

    /**
     * Kontrollera om betalningen behöver åtgärd från användare
     */
    public boolean requiresUserAction() {
        return this == FAILED || this == CANCELLED;
    }

    /**
     * Kontrollera om betalningen bidrar till intäkter
     */
    public boolean contributesToRevenue() {
        return this == COMPLETED || this == PARTIALLY_REFUNDED;
    }

    /**
     * Hämta nästa möjliga statusar från nuvarande status
     */
    public PaymentStatus[] getNextPossibleStatuses() {
        switch (this) {
            case PENDING:
                return new PaymentStatus[]{PROCESSING, FAILED, CANCELLED};
            case PROCESSING:
                return new PaymentStatus[]{COMPLETED, FAILED};
            case COMPLETED:
                return new PaymentStatus[]{REFUNDED, PARTIALLY_REFUNDED};
            case FAILED:
            case CANCELLED:
                return new PaymentStatus[]{PENDING}; // Kan försöka igen
            case REFUNDED:
                return new PaymentStatus[]{}; // Terminal status
            case PARTIALLY_REFUNDED:
                return new PaymentStatus[]{REFUNDED};
            default:
                return new PaymentStatus[]{};
        }
    }

    /**
     * Kontrollera om transition till ny status är giltig
     */
    public boolean canTransitionTo(PaymentStatus newStatus) {
        PaymentStatus[] possibleStatuses = getNextPossibleStatuses();
        for (PaymentStatus status : possibleStatuses) {
            if (status == newStatus) {
                return true;
            }
        }
        return false;
    }

    /**
     * Hämta CSS-klass för styling
     */
    public String getCssClass() {
        switch (this) {
            case PENDING:
                return "payment-pending";
            case PROCESSING:
                return "payment-processing";
            case COMPLETED:
                return "payment-success";
            case FAILED:
                return "payment-failed";
            case CANCELLED:
                return "payment-cancelled";
            case REFUNDED:
                return "payment-refunded";
            case PARTIALLY_REFUNDED:
                return "payment-partial-refund";
            default:
                return "payment-unknown";
        }
    }

    /**
     * Hämta ikon-namn för frontend
     */
    public String getIconName() {
        switch (this) {
            case PENDING:
                return "clock";
            case PROCESSING:
                return "spinner";
            case COMPLETED:
                return "check-circle";
            case FAILED:
                return "times-circle";
            case CANCELLED:
                return "ban";
            case REFUNDED:
                return "undo";
            case PARTIALLY_REFUNDED:
                return "undo-alt";
            default:
                return "question-circle";
        }
    }

    /**
     * Hämta färgkod för status
     */
    public String getColorCode() {
        switch (this) {
            case PENDING:
                return "#FFA500"; // Orange
            case PROCESSING:
                return "#007BFF"; // Blue
            case COMPLETED:
                return "#28A745"; // Green
            case FAILED:
                return "#DC3545"; // Red
            case CANCELLED:
                return "#6C757D"; // Gray
            case REFUNDED:
                return "#17A2B8"; // Cyan
            case PARTIALLY_REFUNDED:
                return "#20C997"; // Teal
            default:
                return "#6C757D"; // Gray
        }
    }

    /**
     * Kontrollera om status är terminal (ingen ytterligare förändring möjlig)
     */
    public boolean isTerminal() {
        return this == REFUNDED || (this == FAILED && getNextPossibleStatuses().length == 0);
    }

    /**
     * Hämta prioritet för sortering (lägre nummer = högre prioritet)
     */
    public int getSortPriority() {
        switch (this) {
            case PROCESSING:
                return 1;
            case PENDING:
                return 2;
            case FAILED:
                return 3;
            case CANCELLED:
                return 4;
            case COMPLETED:
                return 5;
            case PARTIALLY_REFUNDED:
                return 6;
            case REFUNDED:
                return 7;
            default:
                return 99;
        }
    }

    @Override
    public String toString() {
        return swedishName;
    }
}