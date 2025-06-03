package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Product Controller Unit Tests")
class ProductControllerTest {

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
    private Page<Product> testPage;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test product description");
        testProduct.setPrice(new BigDecimal("199.99"));
        testProduct.setCategory("Electronics");
        testProduct.setStockQuantity(5);
        testProduct.setActive(true);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Second Product");
        product2.setDescription("Second test product");
        product2.setPrice(new BigDecimal("299.99"));
        product2.setCategory("Books");
        product2.setStockQuantity(10);
        product2.setActive(true);

        testProducts = Arrays.asList(testProduct, product2);
        testPage = new PageImpl<>(testProducts, PageRequest.of(0, 12), testProducts.size());
    }

    @Test
    @DisplayName("Product listing - Default parameters")
    void testListProductsDefault() {
        // Arrange
        when(productService.findAllActive(any(Pageable.class))).thenReturn(testPage);
        when(productService.getAllCategories()).thenReturn(Arrays.asList("Electronics", "Books"));
        when(productService.getPopularProducts(5)).thenReturn(testProducts);
        when(productService.getNewestProducts(5)).thenReturn(testProducts);

        // Act
        String viewName = productController.listProducts(0, 12, "name", "asc", null, null, null, null, null, false, model);

        // Assert
        assertEquals("products/list", viewName);
        verify(model).addAttribute("products", testProducts);
        verify(model).addAttribute("pageTitle", "Alla produkter");
        verify(model).addAttribute("currentPage", 0);
        verify(model).addAttribute("totalPages", 1);
        
        System.out.println("✅ Product listing default - PASS");
    }

    @Test
    @DisplayName("Product listing - Search functionality")
    void testListProductsWithSearch() {
        // Arrange
        String searchTerm = "test";
        when(productService.searchProducts(eq(searchTerm), any(Pageable.class))).thenReturn(testPage);
        when(productService.getAllCategories()).thenReturn(Arrays.asList("Electronics", "Books"));
        when(productService.getPopularProducts(5)).thenReturn(testProducts);
        when(productService.getNewestProducts(5)).thenReturn(testProducts);

        // Act
        String viewName = productController.listProducts(0, 12, "name", "asc", null, searchTerm, null, null, null, false, model);

        // Assert
        assertEquals("products/list", viewName);
        verify(model).addAttribute("pageTitle", "Sökresultat för: " + searchTerm);
        verify(model).addAttribute("searchTerm", searchTerm);
        
        System.out.println("✅ Product listing search - PASS");
    }

    @Test
    @DisplayName("Product listing - Category filter")
    void testListProductsWithCategory() {
        // Arrange
        String category = "Electronics";
        when(productService.findByCategory(eq(category), any(Pageable.class))).thenReturn(testPage);
        when(productService.getAllCategories()).thenReturn(Arrays.asList("Electronics", "Books"));
        when(productService.getPopularProducts(5)).thenReturn(testProducts);
        when(productService.getNewestProducts(5)).thenReturn(testProducts);

        // Act
        String viewName = productController.listProducts(0, 12, "name", "asc", category, null, null, null, null, false, model);

        // Assert
        assertEquals("products/list", viewName);
        verify(model).addAttribute("pageTitle", "Produkter i kategorin: " + category);
        verify(model).addAttribute("selectedCategory", category);
        
        System.out.println("✅ Product listing category - PASS");
    }

    @Test
    @DisplayName("Product listing - Sale products")
    void testListProductsOnSale() {
        // Arrange
        when(productService.getProductsOnSale()).thenReturn(testProducts);
        when(productService.getAllCategories()).thenReturn(Arrays.asList("Electronics", "Books"));
        when(productService.getPopularProducts(5)).thenReturn(testProducts);
        when(productService.getNewestProducts(5)).thenReturn(testProducts);

        // Act
        String viewName = productController.listProducts(0, 12, "name", "asc", null, null, null, null, null, true, model);

        // Assert
        assertEquals("products/list", viewName);
        verify(model).addAttribute("pageTitle", "Produkter på rea");
        
        System.out.println("✅ Product listing on sale - PASS");
    }

    @Test
    @DisplayName("Product detail - Existing product")
    void testViewProductExisting() {
        // Arrange
        when(productService.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productService.getProductsByCategory("Electronics")).thenReturn(testProducts);

        // Act
        String viewName = productController.viewProduct(1L, model, redirectAttributes);

        // Assert
        assertEquals("products/detail", viewName);
        verify(model).addAttribute("product", testProduct);
        verify(model).addAttribute("inStock", true);
        
        System.out.println("✅ Product detail existing - PASS");
    }

    @Test
    @DisplayName("Product detail - Non-existing product")
    void testViewProductNonExisting() {
        // Arrange
        when(productService.findById(999L)).thenReturn(Optional.empty());

        // Act
        String viewName = productController.viewProduct(999L, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/products", viewName);
        verify(redirectAttributes).addFlashAttribute("error", "Produkten hittades inte.");
        
        System.out.println("✅ Product detail non-existing - PASS");
    }

    @Test
    @DisplayName("API search endpoint")
    void testSearchProductsAPI() {
        // Arrange
        when(productService.searchActiveProducts("test")).thenReturn(testProducts);

        // Act
        List<Product> result = productController.searchProducts("test");

        // Assert
        assertEquals(2, result.size());
        
        System.out.println("✅ API search - PASS");
    }

    @Test
    @DisplayName("API search - Short query")
    void testSearchProductsAPIShortQuery() {
        // Act
        List<Product> result = productController.searchProducts("a");

        // Assert
        assertEquals(0, result.size());
        
        System.out.println("✅ API search short query - PASS");
    }

    @Test
    @DisplayName("Category view")
    void testViewCategory() {
        // Arrange
        when(productService.findByCategory(eq("Electronics"), any(Pageable.class))).thenReturn(testPage);
        when(productService.getAllCategories()).thenReturn(Arrays.asList("Electronics", "Books"));
        when(productService.getPopularProducts(5)).thenReturn(testProducts);
        when(productService.getNewestProducts(5)).thenReturn(testProducts);

        // Act
        String viewName = productController.viewCategory("Electronics", model);

        // Assert
        assertEquals("products/list", viewName);
        
        System.out.println("✅ Category view - PASS");
    }

    @Test
    @DisplayName("Sale products page")
    void testViewSaleProducts() {
        // Arrange
        when(productService.getProductsOnSale()).thenReturn(testProducts);
        when(productService.getAllCategories()).thenReturn(Arrays.asList("Electronics", "Books"));
        when(productService.getPopularProducts(5)).thenReturn(testProducts);
        when(productService.getNewestProducts(5)).thenReturn(testProducts);

        // Act
        String viewName = productController.viewSaleProducts(model);

        // Assert
        assertEquals("products/list", viewName);
        
        System.out.println("✅ Sale products page - PASS");
    }

    @Test
    @DisplayName("Popular products page")
    void testViewPopularProducts() {
        // Arrange
        when(productService.getPopularProducts(20)).thenReturn(testProducts);
        when(productService.getAllCategories()).thenReturn(Arrays.asList("Electronics", "Books"));

        // Act
        String viewName = productController.viewPopularProducts(model);

        // Assert
        assertEquals("products/list", viewName);
        verify(model).addAttribute("pageTitle", "Populära produkter");
        
        System.out.println("✅ Popular products page - PASS");
    }

    @Test
    @DisplayName("New products page")
    void testViewNewProducts() {
        // Arrange
        when(productService.getNewestProducts(20)).thenReturn(testProducts);
        when(productService.getAllCategories()).thenReturn(Arrays.asList("Electronics", "Books"));

        // Act
        String viewName = productController.viewNewProducts(model);

        // Assert
        assertEquals("products/list", viewName);
        verify(model).addAttribute("pageTitle", "Nya produkter");
        
        System.out.println("✅ New products page - PASS");
    }

    @Test
    @DisplayName("Quick view API")
    void testQuickViewProduct() {
        // Arrange
        when(productService.getProductByIdWithoutView(1L)).thenReturn(Optional.of(testProduct));

        // Act
        Product result = productController.quickViewProduct(1L);

        // Assert
        assertEquals(testProduct, result);
        
        System.out.println("✅ Quick view API - PASS");
    }

    @Test
    @DisplayName("Stock check API")
    void testCheckStock() {
        // Arrange
        when(productService.getProductByIdWithoutView(1L)).thenReturn(Optional.of(testProduct));

        // Act
        boolean inStock = productController.checkStock(1L);

        // Assert
        assertTrue(inStock);
        
        System.out.println("✅ Stock check API - PASS");
    }
}
