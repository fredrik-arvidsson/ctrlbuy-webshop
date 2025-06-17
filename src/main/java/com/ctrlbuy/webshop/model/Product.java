package com.ctrlbuy.webshop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")  // Explicit tabellnamn
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category")
    private String category;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity")  // Använd denna istället för stock
    private Integer stockQuantity;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "manufacturer_id")
    private Integer manufacturerId;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    // ⭐ LÄGG TILL DESSA FÄLT som Thymeleaf templates förväntar sig
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "sale_price", precision = 10, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "is_featured")
    private Boolean featured = false;

    @Column(name = "is_active")
    private Boolean active = true;

    // Default constructor
    public Product() {
        this.viewCount = 0;
        this.featured = false;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor
    public Product(String name, String category, BigDecimal price, Integer stockQuantity, String description) {
        this();  // Anropa default constructor för att sätta standardvärden
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
    }

    // ⭐ JPA Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Integer manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getViewCount() {
        return viewCount != null ? viewCount : 0;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    // ⭐ NYA GETTERS OCH SETTERS
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

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public Boolean getFeatured() {
        return featured != null ? featured : false;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Boolean getActive() {
        return active != null ? active : true;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // ⭐ BOOLEAN CONVENIENCE METHODS (för Thymeleaf kompatibilitet)
    public boolean isActive() {
        return getActive();
    }

    public boolean isFeatured() {
        return getFeatured();
    }

    public boolean isNew() {
        return createdAt != null && createdAt.isAfter(LocalDateTime.now().minusDays(30));
    }

    public boolean hasImage() {
        return imageUrl != null && !imageUrl.trim().isEmpty();
    }

    // ⭐ UTILITY METHODS
    public void incrementViewCount() {
        this.viewCount = getViewCount() + 1;
    }

    public boolean isOnSale() {
        return salePrice != null && salePrice.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    public boolean isLowStock() {
        return stockQuantity != null && stockQuantity > 0 && stockQuantity < 5;
    }

    // ⭐ Convenience method för rea-procent
    public BigDecimal getDiscountPercentage() {
        if (isOnSale() && price != null && price.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal difference = price.subtract(salePrice);
            return difference.divide(price, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getSavings() {
        if (isOnSale() && price != null) {
            return price.subtract(salePrice);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", viewCount=" + viewCount +
                ", createdAt=" + createdAt +
                ", active=" + active +
                ", featured=" + featured +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}