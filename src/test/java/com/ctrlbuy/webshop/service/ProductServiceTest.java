package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct1;
    private Product testProduct2;
    private Product lowStockProduct;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        // Setup test products
        testProduct1 = new Product();
        testProduct1.setId(1L);
        testProduct1.setName("Test Game 1");
        testProduct1.setDescription("A great action game");
        testProduct1.setCategory("Action");
        testProduct1.setPrice(new BigDecimal("59.99"));
        testProduct1.setStockQuantity(10);

        testProduct2 = new Product();
        testProduct2.setId(2L);
        testProduct2.setName("Test RPG Game");
        testProduct2.setDescription("An amazing RPG experience");
        testProduct2.setCategory("RPG");
        testProduct2.setPrice(new BigDecimal("49.99"));
        testProduct2.setStockQuantity(5);

        lowStockProduct = new Product();
        lowStockProduct.setId(3L);
        lowStockProduct.setName("Low Stock Game");
        lowStockProduct.setDescription("Almost sold out");
        lowStockProduct.setCategory("Adventure");
        lowStockProduct.setPrice(new BigDecimal("29.99"));
        lowStockProduct.setStockQuantity(2);

        testProducts = Arrays.asList(testProduct1, testProduct2, lowStockProduct);
    }

    // ========================================
    // BASIC RETRIEVAL TESTS
    // ========================================

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(testProducts);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertEquals(3, result.size());
        assertEquals("Test Game 1", result.get(0).getName());
        verify(productRepository).findAll();
    }

    @Test
    void getAllActiveProducts_ShouldReturnActiveProducts() {
        // Arrange
        when(productRepository.findActiveProductsProxy()).thenReturn(testProducts);

        // Act
        List<Product> result = productService.getAllActiveProducts();

        // Assert
        assertEquals(3, result.size());
        verify(productRepository).findActiveProductsProxy();
    }

    @Test
    void findById_WithExistingProduct_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct1));

        // Act
        Optional<Product> result = productService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Game 1", result.get().getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void findById_WithNonExistentProduct_ShouldReturnEmpty() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.findById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository).findById(999L);
    }

    @Test
    void getProductById_WithExistingProduct_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct1));

        // Act
        Product result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Game 1", result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_WithNonExistentProduct_ShouldReturnNull() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Product result = productService.getProductById(999L);

        // Assert
        assertNull(result);
        verify(productRepository).findById(999L);
    }

    @Test
    void getProductByIdWithoutView_ShouldReturnProductWithoutSideEffects() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct1));

        // Act
        Optional<Product> result = productService.getProductByIdWithoutView(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Game 1", result.get().getName());
        verify(productRepository).findById(1L);
        // Verify no additional calls (no view counting)
        verify(productRepository, times(1)).findById(1L);
    }

    // ========================================
    // CATEGORY TESTS
    // ========================================

    @Test
    void getProductsByCategory_ShouldReturnProductsInCategory() {
        // Arrange
        List<Product> actionProducts = Arrays.asList(testProduct1);
        when(productRepository.findByCategory("Action")).thenReturn(actionProducts);

        // Act
        List<Product> result = productService.getProductsByCategory("Action");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Action", result.get(0).getCategory());
        verify(productRepository).findByCategory("Action");
    }

    @Test
    void getActiveProductsByCategory_ShouldReturnCategoryProducts() {
        // Arrange
        List<Product> rpgProducts = Arrays.asList(testProduct2);
        when(productRepository.findByCategory("RPG")).thenReturn(rpgProducts);

        // Act
        List<Product> result = productService.getActiveProductsByCategory("RPG");

        // Assert
        assertEquals(1, result.size());
        assertEquals("RPG", result.get(0).getCategory());
        verify(productRepository).findByCategory("RPG");
    }

    @Test
    void getAllCategories_ShouldReturnDistinctCategories() {
        // Arrange
        List<String> categories = Arrays.asList("Action", "RPG", "Adventure");
        when(productRepository.findDistinctCategories()).thenReturn(categories);

        // Act
        List<String> result = productService.getAllCategories();

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.contains("Action"));
        assertTrue(result.contains("RPG"));
        assertTrue(result.contains("Adventure"));
        verify(productRepository).findDistinctCategories();
    }

    // ========================================
    // SEARCH TESTS
    // ========================================

    @Test
    void searchProducts_ShouldReturnMatchingProducts() {
        // Arrange
        List<Product> searchResults = Arrays.asList(testProduct1, testProduct2);
        when(productRepository.findByNameContainingOrDescriptionContainingIgnoreCase("game", "game"))
                .thenReturn(searchResults);

        // Act
        List<Product> result = productService.searchProducts("game");

        // Assert
        assertEquals(2, result.size());
        verify(productRepository).findByNameContainingOrDescriptionContainingIgnoreCase("game", "game");
    }

    @Test
    void searchActiveProducts_ShouldReturnActiveMatchingProducts() {
        // Arrange
        List<Product> searchResults = Arrays.asList(testProduct1);
        when(productRepository.findByNameContainingOrDescriptionContainingIgnoreCase("action", "action"))
                .thenReturn(searchResults);

        // Act
        List<Product> result = productService.searchActiveProducts("action");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Game 1", result.get(0).getName());
        verify(productRepository).findByNameContainingOrDescriptionContainingIgnoreCase("action", "action");
    }

    @Test
    void searchProducts_WithPagination_ShouldReturnPagedResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(testProducts, pageable, testProducts.size());
        when(productRepository.searchProducts("test", pageable)).thenReturn(page);

        // Act
        Page<Product> result = productService.searchProducts("test", pageable);

        // Assert
        assertEquals(3, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        verify(productRepository).searchProducts("test", pageable);
    }

    // ========================================
    // PRICE RANGE TESTS
    // ========================================

    @Test
    void getProductsByPriceRange_ShouldReturnProductsInRange() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("30.00");
        BigDecimal maxPrice = new BigDecimal("60.00");
        List<Product> priceRangeProducts = Arrays.asList(testProduct1, testProduct2);
        when(productRepository.findByPriceBetween(minPrice, maxPrice))
                .thenReturn(priceRangeProducts);

        // Act
        List<Product> result = productService.getProductsByPriceRange(minPrice, maxPrice);

        // Assert
        assertEquals(2, result.size());
        verify(productRepository).findByPriceBetween(minPrice, maxPrice);
    }

    // ========================================
    // SPECIAL PRODUCT LISTS TESTS
    // ========================================

    @Test
    void getPopularProducts_ShouldReturnPopularProducts() {
        // Arrange
        List<Product> popularProducts = Arrays.asList(testProduct1, testProduct2);
        when(productRepository.findRecentProductsAsPopular(any(Pageable.class)))
                .thenReturn(popularProducts);

        // Act
        List<Product> result = productService.getPopularProducts();

        // Assert
        assertEquals(2, result.size());
        verify(productRepository).findRecentProductsAsPopular(any(Pageable.class));
    }

    @Test
    void getPopularProducts_WithLimit_ShouldReturnLimitedResults() {
        // Arrange
        List<Product> popularProducts = Arrays.asList(testProduct1);
        when(productRepository.findRecentProductsAsPopular(any(Pageable.class)))
                .thenReturn(popularProducts);

        // Act
        List<Product> result = productService.getPopularProducts(5);

        // Assert
        assertEquals(1, result.size());
        verify(productRepository).findRecentProductsAsPopular(any(Pageable.class));
    }

    @Test
    void getNewArrivals_ShouldReturnNewestProducts() {
        // Arrange
        List<Product> newProducts = Arrays.asList(testProduct2, testProduct1);
        when(productRepository.findNewestProductsByIdProxy(any(Pageable.class)))
                .thenReturn(newProducts);

        // Act
        List<Product> result = productService.getNewArrivals();

        // Assert
        assertEquals(2, result.size());
        verify(productRepository).findNewestProductsByIdProxy(any(Pageable.class));
    }

    @Test
    void getNewestProducts_WithLimit_ShouldReturnLimitedResults() {
        // Arrange
        List<Product> newProducts = Arrays.asList(testProduct1, testProduct2);
        when(productRepository.findNewestProductsByIdProxy(any(Pageable.class)))
                .thenReturn(newProducts);

        // Act
        List<Product> result = productService.getNewestProducts(3);

        // Assert
        assertEquals(2, result.size());
        verify(productRepository).findNewestProductsByIdProxy(any(Pageable.class));
    }

    @Test
    void getProductsOnSale_ShouldReturnSaleProducts() {
        // Arrange
        List<Product> saleProducts = Arrays.asList(testProduct1);
        when(productRepository.findSaleProductsProxy()).thenReturn(saleProducts);

        // Act
        List<Product> result = productService.getProductsOnSale();

        // Assert
        assertEquals(1, result.size());
        verify(productRepository).findSaleProductsProxy();
    }

    // ========================================
    // STOCK MANAGEMENT TESTS
    // ========================================

    @Test
    void getLowStockProducts_ShouldReturnLowStockProducts() {
        // Arrange
        List<Product> lowStockProducts = Arrays.asList(lowStockProduct);
        when(productRepository.findByStockQuantityLessThan(5)).thenReturn(lowStockProducts);

        // Act
        List<Product> result = productService.getLowStockProducts(5);

        // Assert
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getStockQuantity());
        verify(productRepository).findByStockQuantityLessThan(5);
    }

    @Test
    void updateStock_WithExistingProduct_ShouldUpdateStock() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct1));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct1);

        // Act
        boolean result = productService.updateStock(1L, 15);

        // Assert
        assertTrue(result);
        assertEquals(15, testProduct1.getStockQuantity());
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct1);
    }

    @Test
    void updateStock_WithNonExistentProduct_ShouldReturnFalse() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        boolean result = productService.updateStock(999L, 15);

        // Assert
        assertFalse(result);
        verify(productRepository).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void decreaseStock_WithSufficientStock_ShouldDecreaseStock() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct1));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct1);

        // Act
        boolean result = productService.decreaseStock(1L, 3);

        // Assert
        assertTrue(result);
        assertEquals(7, testProduct1.getStockQuantity());
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct1);
    }

    @Test
    void decreaseStock_WithInsufficientStock_ShouldReturnFalse() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct1));

        // Act
        boolean result = productService.decreaseStock(1L, 15);

        // Assert
        assertFalse(result);
        assertEquals(10, testProduct1.getStockQuantity()); // Should remain unchanged
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void decreaseStock_WithNonExistentProduct_ShouldReturnFalse() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        boolean result = productService.decreaseStock(999L, 5);

        // Assert
        assertFalse(result);
        verify(productRepository).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    // ========================================
    // CRUD OPERATIONS TESTS
    // ========================================

    @Test
    void saveProduct_ShouldSaveAndReturnProduct() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setName("New Game");
        when(productRepository.save(newProduct)).thenReturn(testProduct1);

        // Act
        Product result = productService.saveProduct(newProduct);

        // Assert
        assertNotNull(result);
        verify(productRepository).save(newProduct);
    }

    @Test
    void createProduct_ShouldCreateNewProduct() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setId(5L); // Should be set to null
        newProduct.setName("Brand New Game");
        when(productRepository.save(any(Product.class))).thenReturn(testProduct1);

        // Act
        Product result = productService.createProduct(newProduct);

        // Assert
        assertNotNull(result);
        assertNull(newProduct.getId()); // Should be set to null for new product
        verify(productRepository).save(newProduct);
    }

    @Test
    void updateProduct_WithExistingProduct_ShouldUpdateProduct() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Game");
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product result = productService.updateProduct(1L, updatedProduct);

        // Assert
        assertNotNull(result);
        assertEquals(1L, updatedProduct.getId());
        verify(productRepository).findById(1L);
        verify(productRepository).save(updatedProduct);
    }

    @Test
    void updateProduct_WithNonExistentProduct_ShouldThrowException() {
        // Arrange
        Product updatedProduct = new Product();
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.updateProduct(999L, updatedProduct));
        assertTrue(exception.getMessage().contains("hittades inte"));
        verify(productRepository).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        // Arrange & Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository).deleteById(1L);
    }

    @Test
    void hardDeleteProduct_ShouldPermanentlyDeleteProduct() {
        // Arrange & Act
        productService.hardDeleteProduct(1L);

        // Assert
        verify(productRepository).deleteById(1L);
    }

    // ========================================
    // PAGINATION TESTS
    // ========================================

    @Test
    void findAllActive_WithPagination_ShouldReturnPagedResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);
        when(productRepository.findActiveProductsProxy()).thenReturn(testProducts);

        // Act
        Page<Product> result = productService.findAllActive(pageable);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
        verify(productRepository).findActiveProductsProxy();
    }

    @Test
    void getActiveProducts_WithSortingAndPagination_ShouldReturnSortedPagedResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        Page<Product> page = new PageImpl<>(testProducts, pageable, testProducts.size());
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        Page<Product> result = productService.getActiveProducts(0, 10, "name", "asc");

        // Assert
        assertEquals(3, result.getContent().size());
        verify(productRepository).findAll(any(Pageable.class));
    }

    @Test
    void findByCategory_WithPagination_ShouldReturnPagedCategoryResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(Arrays.asList(testProduct1), pageable, 1);
        when(productRepository.findByCategory("Action", pageable)).thenReturn(page);

        // Act
        Page<Product> result = productService.findByCategory("Action", pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals("Action", result.getContent().get(0).getCategory());
        verify(productRepository).findByCategory("Action", pageable);
    }

    // ========================================
    // STATISTICS TESTS
    // ========================================

    @Test
    void getTotalActiveProducts_ShouldReturnProductCount() {
        // Arrange
        when(productRepository.count()).thenReturn(3L);

        // Act
        long result = productService.getTotalActiveProducts();

        // Assert
        assertEquals(3L, result);
        verify(productRepository).count();
    }

    @Test
    void getTotalProductsOnSale_ShouldReturnSaleProductCount() {
        // Arrange
        List<Product> saleProducts = Arrays.asList(testProduct1, testProduct2);
        when(productRepository.findSaleProductsProxy()).thenReturn(saleProducts);

        // Act
        long result = productService.getTotalProductsOnSale();

        // Assert
        assertEquals(2L, result);
        verify(productRepository).findSaleProductsProxy();
    }

    @Test
    void getAveragePrice_ShouldReturnAveragePrice() {
        // Arrange
        BigDecimal averagePrice = new BigDecimal("45.00");
        when(productRepository.findAveragePrice()).thenReturn(averagePrice);

        // Act
        BigDecimal result = productService.getAveragePrice();

        // Assert
        assertEquals(averagePrice, result);
        verify(productRepository).findAveragePrice();
    }

    // ========================================
    // ADVANCED SEARCH TESTS
    // ========================================

    @Test
    void findProductsWithFilters_ShouldReturnFilteredResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> filteredPage = new PageImpl<>(Arrays.asList(testProduct1), pageable, 1);
        when(productRepository.findProductsWithFilters(
                eq("Action"), any(BigDecimal.class), any(BigDecimal.class),
                eq(true), eq("game"), eq(pageable)))
                .thenReturn(filteredPage);

        // Act
        Page<Product> result = productService.findProductsWithFilters(
                "Action", new BigDecimal("10.00"), new BigDecimal("100.00"),
                true, "game", pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        verify(productRepository).findProductsWithFilters(
                eq("Action"), any(BigDecimal.class), any(BigDecimal.class),
                eq(true), eq("game"), eq(pageable));
    }

    @Test
    void getRelatedProducts_ShouldReturnRelatedProducts() {
        // Arrange
        List<Product> relatedProducts = Arrays.asList(testProduct2);
        // ✅ FIX: Använd eq() för alla string/Long parameters när du använder any()
        when(productRepository.findRelatedProductsByCategory(eq("Action"), eq(1L), any(Pageable.class)))
                .thenReturn(relatedProducts);

        // Act
        List<Product> result = productService.getRelatedProducts("Action", 1L, 5);

        // Assert
        assertEquals(1, result.size());
        assertEquals("RPG", result.get(0).getCategory()); // Different from testProduct1
        verify(productRepository).findRelatedProductsByCategory(eq("Action"), eq(1L), any(Pageable.class));
    }

    // ========================================
    // UTILITY METHOD TESTS
    // ========================================

    @Test
    void incrementViewCount_ShouldLogMessage() {
        // Act (method currently just logs, doesn't interact with repository)
        productService.incrementViewCount(1L);

        // Assert (verify no exceptions thrown - method is placeholder)
        // This test ensures the method exists and doesn't crash
        assertDoesNotThrow(() -> productService.incrementViewCount(1L));
    }

    @Test
    void debugProductCount_ShouldExecuteWithoutErrors() {
        // Arrange
        when(productRepository.count()).thenReturn(3L);
        when(productRepository.countInStockProducts()).thenReturn(2L);
        when(productRepository.countOutOfStockProducts()).thenReturn(1L);
        when(productRepository.findAll()).thenReturn(testProducts);

        // Act & Assert
        assertDoesNotThrow(() -> productService.debugProductCount());
        verify(productRepository).count();
        verify(productRepository).countInStockProducts();
        verify(productRepository).countOutOfStockProducts();
        verify(productRepository).findAll();
    }
}