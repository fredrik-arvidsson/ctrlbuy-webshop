package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.enums.PaymentStatus;
import com.ctrlbuy.webshop.enums.PaymentType;
import com.ctrlbuy.webshop.exception.PaymentException;
import com.ctrlbuy.webshop.model.*;
import com.ctrlbuy.webshop.repository.OrderRepository;
import com.ctrlbuy.webshop.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Order testOrder;
    private PaymentInfo testPaymentInfo;
    private PaymentResult successResult;
    private PaymentResult failureResult;

    @BeforeEach
    void setUp() {
        // Setup test order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNumber("ORDER-123");
        testOrder.setTotalAmount(99.99);
        testOrder.setPaymentStatus(PaymentStatus.PENDING);

        // Setup test payment info
        testPaymentInfo = new PaymentInfo();
        testPaymentInfo.setAmount(BigDecimal.valueOf(99.99));
        testPaymentInfo.setCardNumber("4532015112830366"); // Valid Visa test number
        testPaymentInfo.setCvv("123");
        testPaymentInfo.setExpiryMonth(12);
        testPaymentInfo.setExpiryYear(2025);
        testPaymentInfo.setOrderId(1L);

        // Setup payment results
        successResult = PaymentResult.success("TXN-12345", "Payment successful");
        failureResult = PaymentResult.failure("CARD_DECLINED", "Card was declined");
    }

    // ===== PROCESS PAYMENT FOR ORDER TESTS =====

    @Test
    void processPaymentForOrder_Success() {
        // Given
        when(paymentGateway.processPayment(any(PaymentInfo.class))).thenReturn(successResult);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(paymentRepository.save(any(Payment.class))).thenReturn(new Payment());

        // When
        PaymentResult result = paymentService.processPaymentForOrder(testOrder, testPaymentInfo);

        // Then
        assertTrue(result.isSuccess());
        assertEquals("TXN-12345", result.getTransactionId());
        assertEquals(PaymentStatus.COMPLETED, testOrder.getPaymentStatus());
        assertEquals("TXN-12345", testOrder.getTransactionId());

        verify(orderRepository, times(1)).save(testOrder);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void processPaymentForOrder_AmountMismatch() {
        // Given
        testPaymentInfo.setAmount(BigDecimal.valueOf(150.00)); // Different from order amount

        // When & Then
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.processPaymentForOrder(testOrder, testPaymentInfo));

        assertTrue(exception.getMessage().contains("Belopp matchar inte order"));
        verify(paymentGateway, never()).processPayment(any());
    }

    @Test
    void processPaymentForOrder_PaymentFailure() {
        // Given
        when(paymentGateway.processPayment(any(PaymentInfo.class))).thenReturn(failureResult);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        PaymentResult result = paymentService.processPaymentForOrder(testOrder, testPaymentInfo);

        // Then
        assertFalse(result.isSuccess());
        assertEquals("CARD_DECLINED", result.getErrorCode());
        assertEquals(PaymentStatus.FAILED, testOrder.getPaymentStatus());
        assertNull(testOrder.getTransactionId());

        verify(orderRepository, times(1)).save(testOrder);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void processPaymentForOrder_GatewayException() {
        // Given
        when(paymentGateway.processPayment(any(PaymentInfo.class)))
                .thenThrow(new RuntimeException("Gateway timeout"));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When & Then
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.processPaymentForOrder(testOrder, testPaymentInfo));

        assertTrue(exception.getMessage().contains("Payment gateway fel"));
        assertEquals(PaymentStatus.FAILED, testOrder.getPaymentStatus());
        verify(orderRepository, times(1)).save(testOrder);
    }

    // ===== PROCESS PAYMENT TESTS =====

    @Test
    void processPayment_Success() {
        // Given
        when(paymentGateway.processPayment(any(PaymentInfo.class))).thenReturn(successResult);

        // When
        PaymentResult result = paymentService.processPayment(testPaymentInfo);

        // Then
        assertTrue(result.isSuccess());
        assertEquals("TXN-12345", result.getTransactionId());
        verify(paymentGateway, atLeastOnce()).processPayment(testPaymentInfo);
    }

    @Test
    void processPayment_RetryLogic() {
        // Given - First two calls fail, third succeeds
        when(paymentGateway.processPayment(any(PaymentInfo.class)))
                .thenThrow(new RuntimeException("Network error"))
                .thenThrow(new RuntimeException("Timeout"))
                .thenReturn(successResult);

        // When
        PaymentResult result = paymentService.processPayment(testPaymentInfo);

        // Then
        assertTrue(result.isSuccess());
        verify(paymentGateway, times(3)).processPayment(testPaymentInfo);
    }

    @Test
    void processPayment_MaxRetriesExceeded() {
        // Given - All calls fail
        when(paymentGateway.processPayment(any(PaymentInfo.class)))
                .thenThrow(new RuntimeException("Network error"));

        // When & Then
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.processPayment(testPaymentInfo));

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Payment gateway timeout") ||
                exception.getMessage().contains("försök"));
        verify(paymentGateway, times(3)).processPayment(testPaymentInfo);
    }

    // ===== REFUND PAYMENT TESTS =====

    @Test
    void refundPayment_Success() {
        // Given
        testOrder.setPaymentStatus(PaymentStatus.COMPLETED);
        testOrder.setTransactionId("TXN-12345");
        when(paymentGateway.refund(anyString(), any(BigDecimal.class))).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(paymentRepository.save(any(Payment.class))).thenReturn(new Payment());

        // When
        boolean result = paymentService.refundPayment(testOrder);

        // Then
        assertTrue(result);
        assertEquals(PaymentStatus.REFUNDED, testOrder.getPaymentStatus());
        assertNotNull(testOrder.getRefundedAt());
        verify(paymentGateway, times(1)).refund("TXN-12345", BigDecimal.valueOf(99.99));
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void refundPayment_AlreadyRefunded() {
        // Given
        testOrder.setPaymentStatus(PaymentStatus.REFUNDED);

        // When & Then
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.refundPayment(testOrder));

        assertEquals("Order är redan återbetald", exception.getMessage());
        verify(paymentGateway, never()).refund(anyString(), any());
    }

    @Test
    void refundPayment_NotCompleted() {
        // Given
        testOrder.setPaymentStatus(PaymentStatus.PENDING);

        // When & Then
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.refundPayment(testOrder));

        assertEquals("Endast betalda orders kan återbetalas", exception.getMessage());
        verify(paymentGateway, never()).refund(anyString(), any());
    }

    @Test
    void refundPayment_NoTransactionId() {
        // Given
        testOrder.setPaymentStatus(PaymentStatus.COMPLETED);
        testOrder.setTransactionId(null);

        // When & Then
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.refundPayment(testOrder));

        assertEquals("Ingen transaktions-ID hittad för återbetalning", exception.getMessage());
        verify(paymentGateway, never()).refund(anyString(), any());
    }

    @Test
    void refundPayment_GatewayFailure() {
        // Given
        testOrder.setPaymentStatus(PaymentStatus.COMPLETED);
        testOrder.setTransactionId("TXN-12345");
        when(paymentGateway.refund(anyString(), any(BigDecimal.class))).thenReturn(false);

        // When
        boolean result = paymentService.refundPayment(testOrder);

        // Then
        assertFalse(result);
        assertEquals(PaymentStatus.COMPLETED, testOrder.getPaymentStatus()); // Should remain unchanged
        verify(orderRepository, never()).save(testOrder);
    }

    // ===== VALIDATE PAYMENT INFO TESTS =====

    @Test
    void validatePaymentInfo_Success() {
        // When
        boolean result = paymentService.validatePaymentInfo(testPaymentInfo);

        // Then
        assertTrue(result);
    }

    @Test
    void validatePaymentInfo_NullPaymentInfo() {
        // When & Then
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.validatePaymentInfo(null));

        assertEquals("Betalningsinformation saknas", exception.getMessage());
    }

    @Test
    void validatePaymentInfo_InvalidCardNumber() {
        // Given
        testPaymentInfo.setCardNumber("1234"); // Too short

        // When & Then
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.validatePaymentInfo(testPaymentInfo));

        assertEquals("Ogiltigt kortnummer", exception.getMessage());
    }

    @Test
    void validatePaymentInfo_ExpiredCard() {
        // Given
        testPaymentInfo.setExpiryMonth(1);
        testPaymentInfo.setExpiryYear(2020); // Expired

        // When & Then
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.validatePaymentInfo(testPaymentInfo));

        assertEquals("Kortet har gått ut eller ogiltigt utgångsdatum", exception.getMessage());
    }

    @Test
    void validatePaymentInfo_InvalidCVV() {
        // Given
        testPaymentInfo.setCvv("12"); // Too short

        // When & Then
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.validatePaymentInfo(testPaymentInfo));

        assertEquals("Ogiltig CVV-kod", exception.getMessage());
    }

    @Test
    void validatePaymentInfo_InvalidAmount() {
        // Given
        testPaymentInfo.setAmount(BigDecimal.ZERO);

        // When & Then
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.validatePaymentInfo(testPaymentInfo));

        assertEquals("Ogiltigt belopp", exception.getMessage());
    }

    @Test
    void validatePaymentInfo_NegativeAmount() {
        // Given
        testPaymentInfo.setAmount(BigDecimal.valueOf(-10.00));

        // When & Then
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.validatePaymentInfo(testPaymentInfo));

        assertEquals("Ogiltigt belopp", exception.getMessage());
    }

    // ===== VALIDATE CARD NUMBER TESTS =====

    @Test
    void validateCardNumber_ValidVisa() {
        // When
        boolean result = paymentService.validateCardNumber("4532015112830366");

        // Then
        assertTrue(result);
    }

    @Test
    void validateCardNumber_ValidMastercard() {
        // When
        boolean result = paymentService.validateCardNumber("5555555555554444");

        // Then
        assertTrue(result);
    }

    @Test
    void validateCardNumber_ValidAmex() {
        // When
        boolean result = paymentService.validateCardNumber("378282246310005");

        // Then
        assertTrue(result);
    }

    @Test
    void validateCardNumber_WithSpaces() {
        // When
        boolean result = paymentService.validateCardNumber("4532 0151 1283 0366");

        // Then
        assertTrue(result);
    }

    @Test
    void validateCardNumber_WithDashes() {
        // When
        boolean result = paymentService.validateCardNumber("4532-0151-1283-0366");

        // Then
        assertTrue(result);
    }

    @Test
    void validateCardNumber_NullInput() {
        // When
        boolean result = paymentService.validateCardNumber(null);

        // Then
        assertFalse(result);
    }

    @Test
    void validateCardNumber_EmptyString() {
        // When
        boolean result = paymentService.validateCardNumber("");

        // Then
        assertFalse(result);
    }

    @Test
    void validateCardNumber_TooShort() {
        // When
        boolean result = paymentService.validateCardNumber("123456789012");

        // Then
        assertFalse(result);
    }

    @Test
    void validateCardNumber_TooLong() {
        // When
        boolean result = paymentService.validateCardNumber("12345678901234567890");

        // Then
        assertFalse(result);
    }

    @Test
    void validateCardNumber_NonNumeric() {
        // When
        boolean result = paymentService.validateCardNumber("453201511283036A");

        // Then
        assertFalse(result);
    }

    @Test
    void validateCardNumber_InvalidLuhn() {
        // When
        boolean result = paymentService.validateCardNumber("4532015112830367"); // Last digit changed

        // Then
        assertFalse(result);
    }

    // ===== VALIDATE EXPIRY DATE TESTS =====

    @Test
    void validateExpiryDate_ValidFutureDate() {
        // When
        boolean result = paymentService.validateExpiryDate("12", "2025");

        // Then
        assertTrue(result);
    }

    @Test
    void validateExpiryDate_CurrentMonth() {
        // Given
        YearMonth current = YearMonth.now();

        // When
        boolean result = paymentService.validateExpiryDate(
                String.valueOf(current.getMonthValue()),
                String.valueOf(current.getYear())
        );

        // Then
        assertTrue(result);
    }

    @Test
    void validateExpiryDate_TwoDigitYear() {
        // When
        boolean result = paymentService.validateExpiryDate("12", "25");

        // Then
        assertTrue(result);
    }

    @Test
    void validateExpiryDate_PastDate() {
        // When
        boolean result = paymentService.validateExpiryDate("01", "2020");

        // Then
        assertFalse(result);
    }

    @Test
    void validateExpiryDate_InvalidMonth() {
        // When
        boolean result = paymentService.validateExpiryDate("13", "2025");

        // Then
        assertFalse(result);
    }

    @Test
    void validateExpiryDate_ZeroMonth() {
        // When
        boolean result = paymentService.validateExpiryDate("0", "2025");

        // Then
        assertFalse(result);
    }

    @Test
    void validateExpiryDate_NullMonth() {
        // When
        boolean result = paymentService.validateExpiryDate(null, "2025");

        // Then
        assertFalse(result);
    }

    @Test
    void validateExpiryDate_NullYear() {
        // When
        boolean result = paymentService.validateExpiryDate("12", null);

        // Then
        assertFalse(result);
    }

    @Test
    void validateExpiryDate_NonNumericInput() {
        // When
        boolean result = paymentService.validateExpiryDate("AB", "XY");

        // Then
        assertFalse(result);
    }

    // ===== VALIDATE CVV TESTS =====

    @Test
    void validateCVV_ThreeDigits() {
        // When
        boolean result = paymentService.validateCVV("123");

        // Then
        assertTrue(result);
    }

    @Test
    void validateCVV_FourDigits() {
        // When
        boolean result = paymentService.validateCVV("1234");

        // Then
        assertTrue(result);
    }

    @Test
    void validateCVV_Null() {
        // When
        boolean result = paymentService.validateCVV(null);

        // Then
        assertFalse(result);
    }

    @Test
    void validateCVV_TooShort() {
        // When
        boolean result = paymentService.validateCVV("12");

        // Then
        assertFalse(result);
    }

    @Test
    void validateCVV_TooLong() {
        // When
        boolean result = paymentService.validateCVV("12345");

        // Then
        assertFalse(result);
    }

    @Test
    void validateCVV_NonNumeric() {
        // When
        boolean result = paymentService.validateCVV("12A");

        // Then
        assertFalse(result);
    }

    // ===== PREPARE FOR STORAGE TESTS =====

    @Test
    void preparForStorage_Success() {
        // When
        PaymentInfo sanitized = paymentService.preparForStorage(testPaymentInfo);

        // Then
        assertNotNull(sanitized);
        assertEquals("****-****-****-0366", sanitized.getCardNumber());
        assertEquals(testPaymentInfo.getAmount(), sanitized.getAmount());
        assertEquals(testPaymentInfo.getExpiryMonth(), sanitized.getExpiryMonth());
        assertEquals(testPaymentInfo.getExpiryYear(), sanitized.getExpiryYear());
        assertNull(sanitized.getCvv()); // CVV should be removed
    }

    @Test
    void preparForStorage_ShortCardNumber() {
        // Given
        testPaymentInfo.setCardNumber("1234");

        // When
        PaymentInfo sanitized = paymentService.preparForStorage(testPaymentInfo);

        // Then
        assertNull(sanitized.getCardNumber()); // Too short to mask
    }

    @Test
    void preparForStorage_NullCardNumber() {
        // Given
        testPaymentInfo.setCardNumber(null);

        // When
        PaymentInfo sanitized = paymentService.preparForStorage(testPaymentInfo);

        // Then
        assertNull(sanitized.getCardNumber());
    }

    // ===== INTEGRATION TESTS =====

    @Test
    void fullPaymentFlow_Success() {
        // Given
        when(paymentGateway.processPayment(any(PaymentInfo.class))).thenReturn(successResult);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(paymentRepository.save(any(Payment.class))).thenReturn(new Payment());

        // When
        PaymentResult result = paymentService.processPaymentForOrder(testOrder, testPaymentInfo);

        // Then
        assertTrue(result.isSuccess());
        assertEquals(PaymentStatus.COMPLETED, testOrder.getPaymentStatus());

        // Verify all interactions
        verify(paymentGateway, atLeastOnce()).processPayment(testPaymentInfo);
        verify(orderRepository, times(1)).save(testOrder);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void fullRefundFlow_Success() {
        // Given
        testOrder.setPaymentStatus(PaymentStatus.COMPLETED);
        testOrder.setTransactionId("TXN-12345");
        when(paymentGateway.refund(anyString(), any(BigDecimal.class))).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(paymentRepository.save(any(Payment.class))).thenReturn(new Payment());

        // When
        boolean result = paymentService.refundPayment(testOrder);

        // Then
        assertTrue(result);
        assertEquals(PaymentStatus.REFUNDED, testOrder.getPaymentStatus());

        // Verify all interactions
        verify(paymentGateway, times(1)).refund("TXN-12345", BigDecimal.valueOf(99.99));
        verify(orderRepository, times(1)).save(testOrder);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}