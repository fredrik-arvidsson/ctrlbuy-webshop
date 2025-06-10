package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.repository.ProductRepository;
import com.ctrlbuy.webshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import javax.sql.DataSource;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Home Controller Unit Tests")
class HomeControllerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @Mock
    private DataSource dataSource;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private HomeController homeController;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Home Product");
        testProduct.setDescription("Test product for home page");
        testProduct.setPrice(new BigDecimal("299.99"));
        testProduct.setCategory("Electronics");
        testProduct.setStockQuantity(10);
        testProduct.setActive(true);
    }

    @Test
    @DisplayName("Home page - Anonymous user")
    void testHomePageAnonymous() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct));

        // Act
        String viewName = homeController.home(model, null);

        // Assert
        assertEquals("home", viewName);
        verify(model).addAttribute("title", "Hem - CTRL+BUY Solutions");
        verify(model).addAttribute("isLoggedIn", false);
        verify(model).addAttribute(eq("featuredProducts"), any(List.class));
        
        System.out.println("✅ Home page anonymous - PASS");
    }

    @Test
    @DisplayName("Home page - Authenticated user")
    void testHomePageAuthenticated() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");

        // Act
        String viewName = homeController.home(model, authentication);

        // Assert
        assertEquals("home", viewName);
        verify(model).addAttribute("title", "Hem - CTRL+BUY Solutions");
        verify(model).addAttribute("isLoggedIn", true);
        verify(model).addAttribute("username", "testuser");
        verify(model).addAttribute(eq("featuredProducts"), any(List.class));
        
        System.out.println("✅ Home page authenticated - PASS");
    }

    @Test
    @DisplayName("Home page - Anonymous user object")
    void testHomePageAnonymousUser() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");

        // Act
        String viewName = homeController.home(model, authentication);

        // Assert
        assertEquals("home", viewName);
        verify(model).addAttribute("title", "Hem - CTRL+BUY Solutions");
        verify(model).addAttribute("isLoggedIn", false);
        verify(model).addAttribute(eq("featuredProducts"), any(List.class));
        
        System.out.println("✅ Home page anonymousUser - PASS");
    }

    @Test
    @DisplayName("Welcome page redirect")
    void testWelcomeRedirect() {
        // Act
        String result = homeController.welcome(model);

        // Assert
        assertEquals("redirect:/", result);
        
        System.out.println("✅ Welcome redirect - PASS");
    }

    @Test
    @DisplayName("About page")
    void testAboutPage() {
        // Act
        String viewName = homeController.about(model);

        // Assert
        assertEquals("about", viewName);
        verify(model).addAttribute("title", "Om oss - CtrlBuy");
        
        System.out.println("✅ About page - PASS");
    }

    @Test
    @DisplayName("Contact page")
    void testContactPage() {
        // Act
        String viewName = homeController.contact(model);

        // Assert
        assertEquals("contact", viewName);
        verify(model).addAttribute("title", "Kontakt - CtrlBuy");
        
        System.out.println("✅ Contact page - PASS");
    }

    @Test
    @DisplayName("Support page")
    void testSupportPage() {
        // Act
        String viewName = homeController.support(model);

        // Assert
        assertEquals("support", viewName);
        verify(model).addAttribute("title", "Support - CtrlBuy");
        
        System.out.println("✅ Support page - PASS");
    }

    @Test
    @DisplayName("Register page")
    void testRegisterPage() {
        // Act
        String viewName = homeController.register(model);

        // Assert
        assertEquals("register", viewName);
        verify(model).addAttribute("title", "Registrera dig - CtrlBuy");
        
        System.out.println("✅ Register page - PASS");
    }

    @Test
    @DisplayName("Profile page - Not authenticated")
    void testProfilePageNotAuthenticated() {
        // Act
        String result = homeController.profile(model, null);

        // Assert
        assertEquals("redirect:/login", result);
        
        System.out.println("✅ Profile page not authenticated - PASS");
    }

    @Test
    @DisplayName("Profile page - Authenticated")
    void testProfilePageAuthenticated() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");

        // Act
        String viewName = homeController.profile(model, authentication);

        // Assert
        assertEquals("profile", viewName);
        verify(model).addAttribute("title", "Min profil - CtrlBuy");
        verify(model).addAttribute("username", "testuser");
        
        System.out.println("✅ Profile page authenticated - PASS");
    }

    @Test
    @DisplayName("Home page - Empty product list")
    void testHomePageEmptyProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        String viewName = homeController.home(model, null);

        // Assert
        assertEquals("home", viewName);
        verify(model).addAttribute(eq("featuredProducts"), any(List.class));
        
        System.out.println("✅ Home page empty products - PASS");
    }
}
