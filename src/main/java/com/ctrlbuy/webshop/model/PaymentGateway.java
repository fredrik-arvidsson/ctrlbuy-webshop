package com.ctrlbuy.webshop.model;

import java.math.BigDecimal;

/**
 * Interface för payment gateway integration
 */
public interface PaymentGateway {

    /**
     * Bearbetar en betalning
     * @param paymentInfo Betalningsinformation
     * @return PaymentResult med resultat av bearbetningen
     */
    PaymentResult processPayment(PaymentInfo paymentInfo);

    /**
     * Återbetalar en transaktion
     * @param transactionId Original transaktions-ID
     * @param amount Belopp att återbetala
     * @return true om återbetalningen lyckades
     */
    boolean refund(String transactionId, BigDecimal amount);

    /**
     * Auktoriserar en betalning (utan att ta pengarna)
     * @param paymentInfo Betalningsinformation
     * @return PaymentResult med auktoriseringskod
     */
    PaymentResult authorizePayment(PaymentInfo paymentInfo);

    /**
     * Avrättnar en auktoriserad betalning
     * @param authorizationId Auktoriserings-ID
     * @param amount Belopp att avrättna
     * @return true om avrättningen lyckades
     */
    boolean capturePayment(String authorizationId, BigDecimal amount);

    /**
     * Avbryter en auktorisering
     * @param authorizationId Auktoriserings-ID
     * @return true om avbrytningen lyckades
     */
    boolean voidAuthorization(String authorizationId);

    /**
     * Verifierar att kortet är giltigt utan att ta betalning
     * @param paymentInfo Betalningsinformation
     * @return PaymentResult med verifieringsresultat
     */
    PaymentResult verifyCard(PaymentInfo paymentInfo);

    /**
     * Kontrollerar att payment gateway är tillgänglig
     * @return true om gateway är online och funktionell
     */
    boolean isHealthy();

    /**
     * Hämtar gateway-specifik information
     * @return Information om payment gateway
     */
    PaymentGatewayInfo getGatewayInfo();

    /**
     * Validerar betalningsinformation innan bearbetning
     * @param paymentInfo Betalningsinformation att validera
     * @return ValidationResult med valideringsresultat
     */
    ValidationResult validatePaymentInfo(PaymentInfo paymentInfo);
}