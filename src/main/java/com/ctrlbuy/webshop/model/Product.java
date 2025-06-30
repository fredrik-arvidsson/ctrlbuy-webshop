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

    // 🔥 NYTT FÄLT - BRAND
    @Column(name = "brand", length = 100)
    private String brand;

    // 🔥 NYTT FÄLT - SKU
    @Column(name = "sku", length = 50, unique = true)
    private String sku;

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

    // 🔥 REA-FÄLT
    @Column(name = "on_sale")
    private Boolean onSale = false;

    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "sale_start_date")
    private LocalDateTime saleStartDate;

    @Column(name = "sale_end_date")
    private LocalDateTime saleEndDate;

    @Column(name = "sale_description")
    private String saleDescription;

    // 🌍 INTERNATIONALISERING - NYA FÄLT
    @Column(name = "origin_country", length = 100)
    private String originCountry;

    @Column(name = "is_domestic")
    private Boolean isDomestic = false;

    @Column(name = "estimated_delivery_days")
    private Integer estimatedDeliveryDays;

    @Column(name = "shipping_weight", precision = 8, scale = 3)
    private BigDecimal shippingWeight; // i kg

    @Column(name = "customs_code", length = 20)
    private String customsCode; // Tullkod för internationella leveranser

    @Column(name = "requires_special_handling")
    private Boolean requiresSpecialHandling = false;

    // Default constructor
    public Product() {
        this.viewCount = 0;
        this.featured = false;
        this.active = true;
        this.onSale = false;
        this.isDomestic = false;
        this.requiresSpecialHandling = false;
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

    // Utökad constructor med brand
    public Product(String name, String brand, String category, BigDecimal price, Integer stockQuantity, String description) {
        this(name, category, price, stockQuantity, description);
        this.brand = brand;
    }

    // Utökad constructor med internationella fält
    public Product(String name, String category, BigDecimal price, Integer stockQuantity,
                   String description, String originCountry, Boolean isDomestic) {
        this(name, category, price, stockQuantity, description);
        this.originCountry = originCountry;
        this.isDomestic = isDomestic;
        this.estimatedDeliveryDays = isDomestic ? 1 : 5; // Standard leveranstider
    }

    // ⭐ JPA Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();

        // Sätt standard leveranstider baserat på ursprung
        if (estimatedDeliveryDays == null) {
            estimatedDeliveryDays = (isDomestic != null && isDomestic) ? 1 : 5;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // Automatisk kontroll av REA-datum
        checkSaleStatus();
    }

    // 🔥 REA-HANTERING
    private void checkSaleStatus() {
        LocalDateTime now = LocalDateTime.now();

        // Om REA har startdatum och slutdatum
        if (saleStartDate != null && saleEndDate != null) {
            // REA ska vara aktiv om vi är mellan start- och slutdatum
            boolean shouldBeOnSale = (now.isAfter(saleStartDate) || now.isEqual(saleStartDate)) &&
                    (now.isBefore(saleEndDate) || now.isEqual(saleEndDate));

            // Uppdatera onSale automatiskt
            if (shouldBeOnSale && salePrice != null) {
                this.onSale = true;
            } else if (!shouldBeOnSale) {
                this.onSale = false;
            }
        }
    }

    // ================== GETTERS AND SETTERS - BEFINTLIGA ==================

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

    // 🔥 BRAND GETTER & SETTER
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    // 🔥 SKU GETTER & SETTER
    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    // ================== REA GETTERS & SETTERS ==================

    public Boolean getOnSale() {
        return onSale != null ? onSale : false;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public LocalDateTime getSaleStartDate() {
        return saleStartDate;
    }

    public void setSaleStartDate(LocalDateTime saleStartDate) {
        this.saleStartDate = saleStartDate;
        checkSaleStatus(); // Kontrollera status när datum ändras
    }

    public LocalDateTime getSaleEndDate() {
        return saleEndDate;
    }

    public void setSaleEndDate(LocalDateTime saleEndDate) {
        this.saleEndDate = saleEndDate;
        checkSaleStatus(); // Kontrollera status när datum ändras
    }

    public String getSaleDescription() {
        return saleDescription;
    }

    public void setSaleDescription(String saleDescription) {
        this.saleDescription = saleDescription;
    }

    // ================== INTERNATIONALISERING GETTERS & SETTERS ==================

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
        // Automatisk bestämning av isDomestic baserat på ursprungsland
        if (originCountry != null) {
            this.isDomestic = "Sverige".equalsIgnoreCase(originCountry) ||
                    "Sweden".equalsIgnoreCase(originCountry) ||
                    "SE".equalsIgnoreCase(originCountry);
        }
    }

    public Boolean getIsDomestic() {
        return isDomestic != null ? isDomestic : false;
    }

    public void setIsDomestic(Boolean isDomestic) {
        this.isDomestic = isDomestic;
    }

    public Integer getEstimatedDeliveryDays() {
        return estimatedDeliveryDays;
    }

    public void setEstimatedDeliveryDays(Integer estimatedDeliveryDays) {
        this.estimatedDeliveryDays = estimatedDeliveryDays;
    }

    public BigDecimal getShippingWeight() {
        return shippingWeight;
    }

    public void setShippingWeight(BigDecimal shippingWeight) {
        this.shippingWeight = shippingWeight;
    }

    public String getCustomsCode() {
        return customsCode;
    }

    public void setCustomsCode(String customsCode) {
        this.customsCode = customsCode;
    }

    public Boolean getRequiresSpecialHandling() {
        return requiresSpecialHandling != null ? requiresSpecialHandling : false;
    }

    public void setRequiresSpecialHandling(Boolean requiresSpecialHandling) {
        this.requiresSpecialHandling = requiresSpecialHandling;
    }

    // ================== BOOLEAN CONVENIENCE METHODS ==================

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

    public boolean isDomestic() {
        return getIsDomestic();
    }

    public boolean requiresSpecialHandling() {
        return getRequiresSpecialHandling();
    }

    // 🔥 BRAND CONVENIENCE METHOD
    public boolean hasBrand() {
        return brand != null && !brand.trim().isEmpty();
    }

    // 🔥 SKU CONVENIENCE METHOD
    public boolean hasSku() {
        return sku != null && !sku.trim().isEmpty();
    }

    // ================== REA-METODER ==================

    public boolean isOnSale() {
        // Kontrollera både flaggan och att vi har ett giltigt REA-pris
        boolean hasValidSalePrice = salePrice != null && salePrice.compareTo(BigDecimal.ZERO) > 0;
        boolean isCurrentlyOnSale = getOnSale();

        // Om vi har datum, kontrollera att vi är inom REA-perioden
        if (saleStartDate != null && saleEndDate != null) {
            LocalDateTime now = LocalDateTime.now();
            boolean withinSalePeriod = (now.isAfter(saleStartDate) || now.isEqual(saleStartDate)) &&
                    (now.isBefore(saleEndDate) || now.isEqual(saleEndDate));
            return hasValidSalePrice && isCurrentlyOnSale && withinSalePeriod;
        }

        // Om inga datum är satta, använd bara flaggan och priset
        return hasValidSalePrice && isCurrentlyOnSale;
    }

    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    public boolean isLowStock() {
        return stockQuantity != null && stockQuantity > 0 && stockQuantity < 5;
    }

    // ================== UTILITY METHODS ==================

    public void incrementViewCount() {
        this.viewCount = getViewCount() + 1;
    }

    // ================== REA-BERÄKNINGAR ==================

    public BigDecimal getDiscountPercentage() {
        if (isOnSale() && getCurrentPrice() != null && getOriginalDisplayPrice() != null) {
            BigDecimal originalPrice = getOriginalDisplayPrice();
            BigDecimal currentPrice = getCurrentPrice();

            if (originalPrice.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal difference = originalPrice.subtract(currentPrice);
                return difference.divide(originalPrice, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100"));
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getSavings() {
        if (isOnSale() && getCurrentPrice() != null && getOriginalDisplayPrice() != null) {
            return getOriginalDisplayPrice().subtract(getCurrentPrice());
        }
        return BigDecimal.ZERO;
    }

    // ================== SMART PRIS-HANTERING ==================

    /**
     * Returnerar det pris som ska visas för kunden (REA-pris eller ordinarie pris)
     */
    public BigDecimal getCurrentPrice() {
        if (isOnSale() && salePrice != null) {
            return salePrice;
        }
        return price;
    }

    /**
     * Returnerar det ursprungliga priset för visning (används för genomstruken text vid REA)
     */
    public BigDecimal getOriginalDisplayPrice() {
        if (isOnSale()) {
            // Om vi har originalPrice satt, använd det, annars använd price
            return originalPrice != null ? originalPrice : price;
        }
        return null; // Inget ursprungligt pris att visa om inte REA
    }

    // ================== REA-HJÄLPMETODER ==================

    /**
     * Sätter en produkt på REA med start- och slutdatum
     */
    public void putOnSale(BigDecimal salePrice, LocalDateTime startDate, LocalDateTime endDate, String description) {
        // Spara ursprungligt pris om det inte redan är sparat
        if (this.originalPrice == null) {
            this.originalPrice = this.price;
        }

        this.salePrice = salePrice;
        this.saleStartDate = startDate;
        this.saleEndDate = endDate;
        this.saleDescription = description;
        this.onSale = true;

        checkSaleStatus(); // Kontrollera om REA:n ska vara aktiv nu
    }

    /**
     * Sätter en produkt på REA utan slutdatum (manuell hantering)
     */
    public void putOnSale(BigDecimal salePrice, String description) {
        if (this.originalPrice == null) {
            this.originalPrice = this.price;
        }

        this.salePrice = salePrice;
        this.saleDescription = description;
        this.onSale = true;
        // Inga datum - måste hanteras manuellt
    }

    /**
     * Tar bort REA och återställer ordinarie pris
     */
    public void removeSale() {
        this.onSale = false;
        this.salePrice = null;
        this.saleStartDate = null;
        this.saleEndDate = null;
        this.saleDescription = null;
        // Behåll originalPrice för framtida REA:or
    }

    /**
     * Kontrollerar om REA:n har gått ut
     */
    public boolean isSaleExpired() {
        if (saleEndDate != null) {
            return LocalDateTime.now().isAfter(saleEndDate);
        }
        return false;
    }

    /**
     * Kontrollerar om REA:n inte har startat än
     */
    public boolean isSalePending() {
        if (saleStartDate != null) {
            return LocalDateTime.now().isBefore(saleStartDate);
        }
        return false;
    }

    // ================== LEVERANS- OCH FRAKT-METODER ==================

    /**
     * Beräknar uppskattad leveranstid baserat på produktens ursprung
     */
    public String getEstimatedDeliveryText() {
        if (estimatedDeliveryDays == null) {
            return "Kontakta oss för leveranstid";
        }

        if (isDomestic()) {
            if (estimatedDeliveryDays == 1) {
                return "Leverans nästa arbetsdag";
            } else if (estimatedDeliveryDays <= 3) {
                return estimatedDeliveryDays + " arbetsdagar";
            }
        }

        if (estimatedDeliveryDays <= 7) {
            return estimatedDeliveryDays + " arbetsdagar";
        } else {
            int weeks = estimatedDeliveryDays / 7;
            int remainingDays = estimatedDeliveryDays % 7;
            if (remainingDays == 0) {
                return weeks + (weeks == 1 ? " vecka" : " veckor");
            } else {
                return weeks + (weeks == 1 ? " vecka" : " veckor") + " och " + remainingDays + " dagar";
            }
        }
    }

    /**
     * Returnerar en beskrivning av produktens ursprung för kunden
     */
    public String getOriginDescription() {
        if (originCountry == null || originCountry.trim().isEmpty()) {
            return "Ursprung ej specificerat";
        }

        if (isDomestic()) {
            return "Svensk produkt";
        } else {
            return "Importerad från " + originCountry;
        }
    }

    /**
     * Kontrollerar om produkten kan levereras snabbt (inom 3 dagar)
     */
    public boolean isFastDelivery() {
        return estimatedDeliveryDays != null && estimatedDeliveryDays <= 3;
    }

    /**
     * Kontrollerar om produkten kräver tullhantering
     */
    public boolean requiresCustomsHandling() {
        return !isDomestic() && (customsCode != null && !customsCode.trim().isEmpty());
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", sku='" + sku + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", salePrice=" + salePrice +
                ", onSale=" + onSale +
                ", stockQuantity=" + stockQuantity +
                ", viewCount=" + viewCount +
                ", originCountry='" + originCountry + '\'' +
                ", isDomestic=" + isDomestic +
                ", estimatedDeliveryDays=" + estimatedDeliveryDays +
                ", createdAt=" + createdAt +
                ", active=" + active +
                ", featured=" + featured +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}