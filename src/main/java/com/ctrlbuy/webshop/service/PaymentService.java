package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.exception.PaymentException;
import com.ctrlbuy.webshop.model.*;
import com.ctrlbuy.webshop.enums.PaymentStatus;
import com.ctrlbuy.webshop.enums.PaymentType;import com.ctrlbuy.webshop.repository.OrderRepository;
import com.ctrlbuy.webshop.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.regex.Pattern;

@Service
@Transactional
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    // Regex för olika korttyper
    private static final Pattern VISA_PATTERN = Pattern.compile("^4[0-9]{12}(?:[0-9]{3})?$");
    private static final Pattern MASTERCARD_PATTERN = Pattern.compile("^5[1-5][0-9]{14}$");
    private static final Pattern AMEX_PATTERN = Pattern.compile("^3[47][0-9]{13}$");

    @Autowired
    private PaymentGateway paymentGateway;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Bearbetar betalning för en order
     */
    public PaymentResult processPaymentForOrder(Order order, PaymentInfo paymentInfo) {
        logger.info("Bearbetar betalning för order: {}", order.getOrderNumber());

        // Validera att belopp matchar
        if (!paymentInfo.getAmount().equals(BigDecimal.valueOf(order.getTotalAmount()))) {
            throw new PaymentException("Belopp matchar inte order. Order: " +
                    BigDecimal.valueOf(order.getTotalAmount()) + ", Betalning: " + paymentInfo.getAmount());
        }

        // Validera betalningsinformation
        validatePaymentInfo(paymentInfo);

        try {
            // Bearbeta betalning
            PaymentResult result = processPayment(paymentInfo);

            if (result.isSuccess()) {
                // Uppdatera order med betalningsinformation
                order.setPaymentStatus(PaymentStatus.COMPLETED);
                order.setTransactionId(result.getTransactionId());
                orderRepository.save(order);

                // Spara betalningsrecord
                Payment payment = createPaymentRecord(order, paymentInfo, result);
                paymentRepository.save(payment);

                logger.info("Betalning genomförd för order {}: {}",
                        order.getOrderNumber(), result.getTransactionId());
            } else {
                order.setPaymentStatus(PaymentStatus.FAILED);
                orderRepository.save(order);

                logger.warn("Betalning misslyckades för order {}: {}",
                        order.getOrderNumber(), result.getErrorMessage());
            }

            return result;

        } catch (Exception e) {
            order.setPaymentStatus(PaymentStatus.FAILED);
            orderRepository.save(order);

            logger.error("Fel vid betalningsbearbetning för order {}: {}",
                    order.getOrderNumber(), e.getMessage());
            throw new PaymentException("Payment gateway fel: " + e.getMessage(), e);
        }
    }

    /**
     * Bearbetar betalning (generisk metod)
     */
    public PaymentResult processPayment(PaymentInfo paymentInfo) {
        logger.debug("Bearbetar betalning för belopp: {}", paymentInfo.getAmount());

        // Validera betalningsinformation
        validatePaymentInfo(paymentInfo);

        try {
            // Maskera kortnummer för loggar
            PaymentInfo maskedInfo = maskPaymentInfo(paymentInfo);
            logger.info("Skickar betalning till gateway: {}", maskedInfo.getCardType());

            // Anropa payment gateway med retry-logik
            PaymentResult result = processWithRetry(paymentInfo, 3);

            if (result.isSuccess()) {
                logger.info("Betalning godkänd: {}", result.getTransactionId());
            } else {
                logger.warn("Betalning avvisad: {}", result.getErrorMessage());
            }

            return result;

        } catch (Exception e) {
            logger.error("Payment gateway error: {}", e.getMessage());
            throw new PaymentException("Payment gateway timeout eller fel", e);
        }
    }

    /**
     * Återbetalar en order
     */
    public boolean refundPayment(Order order) {
        logger.info("Bearbetar återbetalning för order: {}", order.getOrderNumber());

        // Kontrollera att order kan återbetalas
        if (order.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw new PaymentException("Order är redan återbetald");
        }

        if (order.getPaymentStatus() != PaymentStatus.COMPLETED) {
            throw new PaymentException("Endast betalda orders kan återbetalas");
        }

        if (order.getTransactionId() == null) {
            throw new PaymentException("Ingen transaktions-ID hittad för återbetalning");
        }

        try {
            // Anropa payment gateway för återbetalning
            boolean success = paymentGateway.refund(order.getTransactionId(), BigDecimal.valueOf(order.getTotalAmount()));

            if (success) {
                order.setPaymentStatus(PaymentStatus.REFUNDED);
                order.setRefundedAt(LocalDateTime.now());
                orderRepository.save(order);

                // Skapa återbetalningsrecord
                createRefundRecord(order);

                logger.info("Återbetalning genomförd för order {}", order.getOrderNumber());
            } else {
                logger.error("Återbetalning misslyckades för order {}", order.getOrderNumber());
            }

            return success;

        } catch (Exception e) {
            logger.error("Fel vid återbetalning för order {}: {}",
                    order.getOrderNumber(), e.getMessage());
            throw new PaymentException("Återbetalning misslyckades: " + e.getMessage(), e);
        }
    }

    /**
     * Validerar betalningsinformation
     */
    public boolean validatePaymentInfo(PaymentInfo paymentInfo) {
        if (paymentInfo == null) {
            throw new PaymentException("Betalningsinformation saknas");
        }

        // Validera kortnummer
        if (!validateCardNumber(paymentInfo.getCardNumber())) {
            throw new PaymentException("Ogiltigt kortnummer");
        }

        // Validera utgångsdatum
        if (!validateExpiryDate(paymentInfo.getExpiryMonth().toString(), paymentInfo.getExpiryYear().toString())) {
            throw new PaymentException("Kortet har gått ut eller ogiltigt utgångsdatum");
        }

        // Validera CVV
        if (!validateCVV(paymentInfo.getCvv())) {
            throw new PaymentException("Ogiltig CVV-kod");
        }

        // Validera belopp
        if (paymentInfo.getAmount() == null || paymentInfo.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentException("Ogiltigt belopp");
        }

        return true;
    }

    /**
     * Validerar kortnummer med Luhn-algoritmen
     */
    public boolean validateCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return false;
        }

        // Ta bort mellanslag och bindestreck
        String cleanNumber = cardNumber.replaceAll("[\\s-]", "");

        // Kontrollera att det bara innehåller siffror
        if (!cleanNumber.matches("\\d+")) {
            return false;
        }

        // Kontrollera längd (13-19 siffror är vanligt)
        if (cleanNumber.length() < 13 || cleanNumber.length() > 19) {
            return false;
        }

        // Luhn-algoritm för validering
        return luhnCheck(cleanNumber) && validateCardType(cleanNumber);
    }

    /**
     * Validerar utgångsdatum
     */
    public boolean validateExpiryDate(String month, String year) {
        if (month == null || year == null) {
            return false;
        }

        try {
            int monthInt = Integer.parseInt(month);
            int yearInt = Integer.parseInt(year);

            // Kontrollera giltiga månad
            if (monthInt < 1 || monthInt > 12) {
                return false;
            }

            // Hantera 2-siffriga år
            if (yearInt < 100) {
                yearInt += 2000;
            }

            YearMonth cardExpiry = YearMonth.of(yearInt, monthInt);
            YearMonth currentMonth = YearMonth.now();

            return !cardExpiry.isBefore(currentMonth);

        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validerar CVV
     */
    public boolean validateCVV(String cvv) {
        if (cvv == null) {
            return false;
        }

        // CVV ska vara 3-4 siffror
        return cvv.matches("\\d{3,4}");
    }

    /**
     * Förbereder betalningsinformation för säker lagring
     */
    public PaymentInfo preparForStorage(PaymentInfo paymentInfo) {
        PaymentInfo sanitized = new PaymentInfo();

        // Maskera kortnummer (visa bara sista 4 siffror)
        String cardNumber = paymentInfo.getCardNumber();
        if (cardNumber != null && cardNumber.length() > 4) {
            sanitized.setCardNumber("****-****-****-" + cardNumber.substring(cardNumber.length() - 4));
        }

        sanitized.setCardType(paymentInfo.getCardType());
        sanitized.setAmount(paymentInfo.getAmount());
        sanitized.setExpiryMonth(paymentInfo.getExpiryMonth());
        sanitized.setExpiryYear(paymentInfo.getExpiryYear());

        // Ta ALDRIG med CVV i lagring
        sanitized.setCvv(null);

        return sanitized;
    }

    // ===== PRIVATE HELPER METHODS =====

    private PaymentResult processWithRetry(PaymentInfo paymentInfo, int maxRetries) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return paymentGateway.processPayment(paymentInfo);
            } catch (Exception e) {
                lastException = e;
                logger.warn("Betalningsförsök {} misslyckades: {}", attempt, e.getMessage());

                if (attempt < maxRetries) {
                    // Vänta lite mellan försök
                    try {
                        Thread.sleep(1000 * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        throw new PaymentException("Payment gateway timeout efter " + maxRetries + " försök", lastException);
    }

    private boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return (sum % 10) == 0;
    }

    private boolean validateCardType(String cardNumber) {
        // Identifiera korttyp och validera format
        if (VISA_PATTERN.matcher(cardNumber).matches()) {
            return true;
        } else if (MASTERCARD_PATTERN.matcher(cardNumber).matches()) {
            return true;
        } else if (AMEX_PATTERN.matcher(cardNumber).matches()) {
            return true;
        }

        // Acceptera andra korttyper med grundläggande validering
        return cardNumber.length() >= 13 && cardNumber.length() <= 19;
    }

    private String detectCardType(String cardNumber) {
        if (VISA_PATTERN.matcher(cardNumber).matches()) {
            return "VISA";
        } else if (MASTERCARD_PATTERN.matcher(cardNumber).matches()) {
            return "MASTERCARD";
        } else if (AMEX_PATTERN.matcher(cardNumber).matches()) {
            return "AMEX";
        } else {
            return "UNKNOWN";
        }
    }

    private PaymentInfo maskPaymentInfo(PaymentInfo paymentInfo) {
        PaymentInfo masked = new PaymentInfo();
        masked.setCardType(detectCardType(paymentInfo.getCardNumber()));
        masked.setAmount(paymentInfo.getAmount());
        // Kortnummer visas inte i loggar
        return masked;
    }

    private Payment createPaymentRecord(Order order, PaymentInfo paymentInfo, PaymentResult result) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(paymentInfo.getAmount());
        payment.setCardType(detectCardType(paymentInfo.getCardNumber()));
        payment.setTransactionId(result.getTransactionId());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setProcessedAt(LocalDateTime.now());

        // Maskera kortnummer
        payment.setMaskedCardNumber("****-****-****-" +
                paymentInfo.getCardNumber().substring(paymentInfo.getCardNumber().length() - 4));

        return payment;
    }

    private void createRefundRecord(Order order) {
        Payment refund = new Payment();
        refund.setOrder(order);
        refund.setAmount(BigDecimal.valueOf(order.getTotalAmount()).negate()); // Negativt belopp för återbetalning
        refund.setTransactionId(order.getTransactionId() + "-REFUND");
        refund.setStatus(PaymentStatus.REFUNDED);
        refund.setProcessedAt(LocalDateTime.now());
        refund.setType(com.ctrlbuy.webshop.enums.PaymentType.REFUND);

        paymentRepository.save(refund);
    }
}