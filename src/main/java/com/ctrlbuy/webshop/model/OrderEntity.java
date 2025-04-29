package com.ctrlbuy.webshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitetsklass för beställningar med valideringsprocesser och relationshantering
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    /**
     * Unik identifierare för beställningen
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Referens till kund som gjort beställningen
     */
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    /**
     * Tidpunkt då beställningen gjordes
     */
    @Column(nullable = false)
    private LocalDateTime orderDate;

    /**
     * Total summa för beställningen med validering av decimaltal
     */
    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Aktuell status på beställningen
     */
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /**
     * Lista över artiklar i beställningen
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    /**
     * Validerar och formaterar totalAmount-värdet
     * @param amount det belopp som ska valideras
     */
    public void setTotalAmount(BigDecimal amount) {
        validateAmount(amount);
        this.totalAmount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Validerar att beloppet uppfyller alla krav
     * @param amount belopp som ska valideras
     * @throws IllegalArgumentException om valideringen misslyckas
     */
    private void validateAmount(BigDecimal amount) {
        // Kontrollera att värdet inte är null
        if (amount == null) {
            throw new IllegalArgumentException("totalAmount kan inte vara null");
        }

        // Kontrollera att antal decimaler inte överstiger 2
        if (amount.scale() > 2) {
            throw new IllegalArgumentException("Mer än 2 decimaler inte tillåtet");
        }

        // Kontrollera att beloppet inte är negativt
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Negativt belopp inte tillåtet");
        }
    }

    /**
     * Enum för alla möjliga beställningsstatusar
     */
    public enum OrderStatus {
        PENDING,   // Väntande på behandling
        PROCESSING, // Under bearbetning
        SHIPPED,   // Skickad
        DELIVERED, // Levererad
        CANCELLED  // Avbruten
    }
}


//package com.ctrlbuy.webshop.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//
//@Entity
//@Table(name = "orders")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class OrderEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "customer_id", nullable = false)
//    private CustomerEntity customer;
//
//    @Column(nullable = false)
//    private LocalDateTime orderDate;
//
//    @Column(nullable = false)
//    private BigDecimal totalAmount;
//
//    @Enumerated(EnumType.STRING)
//    private OrderStatus status;
//
//    // Här är korrekt konfiguration för OneToMany-relationen
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<OrderItemEntity> orderItems = new ArrayList<>();
//
//    // Enum för understatement
//    public enum OrderStatus {
//        PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
//    }
//}