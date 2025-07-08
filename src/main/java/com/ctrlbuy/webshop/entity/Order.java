package com.ctrlbuy.webshop.entity;

import com.ctrlbuy.webshop.security.entity.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    // Delivery information
    @Column(nullable = false)
    private String deliveryName;

    @Column(nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private String deliveryCity;

    @Column(nullable = false)
    private String deliveryPostalCode;

    private String deliveryPhone;

    // Payment information
    private String paymentMethod;
    private String paymentTransactionId;
    private String transactionId;

    // User relationship - FIXED: Proper ManyToOne relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Order items relationship - FIXED: Proper entity reference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<com.ctrlbuy.webshop.entity.OrderItem> orderItems = new ArrayList<>();

    // Audit fields
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime refundedAt;

    // Enums
    public enum OrderStatus {
        PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }

    public enum PaymentStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED, PARTIALLY_REFUNDED
    }

    // Constructors
    public Order() {
        this.orderDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public Order(String orderNumber, BigDecimal totalAmount) {
        this();
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
    }

    // LEGACY CONSTRUCTOR for backward compatibility
    public Order(String orderNumber, Double totalAmount) {
        this();
        this.orderNumber = orderNumber;
        this.totalAmount = BigDecimal.valueOf(totalAmount);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    // LEGACY METHOD for backward compatibility
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount != null ? BigDecimal.valueOf(totalAmount) : null;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryCity() {
        return deliveryCity;
    }

    public void setDeliveryCity(String deliveryCity) {
        this.deliveryCity = deliveryCity;
    }

    public String getDeliveryPostalCode() {
        return deliveryPostalCode;
    }

    public void setDeliveryPostalCode(String deliveryPostalCode) {
        this.deliveryPostalCode = deliveryPostalCode;
    }

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public void setPaymentTransactionId(String paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    // FIXED: User relationship methods
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // LEGACY: userId methods for backward compatibility
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public void setUserId(Long userId) {
        // This is handled by setUser() method now
        // Legacy method kept for backward compatibility
    }

    public List<com.ctrlbuy.webshop.entity.OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<com.ctrlbuy.webshop.entity.OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(LocalDateTime refundedAt) {
        this.refundedAt = refundedAt;
    }

    // Helper methods
    public void addOrderItem(com.ctrlbuy.webshop.entity.OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void removeOrderItem(com.ctrlbuy.webshop.entity.OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", orderDate=" + orderDate +
                '}';
    }
}