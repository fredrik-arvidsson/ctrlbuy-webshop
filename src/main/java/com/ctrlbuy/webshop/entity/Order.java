package com.ctrlbuy.webshop.entity;

import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.model.Payment;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Entity - Railway-optimerad med Lombok och förbättrade relationships
 * ✅ UPPDATERAD: Bättre performance, relationships och Railway-kompatibilitet
 * ✅ FIXAD: Index-namn matchar faktiska kolumnnamn (snake_case)
 */
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_number", columnList = "order_number"),    // ✅ FIXAD: Använder snake_case
        @Index(name = "idx_order_status", columnList = "status"),
        @Index(name = "idx_order_user", columnList = "user_id"),
        @Index(name = "idx_order_date", columnList = "order_date")          // ✅ FIXAD: Använder snake_case
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "orderItems", "payments"}) // Undvik lazy loading i toString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    @EqualsAndHashCode.Include
    private String orderNumber;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false, precision = 10)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 30)
    private PaymentStatus paymentStatus;

    // ============================
    // DELIVERY INFORMATION
    // ============================
    @Column(name = "delivery_name", nullable = false, length = 100)
    private String deliveryName;

    @Column(name = "delivery_address", nullable = false, length = 200)
    private String deliveryAddress;

    @Column(name = "delivery_city", nullable = false, length = 100)
    private String deliveryCity;

    @Column(name = "delivery_postal_code", nullable = false, length = 20)
    private String deliveryPostalCode;

    @Column(name = "delivery_phone", length = 20)
    private String deliveryPhone;

    @Column(name = "delivery_email", length = 100)
    private String deliveryEmail;

    @Column(name = "delivery_notes", length = 500)
    private String deliveryNotes;

    // ============================
    // PAYMENT INFORMATION
    // ============================
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_transaction_id", length = 100)
    private String paymentTransactionId;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(name = "discount_amount", precision = 10)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 10)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "shipping_cost", precision = 10)
    @Builder.Default
    private BigDecimal shippingCost = BigDecimal.ZERO;

    // ============================
    // RELATIONSHIPS
    // ============================

    /**
     * User relationship - FIXED: Proper ManyToOne relationship
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_user"))
    private User user;

    /**
     * Order items relationship - FIXED: Proper entity reference
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * Payments relationship - NY: För PaymentService integration
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    // ============================
    // AUDIT FIELDS
    // ============================
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    // ============================
    // BUSINESS FIELDS
    // ============================
    @Column(name = "priority", length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderPriority priority = OrderPriority.NORMAL;

    @Column(name = "source", length = 50)
    @Builder.Default
    private String source = "WEB";

    @Column(name = "currency", length = 3)
    @Builder.Default
    private String currency = "SEK";

    @Column(name = "notes", length = 1000)
    private String notes;

    // ============================
    // ENUMS
    // ============================
    public enum OrderStatus {
        PENDING("Väntande", "Ordern har skapats och väntar på betalning"),
        CONFIRMED("Bekräftad", "Ordern är bekräftad och betalning mottagen"),
        PROCESSING("Behandlas", "Ordern behandlas och plockas"),
        SHIPPED("Skickad", "Ordern har skickats till kunden"),
        DELIVERED("Levererad", "Ordern har levererats till kunden"),
        CANCELLED("Avbruten", "Ordern har avbrutits"),
        RETURNED("Returnerad", "Ordern har returnerats av kunden");

        private final String swedishName;
        private final String description;

        OrderStatus(String swedishName, String description) {
            this.swedishName = swedishName;
            this.description = description;
        }

        public String getSwedishName() { return swedishName; }
        public String getDescription() { return description; }

        public boolean isActive() {
            return this != CANCELLED && this != RETURNED;
        }

        public boolean isCompleted() {
            return this == DELIVERED || this == CANCELLED || this == RETURNED;
        }
    }

    public enum PaymentStatus {
        PENDING("Väntande", "Betalning väntar"),
        PROCESSING("Behandlas", "Betalning behandlas"),
        COMPLETED("Genomförd", "Betalning genomförd"),
        FAILED("Misslyckad", "Betalning misslyckades"),
        CANCELLED("Avbruten", "Betalning avbruten"),
        REFUNDED("Återbetald", "Betalning återbetald"),
        PARTIALLY_REFUNDED("Delvis återbetald", "Betalning delvis återbetald");

        private final String swedishName;
        private final String description;

        PaymentStatus(String swedishName, String description) {
            this.swedishName = swedishName;
            this.description = description;
        }

        public String getSwedishName() { return swedishName; }
        public String getDescription() { return description; }

        public boolean isPaid() {
            return this == COMPLETED;
        }

        public boolean canRefund() {
            return this == COMPLETED || this == PARTIALLY_REFUNDED;
        }
    }

    public enum OrderPriority {
        LOW("Låg"), NORMAL("Normal"), HIGH("Hög"), URGENT("Brådskande");

        private final String swedishName;

        OrderPriority(String swedishName) {
            this.swedishName = swedishName;
        }

        public String getSwedishName() { return swedishName; }
    }

    // ============================
    // 🎯 STATUS SETTER METHODS - CRITICAL FIX
    // ============================

    /**
     * 🔧 CRITICAL FIX: Set status method that accepts String (for AdminController compatibility)
     * This method is needed to fix compilation errors in updateOrderStatus()
     */
    public void setStatus(String status) {
        if (status != null) {
            try {
                this.status = OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Fallback to PENDING if invalid status
                this.status = OrderStatus.PENDING;
            }
        }
    }

    /**
     * 🔧 Set status method that accepts OrderStatus enum (Lombok generated one is overridden)
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * 🔧 Get status as String (for backward compatibility)
     */
    public String getStatusAsString() {
        return status != null ? status.name() : "PENDING";
    }

    // ============================
    // LIFECYCLE CALLBACKS
    // ============================
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (orderDate == null) {
            orderDate = now;
        }
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Auto-set completion dates based on status
        if (status == OrderStatus.DELIVERED && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
        if (status == OrderStatus.CANCELLED && cancelledAt == null) {
            cancelledAt = LocalDateTime.now();
        }
    }

    // ============================
    // BUSINESS METHODS
    // ============================

    /**
     * Lägg till order item med automatisk relationship
     */
    public void addOrderItem(OrderItem orderItem) {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    /**
     * Ta bort order item
     */
    public void removeOrderItem(OrderItem orderItem) {
        if (orderItems != null) {
            orderItems.remove(orderItem);
            orderItem.setOrder(null);
        }
    }

    /**
     * Lägg till payment med automatisk relationship
     */
    public void addPayment(Payment payment) {
        if (payments == null) {
            payments = new ArrayList<>();
        }
        payments.add(payment);
        payment.setOrder(this);
    }

    /**
     * Beräkna totalt belopp baserat på items
     */
    public BigDecimal calculateTotal() {
        BigDecimal itemsTotal = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return itemsTotal
                .add(shippingCost != null ? shippingCost : BigDecimal.ZERO)
                .add(taxAmount != null ? taxAmount : BigDecimal.ZERO)
                .subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
    }

    /**
     * Kontrollera om ordern kan avbrytas
     */
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    /**
     * Kontrollera om ordern kan returneras
     */
    public boolean canBeReturned() {
        return status == OrderStatus.DELIVERED &&
                completedAt != null &&
                completedAt.isAfter(LocalDateTime.now().minusDays(30)); // 30 dagars returrätt
    }

    /**
     * Hämta total betald summa
     */
    public BigDecimal getTotalPaidAmount() {
        return payments.stream()
                .filter(payment -> payment.getStatus() == com.ctrlbuy.webshop.enums.PaymentStatus.COMPLETED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Kontrollera om ordern är fullständigt betald
     */
    public boolean isFullyPaid() {
        return getTotalPaidAmount().compareTo(totalAmount) >= 0;
    }

    // ============================
    // LEGACY COMPATIBILITY METHODS - CRITICAL FOR SERVICE COMPATIBILITY
    // ============================

    /**
     * 🔧 CRITICAL: getTotalAmount as Double (för OrderService compatibility)
     */
    public Double getTotalAmount() {
        return totalAmount != null ? totalAmount.doubleValue() : 0.0;
    }

    /**
     * 🔧 CRITICAL: getTotalAmount as BigDecimal (för andra services)
     */
    public BigDecimal getTotalAmountAsBigDecimal() {
        return totalAmount;
    }

    /**
     * LEGACY CONSTRUCTOR for backward compatibility
     */
    public Order(String orderNumber, Double totalAmount) {
        this();
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount != null ? BigDecimal.valueOf(totalAmount) : BigDecimal.ZERO;
    }

    /**
     * LEGACY METHOD for backward compatibility
     */
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount != null ? BigDecimal.valueOf(totalAmount) : BigDecimal.ZERO;
    }

    /**
     * 🔧 CRITICAL: setTotalAmount as BigDecimal (för Lombok compatibility)
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * LEGACY: userId methods for backward compatibility
     */
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public void setUserId(Long userId) {
        // Legacy method kept for backward compatibility
        // Actual user setting should be done via setUser() method
    }

    /**
     * LEGACY: orderItems med full package name
     */
    public List<com.ctrlbuy.webshop.entity.OrderItem> getOrderItemsLegacy() {
        return new ArrayList<>(orderItems);
    }

    public void setOrderItemsLegacy(List<com.ctrlbuy.webshop.entity.OrderItem> orderItems) {
        this.orderItems = new ArrayList<>(orderItems);
    }
}