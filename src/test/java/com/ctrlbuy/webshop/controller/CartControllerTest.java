package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.controller.CartController.CartItem;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CartControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CartController cartController;

    private Product testProduct;
    private List<CartItem> mockCartItems;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("199.99"));
        testProduct.setStockQuantity(10);

        mockCartItems = new ArrayList<>();
        mockCartItems.add(new CartItem(testProduct, 2));

        reset(productService, session, model, redirectAttributes, authentication);
    }

    @Test
    void viewCart_ShouldDelegateToViewCartSwedish() {
        // Arrange
        when(session.getAttribute("shopping_cart")).thenReturn(mockCartItems);

        // Act
        String result = cartController.viewCart(session, model, authentication);

        // Assert
        assertEquals("cart/view", result);
        verify(model).addAttribute(eq("cartItems"), any());
    }

    @Test
    void viewCartSwedish_WithItemsInCart_ShouldCalculateCorrectTotals() {
        // Arrange
        when(session.getAttribute("shopping_cart")).thenReturn(mockCartItems);
        when(authentication.getName()).thenReturn("testuser");

        // Act
        String result = cartController.viewCartSwedish(session, model, authentication);

        // Assert
        assertEquals("cart/view", result);
        verify(model).addAttribute("cartItems", mockCartItems);
        verify(model).addAttribute(eq("subtotal"), any(BigDecimal.class));
        verify(model).addAttribute(eq("shipping"), any(BigDecimal.class));
        verify(model).addAttribute(eq("total"), any(BigDecimal.class));
        verify(model).addAttribute(eq("cartItemCount"), eq(2));
    }

    @Test
    void viewCartSwedish_WithEmptyCart_ShouldSetZeroValues() {
        // Arrange
        when(session.getAttribute("shopping_cart")).thenReturn(null);

        // Act
        String result = cartController.viewCartSwedish(session, model, authentication);

        // Assert
        assertEquals("cart/view", result);
        verify(model).addAttribute(eq("cartItems"), any(List.class));
        verify(model).addAttribute("subtotal", BigDecimal.ZERO);
        verify(model).addAttribute("shipping", new BigDecimal("49"));
        verify(model).addAttribute("total", new BigDecimal("49"));
        verify(model).addAttribute("cartItemCount", 0);
    }

    @Test
    void addToCart_WithValidProduct_ShouldAddToCartAndRedirect() {
        // Arrange
        when(productService.getProductByIdWithoutView(1L)).thenReturn(Optional.of(testProduct));
        when(session.getAttribute("shopping_cart")).thenReturn(new ArrayList<>());

        // Act
        String result = cartController.addToCart(1L, 1, session, redirectAttributes);

        // Assert
        assertEquals("redirect:/produkter", result);
        verify(session).setAttribute(eq("shopping_cart"), any(List.class));
        verify(redirectAttributes).addFlashAttribute("successMessage", "Test Product har lagts till i kundvagnen");
    }

    @Test
    void addToCart_WithNonExistentProduct_ShouldReturnError() {
        // Arrange
        when(productService.getProductByIdWithoutView(999L)).thenReturn(Optional.empty());

        // Act
        String result = cartController.addToCart(999L, 1, session, redirectAttributes);

        // Assert
        assertEquals("redirect:/varukorg", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Produkten hittades inte");
        verify(session, never()).setAttribute(eq("shopping_cart"), any());
    }

    @Test
    void addToCart_WithInsufficientStock_ShouldReturnError() {
        // Arrange
        testProduct.setStockQuantity(2);
        when(productService.getProductByIdWithoutView(1L)).thenReturn(Optional.of(testProduct));

        // Act
        String result = cartController.addToCart(1L, 5, session, redirectAttributes);

        // Assert
        assertEquals("redirect:/produkter", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Otillräckligt lager. Endast 2 tillgängliga");
    }

    @Test
    void updateQuantity_WithValidQuantity_ShouldUpdateAndRedirect() {
        // Arrange
        when(productService.getProductByIdWithoutView(1L)).thenReturn(Optional.of(testProduct));
        when(session.getAttribute("shopping_cart")).thenReturn(mockCartItems);

        // Act
        String result = cartController.updateQuantity(1L, 3, session, redirectAttributes);

        // Assert
        assertEquals("redirect:/varukorg", result);
        verify(session).setAttribute(eq("shopping_cart"), any(List.class));
        verify(redirectAttributes).addFlashAttribute("successMessage", "Kvantiteten har uppdaterats");
    }

    @Test
    void removeFromCart_WithExistingItem_ShouldRemoveAndRedirect() {
        // Arrange
        when(session.getAttribute("shopping_cart")).thenReturn(mockCartItems);

        // Act
        String result = cartController.removeFromCart(1L, session, redirectAttributes);

        // Assert
        assertEquals("redirect:/varukorg", result);
        verify(session).setAttribute(eq("shopping_cart"), any(List.class));
        verify(redirectAttributes).addFlashAttribute("successMessage", "Produkten har tagits bort från kundvagnen");
    }

    @Test
    void clearCart_ShouldRemoveSessionAttributeAndRedirect() {
        // Act
        String result = cartController.clearCart(session, redirectAttributes);

        // Assert
        assertEquals("redirect:/varukorg", result);
        verify(session).removeAttribute("shopping_cart");
        verify(redirectAttributes).addFlashAttribute("successMessage", "Kundvagnen har rensats");
    }

    @Test
    void addToCartAjax_WithValidProduct_ShouldReturnSuccessResponse() {
        // Arrange
        when(productService.getProductByIdWithoutView(1L)).thenReturn(Optional.of(testProduct));
        when(session.getAttribute("shopping_cart")).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<Map<String, Object>> response = cartController.addToCartAjax(1L, 1, session);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue((Boolean) response.getBody().get("success"));
        assertEquals("Produkten har lagts till i kundvagnen", response.getBody().get("message"));
        assertNotNull(response.getBody().get("cartCount"));
        assertNotNull(response.getBody().get("cartTotal"));
    }

    @Test
    void getCartCount_WithItemsInCart_ShouldReturnCorrectCount() {
        // Arrange
        when(session.getAttribute("shopping_cart")).thenReturn(mockCartItems);

        // Act
        ResponseEntity<Map<String, Object>> response = cartController.getCartCount(session);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().get("count"));
    }

    @Test
    void cartItem_ShouldCalculatePricesCorrectly() {
        // Arrange
        Product product = new Product();
        product.setPrice(new BigDecimal("100.00"));

        // Act
        CartItem cartItem = new CartItem(product, 3);

        // Assert
        assertEquals(product, cartItem.getProduct());
        assertEquals(3, cartItem.getQuantity());
        assertEquals(new BigDecimal("100.00"), cartItem.getUnitPrice());
        assertEquals(new BigDecimal("300.00"), cartItem.getTotalPrice());
    }
}
