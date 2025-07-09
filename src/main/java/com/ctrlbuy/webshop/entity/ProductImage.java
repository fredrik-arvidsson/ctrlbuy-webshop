package com.ctrlbuy.webshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * ProductImage Entity - Railway-optimerad med Lombok och förbättrad funktionalitet
 * ✅ UPPDATERAD: Lombok integration, business logic, och Railway-kompatibilitet
 * ✅ FIXAD: Index-namn matchar faktiska kolumnnamn (display_order)
 */
@Entity
@Table(name = "product_images", indexes = {
        @Index(name = "idx_product_image_product", columnList = "product_id"),
        @Index(name = "idx_product_image_primary", columnList = "isPrimary"),
        @Index(name = "idx_product_image_order", columnList = "display_order"),    // ✅ FIXAD: display_order
        @Index(name = "idx_product_image_type", columnList = "imageType")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"product"}) // Undvik lazy loading i toString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // ============================
    // PRODUCT RELATIONSHIP
    // ============================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_image_product"))
    private Product product;

    // ============================
    // IMAGE INFORMATION
    // ============================

    @Column(nullable = false, length = 500)
    @EqualsAndHashCode.Include
    private String imageUrl;

    @Column(length = 255)
    private String altText;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ImageType imageType = ImageType.PRODUCT;

    @Column(length = 100)
    private String fileName;

    @Column(length = 50)
    private String mimeType;

    // ============================
    // IMAGE DIMENSIONS & METADATA
    // ============================

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(length = 20)
    private String resolution;

    @Column(precision = 3)
    private Float aspectRatio;

    // ============================
    // DISPLAY CONFIGURATION
    // ============================

    @Column(name = "isPrimary")
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "isActive")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "isLocked")
    @Builder.Default
    private Boolean isLocked = false;

    @Column(name = "isVisible")
    @Builder.Default
    private Boolean isVisible = true;

    // ============================
    // IMAGE VARIANTS
    // ============================

    @Column(length = 500)
    private String thumbnailUrl;

    @Column(length = 500)
    private String mediumUrl;

    @Column(length = 500)
    private String largeUrl;

    @Column(length = 500)
    private String originalUrl;

    // ============================
    // SEO & ACCESSIBILITY
    // ============================

    @Column(length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(length = 200)
    private String caption;

    @Column(length = 100)
    private String photographer;

    @Column(length = 100)
    private String source;

    // ============================
    // AUDIT FIELDS
    // ============================

    @Column(updatable = false, name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

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
        if (uploadedAt == null) {
            uploadedAt = now;
        }

        // Set defaults
        if (isPrimary == null) isPrimary = false;
        if (isActive == null) isActive = true;
        if (isLocked == null) isLocked = false;
        if (isVisible == null) isVisible = true;
        if (displayOrder == null) displayOrder = 0;
        if (sortOrder == null) sortOrder = 0;
        if (imageType == null) imageType = ImageType.PRODUCT;

        // Calculate aspect ratio if dimensions are available
        calculateAspectRatio();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateAspectRatio();
    }

    // ============================
    // ENUMS
    // ============================

    public enum ImageType {
        PRODUCT("Product Image", "Produktbild"),
        THUMBNAIL("Thumbnail", "Miniatyrbild"),
        GALLERY("Gallery", "Galleri"),
        DETAIL("Detail", "Detaljbild"),
        LIFESTYLE("Lifestyle", "Livsstilsbild"),
        TECHNICAL("Technical", "Teknisk bild"),
        PACKAGING("Packaging", "Förpackning"),
        INSTRUCTION("Instruction", "Instruktion"),
        WARRANTY("Warranty", "Garanti"),
        CERTIFICATE("Certificate", "Certifikat");

        private final String displayName;
        private final String swedishName;

        ImageType(String displayName, String swedishName) {
            this.displayName = displayName;
            this.swedishName = swedishName;
        }

        public String getDisplayName() { return displayName; }
        public String getSwedishName() { return swedishName; }
    }

    // ============================
    // BUSINESS LOGIC METHODS
    // ============================

    /**
     * Beräkna aspect ratio baserat på width/height
     */
    private void calculateAspectRatio() {
        if (width != null && height != null && height > 0) {
            this.aspectRatio = width.floatValue() / height.floatValue();
        }
    }

    /**
     * Kontrollera om bilden är synlig för kunder
     */
    public boolean isDisplayable() {
        return Boolean.TRUE.equals(isActive) &&
                Boolean.TRUE.equals(isVisible) &&
                imageUrl != null && !imageUrl.trim().isEmpty();
    }

    /**
     * Kontrollera om bilden kan redigeras
     */
    public boolean isEditable() {
        return !Boolean.TRUE.equals(isLocked);
    }

    /**
     * Hämta bästa tillgängliga bild-URL baserat på storlek
     */
    public String getBestImageUrl(ImageSize preferredSize) {
        switch (preferredSize) {
            case THUMBNAIL:
                return thumbnailUrl != null ? thumbnailUrl : imageUrl;
            case MEDIUM:
                return mediumUrl != null ? mediumUrl :
                        (largeUrl != null ? largeUrl : imageUrl);
            case LARGE:
                return largeUrl != null ? largeUrl :
                        (originalUrl != null ? originalUrl : imageUrl);
            case ORIGINAL:
                return originalUrl != null ? originalUrl : imageUrl;
            default:
                return imageUrl;
        }
    }

    /**
     * Enum för bildstorlekar
     */
    public enum ImageSize {
        THUMBNAIL, MEDIUM, LARGE, ORIGINAL
    }

    /**
     * Hämta formatted filstorlek
     */
    public String getFormattedFileSize() {
        if (fileSize == null) {
            return "Unknown";
        }

        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }

    /**
     * Hämta bildformat från filename eller mimeType
     */
    public String getImageFormat() {
        if (mimeType != null) {
            switch (mimeType.toLowerCase()) {
                case "image/jpeg":
                case "image/jpg":
                    return "JPEG";
                case "image/png":
                    return "PNG";
                case "image/gif":
                    return "GIF";
                case "image/webp":
                    return "WebP";
                case "image/svg+xml":
                    return "SVG";
                default:
                    return mimeType.substring(6).toUpperCase();
            }
        }

        if (fileName != null) {
            String extension = getFileExtension(fileName);
            return extension.toUpperCase();
        }

        if (imageUrl != null) {
            String extension = getFileExtension(imageUrl);
            return extension.toUpperCase();
        }

        return "Unknown";
    }

    /**
     * Extrahera filextension från filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1);
        }

        return "";
    }

    /**
     * Kontrollera om bilden är i landskapsformat
     */
    public boolean isLandscape() {
        return aspectRatio != null && aspectRatio > 1.0f;
    }

    /**
     * Kontrollera om bilden är i porträttformat
     */
    public boolean isPortrait() {
        return aspectRatio != null && aspectRatio < 1.0f;
    }

    /**
     * Kontrollera om bilden är kvadratisk
     */
    public boolean isSquare() {
        return aspectRatio != null && Math.abs(aspectRatio - 1.0f) < 0.1f;
    }

    /**
     * Hämta effective alt text (fallback till title eller description)
     */
    public String getEffectiveAltText() {
        if (altText != null && !altText.trim().isEmpty()) {
            return altText;
        }

        if (title != null && !title.trim().isEmpty()) {
            return title;
        }

        if (description != null && !description.trim().isEmpty()) {
            String shortDesc = description.length() > 100 ?
                    description.substring(0, 97) + "..." : description;
            return shortDesc;
        }

        // Fallback till product name om tillgängligt
        if (product != null && product.getName() != null) {
            return product.getName() + " - " + imageType.getSwedishName();
        }

        return "Produktbild";
    }

    /**
     * Generera responsive image srcset för olika skärmar
     */
    public String generateSrcSet() {
        StringBuilder srcSet = new StringBuilder();

        if (thumbnailUrl != null) {
            srcSet.append(thumbnailUrl).append(" 300w");
        }

        if (mediumUrl != null) {
            if (srcSet.length() > 0) srcSet.append(", ");
            srcSet.append(mediumUrl).append(" 600w");
        }

        if (largeUrl != null) {
            if (srcSet.length() > 0) srcSet.append(", ");
            srcSet.append(largeUrl).append(" 1200w");
        }

        if (originalUrl != null) {
            if (srcSet.length() > 0) srcSet.append(", ");
            srcSet.append(originalUrl).append(" 1920w");
        }

        return srcSet.toString();
    }

    // ============================
    // BOOLEAN ALIAS METHODS (för compatibility)
    // ============================

    /**
     * Standard boolean naming convention aliases
     */
    public Boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        this.isPrimary = primary;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        this.isActive = active;
    }

    public Boolean isLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        this.isLocked = locked;
    }

    public Boolean isVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        this.isVisible = visible;
    }

    // ============================
    // VALIDATION METHODS
    // ============================

    /**
     * Validera att bilden har all nödvändig information
     */
    public boolean isValid() {
        return imageUrl != null && !imageUrl.trim().isEmpty() &&
                product != null;
    }

    /**
     * Kontrollera om bild-URL är giltig
     */
    public boolean hasValidUrl() {
        return imageUrl != null &&
                !imageUrl.trim().isEmpty() &&
                (imageUrl.startsWith("http://") ||
                        imageUrl.startsWith("https://") ||
                        imageUrl.startsWith("/"));
    }

    // ============================
    // CONSTRUCTORS (additional convenience)
    // ============================

    /**
     * Convenience constructor för basic image creation
     */
    public ProductImage(Product product, String imageUrl, String altText) {
        this();
        this.product = product;
        this.imageUrl = imageUrl;
        this.altText = altText;
    }

    /**
     * Constructor för primary image
     */
    public ProductImage(Product product, String imageUrl, String altText, boolean isPrimary) {
        this(product, imageUrl, altText);
        this.isPrimary = isPrimary;
    }

    /**
     * Constructor med image type
     */
    public ProductImage(Product product, String imageUrl, String altText, ImageType imageType) {
        this(product, imageUrl, altText);
        this.imageType = imageType;
    }
}