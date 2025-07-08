package com.ctrlbuy.webshop.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // FIXED: Proper Product relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Constructors
    public OrderItem() {}

    public OrderItem(Product product, String productName, Integer quantity, BigDecimal price) {
        this.product = product;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    // LEGACY constructor for backward compatibility
    public OrderItem(Long productId, String productName, Integer quantity, Double price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = BigDecimal.valueOf(price);
        // productId will be handled by setProduct() if needed
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    // FIXED: Product relationship methods
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // LEGACY: productId methods for backward compatibility
    public Long getProductId() {
        return product != null ? product.getId() : null;
    }

    public void setProductId(Long productId) {
        // This is handled by setProduct() method now
        // Legacy method kept for backward compatibility
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // LEGACY method for backward compatibility
    public void setPrice(Double price) {
        this.price = price != null ? BigDecimal.valueOf(price) : null;
    }

    // Helper methods
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    // ✅ FIXAT: Lägg till getTotalPrice() metoden som templates behöver
    public BigDecimal getTotalPrice() {
        return getSubtotal(); // Samma som subtotal för OrderItem
    }

    // LEGACY method for backward compatibility
    public Double getSubtotalAsDouble() {
        return getSubtotal().doubleValue();
    }

    // ✅ EXTRA: Lägg till getTotalPriceAsDouble() för säkerhets skull
    public Double getTotalPriceAsDouble() {
        return getTotalPrice().doubleValue();
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }

    @Entity
    @Table(name = "cart_items")
    public static class CartItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "cart_id", nullable = false)
        private Cart cart;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "product_id", nullable = false)
        private Product product;

        @Column(name = "quantity", nullable = false)
        private Integer quantity;

        @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
        private BigDecimal price;

        @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
        private BigDecimal subtotal;

        @Column(name = "created_at")
        private LocalDateTime createdAt;

        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
            updatedAt = LocalDateTime.now();
            calculateSubtotal();
        }

        @PreUpdate
        protected void onUpdate() {
            updatedAt = LocalDateTime.now();
            calculateSubtotal();
        }

        // Constructors
        public CartItem() {}

        public CartItem(Cart cart, Product product, Integer quantity) {
            this.cart = cart;
            this.product = product;
            this.quantity = quantity;
            this.price = product.getPrice();
            calculateSubtotal();
        }

        public CartItem(Product product, Integer quantity) {
            this.product = product;
            this.quantity = quantity;
            this.price = product.getPrice();
            calculateSubtotal();
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Cart getCart() {
            return cart;
        }

        public void setCart(Cart cart) {
            this.cart = cart;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
            if (product != null) {
                this.price = product.getPrice();
                calculateSubtotal();
            }
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
            calculateSubtotal();
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
            calculateSubtotal();
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }

        // ✅ FIXAT: Lägg till getTotalPrice() även för CartItem
        public BigDecimal getTotalPrice() {
            return getSubtotal();
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

        // Business methods
        public void calculateSubtotal() {
            if (price != null && quantity != null) {
                this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
            }
        }

        public void increaseQuantity() {
            this.quantity++;
            calculateSubtotal();
        }

        public void decreaseQuantity() {
            if (this.quantity > 1) {
                this.quantity--;
                calculateSubtotal();
            }
        }

        public String getProductName() {
            return product != null ? product.getName() : "Unknown Product";
        }

        public String getProductImageUrl() {
            return product != null ? product.getImageUrl() : null;
        }

        @Override
        public String toString() {
            return "CartItem{" +
                    "id=" + id +
                    ", product=" + (product != null ? product.getName() : "null") +
                    ", quantity=" + quantity +
                    ", price=" + price +
                    ", subtotal=" + subtotal +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CartItem cartItem = (CartItem) o;

            if (id != null ? !id.equals(cartItem.id) : cartItem.id != null) return false;
            return product != null ? product.equals(cartItem.product) : cartItem.product == null;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (product != null ? product.hashCode() : 0);
            return result;
        }
    }
}