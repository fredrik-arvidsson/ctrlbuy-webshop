package com.ctrlbuy.webshop.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, name = "stockQuantity")
    private Integer stockQuantity;

    @Column(name = "imageUrl")
    private String imageUrl;

    private String brand;
    private String model;
    private String color;
    private String sku;
    private String barcode;
    private String dimensions;
    private Float weight;

    @Column(name = "originCountry")
    private String originCountry;

    // Additional product fields
    @Column(precision = 10, scale = 2, name = "costPrice")
    private BigDecimal costPrice;

    @Column(precision = 10, scale = 2, name = "originalPrice")
    private BigDecimal originalPrice;

    @Column(precision = 10, scale = 2, name = "salePrice")
    private BigDecimal salePrice;

    @Column(precision = 5, scale = 2, name = "discountPercentage")
    private BigDecimal discountPercentage;

    @Column(name = "saleStartDate")
    private LocalDateTime saleStartDate;

    @Column(name = "saleEndDate")
    private LocalDateTime saleEndDate;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(name = "reviewCount")
    private Integer reviewCount;

    @Column(name = "viewCount")
    private Integer viewCount;

    // Inventory management
    @Column(name = "minimumStockLevel")
    private Integer minimumStockLevel;

    @Column(name = "maximumStockLevel")
    private Integer maximumStockLevel;

    @Column(name = "reorderPoint")
    private Integer reorderPoint;

    @Column(name = "supplierId")
    private Long supplierId;

    // Product status flags
    @Column(columnDefinition = "BIT DEFAULT 1", name = "isActive")
    private Boolean isActive = true;

    @Column(columnDefinition = "BIT DEFAULT 0", name = "isFeatured")
    private Boolean isFeatured = false;

    @Column(columnDefinition = "BIT DEFAULT 0", name = "isOnSale")
    private Boolean isOnSale = false;

    // SEO and metadata
    @Column(length = 200, name = "metaTitle")
    private String metaTitle;

    @Column(length = 500, name = "metaDescription")
    private String metaDescription;

    @Column(length = 500)
    private String tags;

    // NYTT: Sale description field
    @Column(length = 1000, name = "saleDescription")
    private String saleDescription;

    // Product specifications
    @Column(name = "warrantyMonths")
    private Integer warrantyMonths;

    @Column(name = "estimatedDeliveryDays")
    private Integer estimatedDeliveryDays;

    // Audit fields
    @Column(updatable = false, name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> productImages = new ArrayList<>();

    // Constructors
    public Product() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.isFeatured = false;
        this.isOnSale = false;
        this.viewCount = 0;
        this.reviewCount = 0;
    }

    public Product(String name, String description, BigDecimal price, String category, Integer stockQuantity) {
        this();
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stockQuantity = stockQuantity;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDateTime getSaleStartDate() {
        return saleStartDate;
    }

    public void setSaleStartDate(LocalDateTime saleStartDate) {
        this.saleStartDate = saleStartDate;
    }

    public LocalDateTime getSaleEndDate() {
        return saleEndDate;
    }

    public void setSaleEndDate(LocalDateTime saleEndDate) {
        this.saleEndDate = saleEndDate;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getMinimumStockLevel() {
        return minimumStockLevel;
    }

    public void setMinimumStockLevel(Integer minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public Integer getMaximumStockLevel() {
        return maximumStockLevel;
    }

    public void setMaximumStockLevel(Integer maximumStockLevel) {
        this.maximumStockLevel = maximumStockLevel;
    }

    public Integer getReorderPoint() {
        return reorderPoint;
    }

    public void setReorderPoint(Integer reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    // ========================================
    // FIXADE BOOLEAN GETTERS/SETTERS - STANDARD NAMING
    // ========================================

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // ALIAS METHODS för standard boolean naming convention
    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        this.isActive = active;
    }

    public Boolean getActive() {
        return isActive;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    // ALIAS METHODS för featured
    public Boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(Boolean featured) {
        this.isFeatured = featured;
    }

    public Boolean getIsOnSale() {
        return isOnSale;
    }

    public void setIsOnSale(Boolean isOnSale) {
        this.isOnSale = isOnSale;
    }

    // ALIAS METHODS för onSale
    public Boolean isOnSale() {
        return isOnSale;
    }

    public void setOnSale(Boolean onSale) {
        this.isOnSale = onSale;
    }

    public Boolean getOnSale() {
        return isOnSale;
    }

    // ========================================
    // NYA SAKNADE METODER
    // ========================================

    public String getSaleDescription() {
        return saleDescription;
    }

    public void setSaleDescription(String saleDescription) {
        this.saleDescription = saleDescription;
    }

    public BigDecimal getCurrentPrice() {
        return getEffectivePrice(); // Använd befintlig logik
    }

    public BigDecimal getOriginalDisplayPrice() {
        return originalPrice != null ? originalPrice : price;
    }

    public BigDecimal getSavings() {
        if (isOnSale() && salePrice != null && originalPrice != null) {
            return originalPrice.subtract(salePrice);
        }
        return BigDecimal.ZERO;
    }

    // ========================================
    // ÖVRIGA GETTERS/SETTERS
    // ========================================

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(Integer warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    public Integer getEstimatedDeliveryDays() {
        return estimatedDeliveryDays;
    }

    public void setEstimatedDeliveryDays(Integer estimatedDeliveryDays) {
        this.estimatedDeliveryDays = estimatedDeliveryDays;
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

    public List<ProductImage> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ProductImage> productImages) {
        this.productImages = productImages;
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    public boolean isLowStock() {
        return minimumStockLevel != null && stockQuantity != null && stockQuantity <= minimumStockLevel;
    }

    public BigDecimal getEffectivePrice() {
        if (Boolean.TRUE.equals(isOnSale) && salePrice != null) {
            return salePrice;
        }
        return price;
    }

    public void incrementViewCount() {
        this.viewCount = (viewCount == null ? 0 : viewCount) + 1;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", isActive=" + isActive +
                ", isFeatured=" + isFeatured +
                ", isOnSale=" + isOnSale +
                '}';
    }
}