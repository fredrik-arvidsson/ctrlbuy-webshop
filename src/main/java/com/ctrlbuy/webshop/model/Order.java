package com.ctrlbuy.webshop.model;

import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    // Delivery address
    @Column(nullable = false)
    private String deliveryName;

    @Column(nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private String deliveryCity;

    @Column(nullable = false)
    private String deliveryPostalCode;

    @Column
    private String deliveryPhone;

    // Payment information
    @Column
    private String paymentMethod;

    @Column
    private String paymentTransactionId;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentStatus paymentStatus;

    @Column
    private String transactionId;

    @Column
    private LocalDateTime refundedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // Constructors
    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public Order(User user, String orderNumber, Double totalAmount) {
        this();
        this.user = user;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
    }

    // Payment-related methods that PaymentService needs
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(LocalDateTime refundedAt) {
        this.refundedAt = refundedAt;
    }

    // Helper methods
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }

    // Payment status convenience methods
    public boolean isPaymentCompleted() {
        return PaymentStatus.COMPLETED.equals(paymentStatus);
    }

    public boolean isPaymentPending() {
        return PaymentStatus.PENDING.equals(paymentStatus);
    }

    public boolean isPaymentFailed() {
        return PaymentStatus.FAILED.equals(paymentStatus);
    }

    public boolean canBeRefunded() {
        return isPaymentCompleted() && refundedAt == null;
    }

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }
}