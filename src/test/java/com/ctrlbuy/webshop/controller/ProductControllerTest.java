package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ProductController productController;

    private Product testProduct;
    private List<Product> testProducts;
    private Page<Product> testProductPage;

    @BeforeEach
    void setUp() {
        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Game");
        testProduct.setCategory("Action");
        testProduct.setPrice(new BigDecimal("59.99"));
        testProduct.setStockQuantity(10);
        testProduct.setDescription("Test description");

        // Setup test products list
        testProducts = new ArrayList<>();
        testProducts.add(testProduct);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Game 2");
        product2.setCategory("RPG");
        product2.setPrice(new BigDecimal("49.99"));
        testProducts.add(product2);

        // Setup paginated products
        testProductPage = new PageImpl<>(testProducts, PageRequest.of(0, 10), testProducts.size());

        // Common mocks - använd rätta metodnamn
        when(productService.findAllActive(any(Pageable.class))).thenReturn(testProductPage);
        when(productService.findById(1L)).thenReturn(Optional.of(testProduct));
    }

    @Test
    void testListProducts_NoFilters() {
        // Test GET /products (no filters) - använd rätt service method
        when(productService.getAllProducts()).thenReturn(testProducts);

        String result = productController.listProducts(null, null, model);

        assertEquals("products", result);
        verify(model).addAttribute(eq("products"), any());
        verify(model).addAttribute(eq("pageTitle"), eq("Alla produkter"));
        verify(productService).getAllProducts();
    }

    @Test
    void testListProducts_WithCategory() {
        // Test GET /products?category=Action - rätt attribut och service call
        when(productService.getProductsByCategory("Action")).thenReturn(testProducts);

        String result = productController.listProducts("Action", null, model);

        assertEquals("products", result);
        verify(model).addAttribute(eq("products"), any());
        verify(model).addAttribute(eq("selectedCategory"), eq("Action"));
        verify(model).addAttribute(eq("pageTitle"), eq("Produkter i kategorin: Action"));
        verify(productService).getProductsByCategory("Action");
    }

    @Test
    void testListProducts_WithSearch() {
        // Test GET /products?search=game - använd rätt metod utan Pageable
        when(productService.searchProducts("game")).thenReturn(testProducts);

        String result = productController.listProducts(null, "game", model);

        assertEquals("products", result);
        verify(model).addAttribute(eq("products"), any());
        verify(model).addAttribute(eq("searchTerm"), eq("game"));
        verify(productService).searchProducts("game");
    }

    @Test
    void testViewProduct_Success() {
        // Test GET /products/{id} - ✅ FIXAT: Korrekt template för produktdetaljer
        String result = productController.viewProduct(1L, model, redirectAttributes);

        assertEquals("product-detail", result); // ✅ RÄTT: viewProduct ska returnera product-detail
        verify(model).addAttribute(eq("product"), eq(testProduct));
        verify(productService).findById(1L);
    }

    @Test
    void testViewProduct_NotFound() {
        // Test GET /products/{id} med produkt som inte finns - rätt redirect
        when(productService.findById(999L)).thenReturn(Optional.empty());

        String result = productController.viewProduct(999L, model, redirectAttributes);

        assertEquals("redirect:/products", result);
        verify(redirectAttributes).addFlashAttribute(anyString(), anyString());
    }

    @Test
    void testSearchProducts_API() {
        // Test GET /products/api/search?q=game - använd rätt service method
        when(productService.searchActiveProducts("game")).thenReturn(new ArrayList<>());

        List<Product> result = productController.searchProducts("game");

        assertEquals(0, result.size()); // Förvänta tom lista
        verify(productService).searchActiveProducts("game");
    }

    @Test
    void testViewCategory() {
        // Test GET /products/category/{category} - använd rätt service method
        when(productService.getProductsByCategory("Action")).thenReturn(testProducts);

        String result = productController.viewCategory("Action", model);

        assertEquals("products", result);
        verify(productService).getProductsByCategory("Action");
    }

    @Test
    void testViewSaleProducts() {
        // Test GET /products/sale - rätt template
        when(productService.getProductsOnSale()).thenReturn(testProducts);

        String result = productController.viewSaleProducts(model);

        assertEquals("products", result);
        verify(model).addAttribute(eq("products"), eq(testProducts));
        verify(productService).getProductsOnSale();
    }

    @Test
    void testViewPopularProducts() {
        // Test GET /products/popular - använd rätt service method med parameter
        when(productService.getPopularProducts(12)).thenReturn(new ArrayList<>());

        String result = productController.viewPopularProducts(model);

        assertEquals("products", result);
        verify(model).addAttribute(eq("products"), any());
        verify(model).addAttribute(eq("pageTitle"), eq("Populära produkter"));
        verify(productService).getPopularProducts(12);
    }

    @Test
    void testViewNewProducts() {
        // Test GET /products/new - använd rätt service method
        when(productService.getNewestProducts(12)).thenReturn(new ArrayList<>());

        String result = productController.viewNewProducts(model);

        assertEquals("products", result);
        verify(model).addAttribute(eq("products"), any());
        verify(model).addAttribute(eq("pageTitle"), eq("Nya produkter"));
        verify(productService).getNewestProducts(12);
    }

    @Test
    void testCheckStock_InStock() {
        // Test GET /products/{id}/stock - använd rätt service method
        when(productService.getProductByIdWithoutView(1L)).thenReturn(Optional.of(testProduct));

        boolean result = productController.checkStock(1L);

        assertTrue(result);
        verify(productService).getProductByIdWithoutView(1L);
    }

    @Test
    void testCheckStock_OutOfStock() {
        // Test GET /products/{id}/stock - product out of stock
        testProduct.setStockQuantity(0);
        when(productService.getProductByIdWithoutView(1L)).thenReturn(Optional.of(testProduct));

        boolean result = productController.checkStock(1L);

        assertFalse(result);
        verify(productService).getProductByIdWithoutView(1L);
    }

    @Test
    void testCheckStock_ProductNotFound() {
        // Test GET /products/{id}/stock - product not found
        when(productService.getProductByIdWithoutView(999L)).thenReturn(Optional.empty());

        boolean result = productController.checkStock(999L);

        assertFalse(result);
        verify(productService).getProductByIdWithoutView(999L);
    }

    @Test
    void testHandleError() {
        // Test exception handling - rätt template
        Exception testException = new RuntimeException("Test error");

        String result = productController.handleError(testException, model);

        assertEquals("products", result);
        verify(model).addAttribute(eq("error"), anyString());
    }
}