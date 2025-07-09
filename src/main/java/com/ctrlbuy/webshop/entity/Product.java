package com.ctrlbuy.webshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Product Entity - Railway-optimerad med fullständig ProductService kompatibilitet
 * ✅ UPPDATERAD: Alla stock methods, Lombok integration, och business logic
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_category", columnList = "category"),
        @Index(name = "idx_product_active", columnList = "isActive"),
        @Index(name = "idx_product_sale", columnList = "isOnSale"),
        @Index(name = "idx_product_featured", columnList = "isFeatured"),
        @Index(name = "idx_product_price", columnList = "price"),
        @Index(name = "idx_product_created", columnList = "createdAt")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"productImages"}) // Undvik lazy loading i toString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 10)
    private BigDecimal price;

    @Column(nullable = false, length = 100)
    private String category;

    // ============================
    // STOCK FIELDS - Multiple compatibility options
    // ============================

    /**
     * Primary stock field - används av ProductService
     */
    @Column(nullable = false, name = "stockQuantity")
    @Builder.Default
    private Integer stockQuantity = 0;

    // ============================
    // PRODUCT DETAILS
    // ============================

    @Column(name = "imageUrl", length = 500)
    private String imageUrl;

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String model;

    @Column(length = 50)
    private String color;

    @Column(length = 100)
    private String sku;

    @Column(length = 100)
    private String barcode;

    @Column(length = 200)
    private String dimensions;

    @Column(precision = 8)
    private Float weight;

    @Column(name = "originCountry", length = 100)
    private String originCountry;

    // ============================
    // PRICING FIELDS
    // ============================

    @Column(precision = 10, name = "costPrice")
    private BigDecimal costPrice;

    @Column(precision = 10, name = "originalPrice")
    private BigDecimal originalPrice;

    @Column(precision = 10, name = "salePrice")
    private BigDecimal salePrice;

    @Column(precision = 5, name = "discountPercentage")
    private BigDecimal discountPercentage;

    @Column(name = "saleStartDate")
    private LocalDateTime saleStartDate;

    @Column(name = "saleEndDate")
    private LocalDateTime saleEndDate;

    // ============================
    // RATINGS & REVIEWS
    // ============================

    @Column(precision = 3)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "reviewCount")
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(name = "viewCount")
    @Builder.Default
    private Integer viewCount = 0;

    // ============================
    // INVENTORY MANAGEMENT
    // ============================

    @Column(name = "minimumStockLevel")
    private Integer minimumStockLevel;

    @Column(name = "maximumStockLevel")
    private Integer maximumStockLevel;

    @Column(name = "reorderPoint")
    private Integer reorderPoint;

    @Column(name = "supplierId")
    private Long supplierId;

    // ============================
    // PRODUCT STATUS FLAGS
    // ============================

    @Column(name = "isActive")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "isFeatured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "isOnSale")
    @Builder.Default
    private Boolean isOnSale = false;

    // ============================
    // SEO AND METADATA
    // ============================

    @Column(length = 200, name = "metaTitle")
    private String metaTitle;

    @Column(length = 500, name = "metaDescription")
    private String metaDescription;

    @Column(length = 500)
    private String tags;

    @Column(length = 1000, name = "saleDescription")
    private String saleDescription;

    // ============================
    // PRODUCT SPECIFICATIONS
    // ============================

    @Column(name = "warrantyMonths")
    private Integer warrantyMonths;

    @Column(name = "estimatedDeliveryDays")
    private Integer estimatedDeliveryDays;

    // ============================
    // AUDIT FIELDS
    // ============================

    @Column(updatable = false, name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    // ============================
    // RELATIONSHIPS
    // ============================

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductImage> productImages = new ArrayList<>();

    // ============================
    // LIFECYCLE CALLBACKS
    // ============================

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }

        // Set defaults
        if (isActive == null) isActive = true;
        if (isFeatured == null) isFeatured = false;
        if (isOnSale == null) isOnSale = false;
        if (viewCount == null) viewCount = 0;
        if (reviewCount == null) reviewCount = 0;
        if (stockQuantity == null) stockQuantity = 0;
        if (rating == null) rating = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ============================
    // PRODUCTSERVICE COMPATIBILITY METHODS
    // ============================

    /**
     * Primary stock getter - ProductService kompatibilitet
     */
    public Integer getStock() {
        return this.stockQuantity;
    }

    /**
     * Primary stock setter - ProductService kompatibilitet
     */
    public void setStock(Integer stock) {
        this.stockQuantity = stock;
    }

    /**
     * Alternative stock getter - fallback compatibility
     */
    public Integer getInventory() {
        return this.stockQuantity;
    }

    /**
     * Alternative stock setter - fallback compatibility
     */
    public void setInventory(Integer inventory) {
        this.stockQuantity = inventory;
    }

    /**
     * Alternative stock getter - fallback compatibility
     */
    public Integer getQuantity() {
        return this.stockQuantity;
    }

    /**
     * Alternative stock setter - fallback compatibility
     */
    public void setQuantity(Integer quantity) {
        this.stockQuantity = quantity;
    }

    // ============================
    // BOOLEAN ALIAS METHODS (för compatibility)
    // ============================

    /**
     * Standard boolean naming convention alias
     */
    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        this.isActive = active;
    }

    public Boolean getActive() {
        return isActive;
    }

    /**
     * Featured product aliases
     */
    public Boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(Boolean featured) {
        this.isFeatured = featured;
    }

    public Boolean getFeatured() {
        return isFeatured;
    }

    /**
     * On sale aliases
     */
    public Boolean isOnSale() {
        return isOnSale;
    }

    public void setOnSale(Boolean onSale) {
        this.isOnSale = onSale;
    }

    public Boolean getOnSale() {
        return isOnSale;
    }

    // ============================
    // BUSINESS LOGIC METHODS
    // ============================

    /**
     * Kontrollera om produkt är i lager
     */
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    /**
     * Kontrollera om lagersaldo är lågt
     */
    public boolean isLowStock() {
        return minimumStockLevel != null && stockQuantity != null &&
                stockQuantity <= minimumStockLevel;
    }

    /**
     * Kontrollera om produkt behöver beställas
     */
    public boolean needsReorder() {
        return reorderPoint != null && stockQuantity != null &&
                stockQuantity <= reorderPoint;
    }

    /**
     * Hämta effektivt pris (sale price om på rea, annars ordinarie)
     */
    public BigDecimal getEffectivePrice() {
        if (Boolean.TRUE.equals(isOnSale) && salePrice != null) {
            return salePrice;
        }
        return price;
    }

    /**
     * Hämta aktuellt pris (alias för getEffectivePrice)
     */
    public BigDecimal getCurrentPrice() {
        return getEffectivePrice();
    }

    /**
     * Hämta ursprungligt pris för display
     */
    public BigDecimal getOriginalDisplayPrice() {
        return originalPrice != null ? originalPrice : price;
    }

    /**
     * Beräkna besparingar vid rea
     */
    public BigDecimal getSavings() {
        if (Boolean.TRUE.equals(isOnSale) && salePrice != null) {
            BigDecimal originalDisplayPrice = getOriginalDisplayPrice();
            if (originalDisplayPrice.compareTo(salePrice) > 0) {
                return originalDisplayPrice.subtract(salePrice);
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Beräkna rabattprocent
     */
    public BigDecimal getCalculatedDiscountPercentage() {
        if (discountPercentage != null) {
            return discountPercentage;
        }

        BigDecimal savings = getSavings();
        BigDecimal originalDisplayPrice = getOriginalDisplayPrice();

        if (savings.compareTo(BigDecimal.ZERO) > 0 &&
                originalDisplayPrice.compareTo(BigDecimal.ZERO) > 0) {
            return savings.divide(originalDisplayPrice, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        return BigDecimal.ZERO;
    }

    /**
     * Kontrollera om rea är aktiv just nu
     */
    public boolean isSaleActive() {
        if (!Boolean.TRUE.equals(isOnSale)) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        // Kontrollera start datum
        if (saleStartDate != null && now.isBefore(saleStartDate)) {
            return false;
        }

        // Kontrollera slut datum
        if (saleEndDate != null && now.isAfter(saleEndDate)) {
            return false;
        }

        return true;
    }

    /**
     * Öka visningar
     */
    public void incrementViewCount() {
        this.viewCount = (viewCount == null ? 0 : viewCount) + 1;
    }

    /**
     * Lägg till betyg
     */
    public void addRating(BigDecimal newRating) {
        if (newRating == null) return;

        int currentReviews = reviewCount != null ? reviewCount : 0;
        BigDecimal currentRating = rating != null ? rating : BigDecimal.ZERO;

        // Beräkna nytt genomsnittsbetyg
        BigDecimal totalRating = currentRating.multiply(BigDecimal.valueOf(currentReviews));
        totalRating = totalRating.add(newRating);

        this.reviewCount = currentReviews + 1;
        this.rating = totalRating.divide(BigDecimal.valueOf(this.reviewCount), 2,
                java.math.RoundingMode.HALF_UP);
    }

    /**
     * Minska lager
     */
    public boolean decreaseStock(int quantity) {
        if (stockQuantity == null || stockQuantity < quantity) {
            return false;
        }

        stockQuantity -= quantity;
        return true;
    }

    /**
     * Öka lager
     */
    public void increaseStock(int quantity) {
        if (stockQuantity == null) {
            stockQuantity = 0;
        }
        stockQuantity += quantity;
    }

    /**
     * Kontrollera om produkten är tillgänglig för köp
     */
    public boolean isAvailableForPurchase() {
        return Boolean.TRUE.equals(isActive) && isInStock();
    }

    /**
     * Kontrollera om produkten är synlig för kunder
     */
    public boolean isVisible() {
        return Boolean.TRUE.equals(isActive);
    }

    // ============================
    // DISPLAY METHODS
    // ============================

    /**
     * Hämta formatted pris för display
     */
    public String getFormattedPrice() {
        BigDecimal effectivePrice = getEffectivePrice();
        return String.format("%.2f SEK", effectivePrice);
    }

    /**
     * Hämta kort beskrivning (första 100 tecken)
     */
    public String getShortDescription() {
        if (description == null || description.trim().isEmpty()) {
            return "";
        }

        String clean = description.trim();
        if (clean.length() <= 100) {
            return clean;
        }

        return clean.substring(0, 97) + "...";
    }

    /**
     * Hämta första produktbild URL
     */
    public String getPrimaryImageUrl() {
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            return imageUrl;
        }

        if (productImages != null && !productImages.isEmpty()) {
            ProductImage firstImage = productImages.get(0);
            return firstImage.getImageUrl();
        }

        return "/images/no-image.jpg"; // Default fallback
    }

    // ============================
    // SEARCH & SEO METHODS
    // ============================

    /**
     * Hämta searchable text för indexing
     */
    public String getSearchableText() {
        StringBuilder sb = new StringBuilder();

        if (name != null) sb.append(name).append(" ");
        if (description != null) sb.append(description).append(" ");
        if (brand != null) sb.append(brand).append(" ");
        if (model != null) sb.append(model).append(" ");
        if (color != null) sb.append(color).append(" ");
        if (category != null) sb.append(category).append(" ");
        if (tags != null) sb.append(tags).append(" ");

        return sb.toString().toLowerCase().trim();
    }

    /**
     * Hämta meta title för SEO (fallback till product name)
     */
    public String getEffectiveMetaTitle() {
        return metaTitle != null && !metaTitle.trim().isEmpty() ? metaTitle : name;
    }

    /**
     * Hämta meta description för SEO (fallback till short description)
     */
    public String getEffectiveMetaDescription() {
        if (metaDescription != null && !metaDescription.trim().isEmpty()) {
            return metaDescription;
        }
        return getShortDescription();
    }

    // ============================
    // VALIDATION METHODS
    // ============================

    /**
     * Validera att produkten har all nödvändig information
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
                price != null && price.compareTo(BigDecimal.ZERO) >= 0 &&
                category != null && !category.trim().isEmpty() &&
                stockQuantity != null && stockQuantity >= 0;
    }

    /**
     * Hämta lista över validation errors
     */
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();

        if (name == null || name.trim().isEmpty()) {
            errors.add("Product name is required");
        }

        if (price == null) {
            errors.add("Price is required");
        } else if (price.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Price cannot be negative");
        }

        if (category == null || category.trim().isEmpty()) {
            errors.add("Category is required");
        }

        if (stockQuantity == null) {
            errors.add("Stock quantity is required");
        } else if (stockQuantity < 0) {
            errors.add("Stock quantity cannot be negative");
        }

        return errors;
    }

    // ============================
    // CONSTRUCTORS (additional)
    // ============================

    /**
     * Convenience constructor för basic product creation
     */
    public Product(String name, String description, BigDecimal price, String category, Integer stockQuantity) {
        this();
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stockQuantity = stockQuantity;
    }

    /**
     * Constructor för quick product setup
     */
    public Product(String name, BigDecimal price, String category) {
        this(name, null, price, category, 0);
    }
}