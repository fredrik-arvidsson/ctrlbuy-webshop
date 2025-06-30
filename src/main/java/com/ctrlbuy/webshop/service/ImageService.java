package com.ctrlbuy.webshop.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ImageService {
    
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);
    private final ResourceLoader resourceLoader;
    
    private static final String DEFAULT_PHONE_IMAGE = "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400";
    private static final String DEFAULT_LAPTOP_IMAGE = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400";
    private static final String DEFAULT_PRODUCT_IMAGE = "https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=400";
    
    public ImageService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    public String getProductImageUrl(String productName, String category, String originalImageUrl) {
        if (originalImageUrl != null && originalImageUrl.startsWith("http")) {
            return originalImageUrl;
        }
        
        String localImagePath = getLocalImagePath(originalImageUrl);
        if (localImageExists(localImagePath)) {
            logger.debug("🖼️ Använder lokal bild: {}", localImagePath);
            return localImagePath;
        }
        
        return getCategoryFallbackImage(category);
    }
    
    private boolean localImageExists(String imagePath) {
        try {
            Resource resource = resourceLoader.getResource("classpath:static" + imagePath);
            return resource.exists() && resource.isReadable();
        } catch (Exception e) {
            return false;
        }
    }
    
    private String getLocalImagePath(String originalImageUrl) {
        if (originalImageUrl != null && originalImageUrl.startsWith("/images/")) {
            return originalImageUrl;
        }
        return "/images/placeholder-product.jpg";
    }
    
    private String getCategoryFallbackImage(String category) {
        if (category == null) return DEFAULT_PRODUCT_IMAGE;
        
        String lowerCategory = category.toLowerCase();
        if (lowerCategory.contains("smartphone") || lowerCategory.contains("phone")) {
            return DEFAULT_PHONE_IMAGE;
        } else if (lowerCategory.contains("laptop") || lowerCategory.contains("computer")) {
            return DEFAULT_LAPTOP_IMAGE;
        }
        return DEFAULT_PRODUCT_IMAGE;
    }
}
