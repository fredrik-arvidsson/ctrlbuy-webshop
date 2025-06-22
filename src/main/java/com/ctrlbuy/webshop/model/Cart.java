// FILE: src/main/java/com/ctrlbuy/webshop/model/Cart.java
package com.ctrlbuy.webshop.model;

import com.ctrlbuy.webshop.security.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // För inloggade användare
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // För session-baserade carts (innan inloggning)
    @Column(name = "session_id")
    private String sessionId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Beräkna total summa för hela kundvagnen
    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Få totalt antal varor i kundvagnen
    public Integer getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    // Lägg till item i kundvagnen
    public void addItem(CartItem item) {
        item.setCart(this);
        this.items.add(item);
    }

    // Ta bort item från kundvagnen
    public void removeItem(CartItem item) {
        this.items.remove(item);
        item.setCart(null);
    }

    // Hitta item baserat på produkt
    public CartItem findItemByProduct(Product product) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);
    }

    // Kontrollera om kundvagnen är tom
    public boolean isEmpty() {
        return items.isEmpty();
    }

    // Rensa hela kundvagnen
    public void clear() {
        items.clear();
    }
}