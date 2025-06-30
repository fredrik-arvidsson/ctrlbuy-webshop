package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    // ================================
    // BILDHANTERING - UNSPLASH INTEGRATION
    // ================================

    /**
     * Förbättrar en produkt med kategori-baserad Unsplash-bild
     * Ersätter 404-fel med snygga produktbilder
     */
    private Product enhanceProductWithImage(Product product) {
        try {
            // Om produkten redan har en fungerande bild-URL, behåll den
            String currentImageUrl = product.getImageUrl();

            // Kontrollera om bilden behöver ersättas (404-fel, tom, eller gamla /images/products/ paths)
            if (currentImageUrl == null || currentImageUrl.trim().isEmpty() ||
                    currentImageUrl.contains("404") || currentImageUrl.contains("placeholder") ||
                    currentImageUrl.startsWith("/images/products/")) {

                String categoryImageUrl = getCategoryImageUrl(product.getCategory());
                product.setImageUrl(categoryImageUrl);

                log.debug("🖼️ Ersatte bild för produkt '{}' (kategori: {}) med: {}",
                        product.getName(), product.getCategory(), categoryImageUrl);
            }

            return product;
        } catch (Exception e) {
            log.warn("⚠️ Kunde inte förbättra bild för produkt '{}': {}",
                    product.getName(), e.getMessage());
            return product;
        }
    }

    /**
     * Hämtar kategori-specifik Unsplash-bild URL
     * Använder direkta Unsplash URL:er för snabbare laddning
     */
    private String getCategoryImageUrl(String category) {
        if (category == null || category.trim().isEmpty()) {
            return getGenericProductImage();
        }

        String normalizedCategory = category.toLowerCase().trim();

        // Direkta Unsplash-bilder för konsistenta resultat
        return switch (normalizedCategory) {
            case "smartphones", "telefoner", "mobiler" ->
                    "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=300&fit=crop";

            case "laptops", "datorer", "bärbara" ->
                    "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=300&fit=crop";

            case "gaming", "spel", "spelkonsoler" ->
                    "https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?w=400&h=300&fit=crop";

            case "tablets", "surfplattor" ->
                    "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400&h=300&fit=crop";

            case "accessories", "tillbehör", "headphones", "hörlurar" ->
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=300&fit=crop";

            case "watches", "klockor", "smartwatch", "smartwatches" ->
                    "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=300&fit=crop";

            case "cameras", "kameror", "fotografering" ->
                    "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=400&h=300&fit=crop";

            case "audio", "ljud", "speakers", "högtalare" ->
                    "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=400&h=300&fit=crop";

            case "tv", "television", "monitors", "skärmar" ->
                    "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=400&h=300&fit=crop";

            case "wearables", "fitnesstracker" ->
                    "https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=400&h=300&fit=crop";

            case "smart-home", "smarta-hem" ->
                    "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=300&fit=crop";

            case "cables", "kablar", "chargers", "laddare" ->
                    "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=400&h=300&fit=crop";

            case "storage", "lagring", "memory", "minne" ->
                    "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=400&h=300&fit=crop";

            case "vr" ->
                    "https://images.unsplash.com/photo-1593508512255-86ab42a8e620?w=400&h=300&fit=crop";

            default -> {
                log.debug("🖼️ Okänd kategori '{}', använder generisk teknikbild", category);
                yield getGenericProductImage();
            }
        };
    }

    /**
     * Generisk produktbild för okända kategorier
     */
    private String getGenericProductImage() {
        return "https://images.unsplash.com/photo-1468495244123-6c6c332eeece?w=400&h=300&fit=crop";
    }

    // ================================
    // BEFINTLIGA METODER - OFÖRÄNDRADE
    // ================================

    /**
     * Hämtar alla produkter från databasen
     * FIXAT - explicit transaction och debugging
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        log.info("🔍 DEBUGGING: Hämtar alla produkter från databas");

        try {
            List<Product> products = productRepository.findAll();
            log.info("🔍 DEBUGGING: Repository returnerade {} produkter", products.size());

            // Debug första produkterna
            if (!products.isEmpty()) {
                Product first = products.get(0);
                log.info("🔍 DEBUGGING: Första produkten: ID={}, Name={}, Category={}",
                        first.getId(), first.getName(), first.getCategory());
            } else {
                log.warn("🔍 DEBUGGING: Repository returnerade tom lista!");

                // Testa direct count
                long count = productRepository.count();
                log.warn("🔍 DEBUGGING: Repository count() returnerar: {}", count);
            }

            return products.stream().map(this::enhanceProductWithImage).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("🔍 DEBUGGING: Fel vid hämtning av produkter: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Hämtar alla produkter med paginering (för ProductController)
     * BEFINTLIG - använder befintlig databasstruktur
     */
    public Page<Product> findAllActive(Pageable pageable) {
        log.debug("Hämtar produkter med paginering: {}", pageable);

        // Använd repository proxy för "aktiva" produkter
        List<Product> activeProducts = productRepository.findActiveProductsProxy();

        // Konvertera till Page med manuell paginering
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), activeProducts.size());

        List<Product> pageContent = start < activeProducts.size() ?
                activeProducts.subList(start, end) : List.of();

        return new PageImpl<>(pageContent, pageable, activeProducts.size());
    }

    /**
     * Hämtar populära produkter
     * UPPDATERAD - använder ID som proxy för popularitet
     */
    public List<Product> getPopularProducts() {
        log.debug("Hämtar populära produkter (top 8 baserat på ID)");
        Pageable pageable = PageRequest.of(0, 8);
        return productRepository.findRecentProductsAsPopular(pageable);
    }

    /**
     * Hämtar produkter efter kategori
     * BEFINTLIG - fungerar med nuvarande databas
     */
    public List<Product> getProductsByCategory(String category) {
        log.debug("Hämtar produkter i kategori: {}", category);
        return productRepository.findByCategory(category);
    }

    /**
     * Hämtar produkter efter kategori med paginering
     * BEFINTLIG - fungerar med nuvarande databas
     */
    public Page<Product> findByCategory(String category, Pageable pageable) {
        log.debug("Hämtar produkter i kategori {} med paginering", category);
        return productRepository.findByCategory(category, pageable);
    }

    /**
     * Hämtar en produkt baserat på dess ID
     * UPPDATERAD - kommer att räkna visningar när view_count läggs till
     */
    public Product getProductById(Long id) {
        log.debug("Hämtar produkt med ID: {}", id);
        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()) {
            Product p = product.get();
            // TODO: Implementera visningsräkning när view_count kolumn läggs till
            // p.incrementViewCount();
            // productRepository.save(p);
            log.debug("Hämtade produkt: {}", p.getName());
            return enhanceProductWithImage(p); // Lägg till bildförbättring
        }

        return null;
    }

    /**
     * Hämtar en produkt baserat på dess ID (Optional version)
     * UPPDATERAD - kommer att räkna visningar när view_count läggs till
     */
    public Optional<Product> findById(Long id) {
        log.debug("Hämtar produkt (Optional) med ID: {}", id);
        Optional<Product> productOpt = productRepository.findById(id);

        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            // TODO: Implementera visningsräkning när view_count kolumn läggs till
            // product.incrementViewCount();
            // productRepository.save(product);
            log.debug("Hämtade produkt: {}", product.getName());
            return Optional.of(enhanceProductWithImage(product)); // Lägg till bildförbättring
        }

        return productOpt;
    }

    /**
     * Söker efter produkter baserat på nyckelord
     * BEFINTLIG - fungerar med nuvarande databas
     */
    public List<Product> searchProducts(String keyword) {
        log.debug("Söker produkter med nyckelord: {}", keyword);
        return productRepository.findByNameContainingOrDescriptionContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::enhanceProductWithImage)
                .collect(Collectors.toList());
    }

    /**
     * Söker efter produkter med paginering
     * UPPDATERAD - använder ny Query-metod
     */
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        log.debug("Söker produkter med paginering för: {}", keyword);
        Page<Product> results = productRepository.searchProducts(keyword, pageable);

        // Förbättra bilder för sökresultat
        List<Product> enhancedProducts = results.getContent()
                .stream()
                .map(this::enhanceProductWithImage)
                .collect(Collectors.toList());

        return new PageImpl<>(enhancedProducts, pageable, results.getTotalElements());
    }

    /**
     * Hämtar alla kategorier
     * UPPDATERAD - använder optimerad Query
     */
    public List<String> getAllCategories() {
        log.debug("Hämtar alla kategorier");
        return productRepository.findDistinctCategories();
    }

    /**
     * Hämtar produkter inom ett visst prisintervall
     * BEFINTLIG - fungerar med nuvarande databas
     */
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Hämtar produkter inom prisintervall: {} - {}", minPrice, maxPrice);
        return productRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(this::enhanceProductWithImage)
                .collect(Collectors.toList());
    }

    /**
     * Inkrementerar visningsantalet för en produkt
     * TILLFÄLLIG - kommer att implementeras när view_count läggs till
     */
    public void incrementViewCount(Long productId) {
        log.debug("Visningsräkning för produkt: {} (väntar på view_count kolumn)", productId);
        // TODO: Implementera när view_count kolumn läggs till
        /*
        productRepository.findById(productId)
                .ifPresent(product -> {
                    product.incrementViewCount();
                    productRepository.save(product);
                    log.debug("Uppdaterade visningar för '{}' till {}",
                             product.getName(), product.getViewCount());
                });
        */
    }

    /**
     * Hämtar nyligen tillagda produkter
     * UPPDATERAD - använder ID som proxy för datum
     */
    public List<Product> getNewArrivals() {
        log.debug("Hämtar nyligen tillagda produkter (top 10 baserat på ID)");
        Pageable pageable = PageRequest.of(0, 10);
        return productRepository.findNewestProductsByIdProxy(pageable)
                .stream()
                .map(this::enhanceProductWithImage)
                .collect(Collectors.toList());
    }

    /**
     * Skapar eller uppdaterar en produkt
     * BEFINTLIG - fungerar med nuvarande databas
     */
    public Product saveProduct(Product product) {
        log.info("Sparar produkt: {}", product.getName());
        return productRepository.save(product);
    }

    /**
     * Tar bort en produkt baserat på dess ID
     * BEFINTLIG - fungerar med nuvarande databas
     */
    public void deleteProduct(Long id) {
        log.info("Tar bort produkt: {}", id);
        productRepository.deleteById(id);
    }

    /**
     * Debug-metod för att kontrollera produkter
     * UPPDATERAD - förbättrad statistik
     */
    public void debugProductCount() {
        long totalCount = productRepository.count();
        long inStockCount = productRepository.countInStockProducts();
        long outOfStockCount = productRepository.countOutOfStockProducts();

        System.out.println("=== PRODUCT DEBUG ===");
        System.out.println("Total products in database: " + totalCount);
        System.out.println("Products in stock: " + inStockCount);
        System.out.println("Products out of stock: " + outOfStockCount);

        if (totalCount > 0) {
            List<Product> sample = productRepository.findAll().stream().limit(3).toList();

            for (Product product : sample) {
                System.out.println("Sample product: ID=" + product.getId() +
                        ", Name=" + product.getName() +
                        ", Category=" + product.getCategory() +
                        ", Price=" + product.getPrice() +
                        ", Stock=" + product.getStockQuantity());
            }
        }
        System.out.println("===================");
    }

    // ================================
    // NYA METODER SOM FUNGERAR MED NUVARANDE DATABAS
    // ================================

    /**
     * Hämtar alla aktiva produkter (proxy-metod)
     */
    public List<Product> getAllActiveProducts() {
        log.debug("Hämtar alla 'aktiva' produkter (proxy)");
        return productRepository.findActiveProductsProxy()
                .stream()
                .map(this::enhanceProductWithImage)
                .collect(Collectors.toList());
    }

    /**
     * Hämtar produkter med paginering och sortering
     */
    public Page<Product> getActiveProducts(int page, int size, String sortBy, String sortDir) {
        log.debug("Hämtar produkter - sida: {}, storlek: {}, sortering: {} {}",
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // Använd findAll med sortering för nu
        Page<Product> results = productRepository.findAll(pageable);

        // Förbättra bilder
        List<Product> enhancedProducts = results.getContent()
                .stream()
                .map(this::enhanceProductWithImage)
                .collect(Collectors.toList());

        return new PageImpl<>(enhancedProducts, pageable, results.getTotalElements());
    }

    /**
     * Hämtar produkter per kategori (alias)
     */
    public List<Product> getActiveProductsByCategory(String category) {
        return getProductsByCategory(category);
    }

    /**
     * Hämta produkt utan att räkna visning
     */
    public Optional<Product> getProductByIdWithoutView(Long id) {
        log.debug("Hämtar produkt utan visningsräkning: {}", id);
        Optional<Product> productOpt = productRepository.findById(id);
        return productOpt.map(this::enhanceProductWithImage);
    }

    /**
     * Sök aktiva produkter (alias)
     */
    public List<Product> searchActiveProducts(String searchTerm) {
        return searchProducts(searchTerm);
    }

    /**
     * Hämta produkter på rea - ✅ FIXAT för tester
     */
    public List<Product> getProductsOnSale() {
        log.debug("🔥 Hämtar produkter på REA");
        try {
            // ✅ FIXA: Om produkterna har isOnSale() metod, filtrera med den
            List<Product> allProducts = productRepository.findAll();
            List<Product> saleProducts = allProducts.stream()
                    .filter(product -> {
                        try {
                            return product.isOnSale() && product.isActive();
                        } catch (Exception e) {
                            // Om isOnSale() inte finns, kolla direktattribut
                            return product.getOnSale() != null && product.getOnSale() &&
                                    product.getActive() != null && product.getActive();
                        }
                    })
                    .map(this::enhanceProductWithImage)
                    .collect(Collectors.toList());

            log.debug("🔥 Hittade {} produkter på rea", saleProducts.size());
            return saleProducts;

        } catch (Exception e) {
            log.warn("🔥 Fallback till proxy-metod för rea-produkter: {}", e.getMessage());
            return productRepository.findSaleProductsProxy()
                    .stream()
                    .map(this::enhanceProductWithImage)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Hämta populära produkter (med limit)
     */
    public List<Product> getPopularProducts(int limit) {
        log.debug("Hämtar {} populära produkter", limit);
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findRecentProductsAsPopular(pageable)
                .stream()
                .map(this::enhanceProductWithImage)
                .collect(Collectors.toList());
    }

    /**
     * Hämta nya produkter (med limit)
     */
    public List<Product> getNewestProducts(int limit) {
        log.debug("Hämtar {} nyaste produkter", limit);
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findNewestProductsByIdProxy(pageable)
                .stream()
                .map(this::enhanceProductWithImage)
                .collect(Collectors.toList());
    }

    /**
     * Hämta produkter med lågt lager
     */
    public List<Product> getLowStockProducts(int threshold) {
        log.debug("Hämtar produkter med lager under {}", threshold);
        return productRepository.findByStockQuantityLessThan(threshold)
                .stream()
                .map(this::enhanceProductWithImage)
                .collect(Collectors.toList());
    }

    // ================================
    // CRUD-OPERATIONER FÖR ADMIN
    // ================================

    public Product createProduct(Product product) {
        log.info("Skapar ny produkt: {}", product.getName());
        product.setId(null); // Säkerställ att det är en ny produkt
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        log.info("Uppdaterar produkt med ID: {}", id);

        return productRepository.findById(id)
                .map(existingProduct -> {
                    updatedProduct.setId(id);
                    // TODO: Behåll timestamps när kolumnerna läggs till
                    return productRepository.save(updatedProduct);
                })
                .orElseThrow(() -> new RuntimeException("Produkt med ID " + id + " hittades inte"));
    }

    public void hardDeleteProduct(Long id) {
        log.warn("Permanent borttagning av produkt: {}", id);
        productRepository.deleteById(id);
    }

    // ================================
    // LAGERHANTERING
    // ================================

    public boolean updateStock(Long productId, Integer newQuantity) {
        log.info("Uppdaterar lager för produkt {} till {}", productId, newQuantity);

        return productRepository.findById(productId)
                .map(product -> {
                    product.setStockQuantity(newQuantity);
                    productRepository.save(product);
                    return true;
                })
                .orElse(false);
    }

    public boolean decreaseStock(Long productId, Integer quantity) {
        log.info("Minskar lager för produkt {} med {}", productId, quantity);

        return productRepository.findById(productId)
                .map(product -> {
                    if (product.getStockQuantity() >= quantity) {
                        product.setStockQuantity(product.getStockQuantity() - quantity);
                        productRepository.save(product);
                        return true;
                    }
                    log.warn("Otillräckligt lager för produkt {}. Finns: {}, Begärt: {}",
                            productId, product.getStockQuantity(), quantity);
                    return false;
                })
                .orElse(false);
    }

    // ================================
    // STATISTIK SOM FUNGERAR NU - ✅ FIXAT
    // ================================

    public long getTotalActiveProducts() {
        return productRepository.count(); // Alla produkter för nu
    }

    /**
     * ✅ FIXAT: Räknar produkter på REA korrekt
     */
    public long getTotalProductsOnSale() {
        try {
            List<Product> saleProducts = getProductsOnSale();
            log.debug("🔥 getTotalProductsOnSale returnerar: {}", saleProducts.size());
            return saleProducts.size();
        } catch (Exception e) {
            log.warn("🔥 Fallback count för REA-produkter: {}", e.getMessage());
            return productRepository.findSaleProductsProxy().size(); // Fallback
        }
    }

    public BigDecimal getAveragePrice() {
        return productRepository.findAveragePrice();
    }

    // ================================
    // AVANCERAD SÖKNING OCH FILTRERING
    // ================================

    /**
     * Avancerad sökning med flera filter
     */
    public Page<Product> findProductsWithFilters(String category, BigDecimal minPrice,
                                                 BigDecimal maxPrice, Boolean inStock,
                                                 String searchTerm, Pageable pageable) {
        log.debug("Avancerad sökning med filter");
        Page<Product> results = productRepository.findProductsWithFilters(category, minPrice, maxPrice,
                inStock, searchTerm, pageable);

        // Förbättra bilder för filtrerade resultat
        List<Product> enhancedProducts = results.getContent()
                .stream()
                .map(this::enhanceProductWithImage)
                .collect(Collectors.toList());

        return new PageImpl<>(enhancedProducts, pageable, results.getTotalElements());
    }

    /**
     * Hitta relaterade produkter
     */
    public List<Product> getRelatedProducts(String category, Long excludeId, int limit) {
        log.debug("Hämtar relaterade produkter i kategori: {}", category);
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findRelatedProductsByCategory(category, excludeId, pageable)
                .stream()
                .map(this::enhanceProductWithImage)
                .collect(Collectors.toList());
    }
}