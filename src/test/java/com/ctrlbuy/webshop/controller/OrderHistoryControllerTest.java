package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.service.OrderService;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Order History Controller Unit Tests")
class OrderHistoryControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private OrderHistoryController orderHistoryController;

    private Order testOrder;
    private List<Order> testOrders;
    private Page<Order> testOrderPage;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Setup test data
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNumber("ORDER-123");
        testOrder.setTotalAmount(299.99);

        testOrders = new ArrayList<>();
        testOrders.add(testOrder);

        testOrderPage = new PageImpl<>(testOrders);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        reset(model, orderService, authentication, userRepository);

        // Setup authentication mock
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(testUser));

        System.out.println("📦 OrderHistoryController test setup complete");
    }

    @Test
    @DisplayName("View order history - Should return orders view with paginated orders")
    void testViewOrderHistory() {
        // Arrange
        int page = 0;
        int size = 10;
        when(orderService.getOrdersByUserWithPagination(any(), eq(page), eq(size))).thenReturn(testOrderPage);

        // Act
        String result = orderHistoryController.viewOrderHistory(page, size, authentication, model);

        // Assert
        assertEquals("order-history", result);
        verify(model).addAttribute("orderPage", testOrderPage);
        verify(model).addAttribute("currentPage", page);
        verify(model).addAttribute("totalPages", testOrderPage.getTotalPages());

        System.out.println("✅ View order history - PASS");
    }

    @Test
    @DisplayName("View order history - With custom page and size")
    void testViewOrderHistoryCustomPageSize() {
        // Arrange
        int page = 2;
        int size = 5;
        when(orderService.getOrdersByUserWithPagination(any(), eq(page), eq(size))).thenReturn(testOrderPage);

        // Act
        String result = orderHistoryController.viewOrderHistory(page, size, authentication, model);

        // Assert
        assertEquals("order-history", result);
        verify(model).addAttribute("currentPage", page);

        System.out.println("✅ View order history custom page/size - PASS");
    }

    @Test
    @DisplayName("View order details - Valid order should return order details view")
    void testViewOrderDetailsValid() {
        // Arrange
        Long orderId = 1L;
        when(orderService.getOrderByIdAndUser(eq(orderId), any())).thenReturn(Optional.of(testOrder));

        // Act
        String result = orderHistoryController.viewOrderDetails(orderId, authentication, model, redirectAttributes);

        // Assert
        assertEquals("order-details", result);
        verify(model).addAttribute("order", testOrder);

        System.out.println("✅ View order details valid - PASS");
    }

    @Test
    @DisplayName("View order details - Invalid order should redirect to order history")
    void testViewOrderDetailsInvalid() {
        // Arrange
        Long orderId = 999L;
        when(orderService.getOrderByIdAndUser(eq(orderId), any())).thenReturn(Optional.empty());

        // Act
        String result = orderHistoryController.viewOrderDetails(orderId, authentication, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/orders", result);
        verify(redirectAttributes).addFlashAttribute("error", "Order inte hittad eller tillhör inte dig.");

        System.out.println("✅ View order details invalid - PASS");
    }

    @Test
    @DisplayName("Search order - Valid order number should return order details")
    void testSearchOrderValid() {
        // Arrange
        String orderNumber = "ORDER-123";
        when(orderService.getOrderByOrderNumberAndUser(eq(orderNumber), any())).thenReturn(Optional.of(testOrder));

        // Act
        String result = orderHistoryController.searchOrder(orderNumber, authentication, redirectAttributes);

        // Assert
        assertEquals("redirect:/orders/1", result);

        System.out.println("✅ Search order valid - PASS");
    }

    @Test
    @DisplayName("Search order - Invalid order number should redirect with error")
    void testSearchOrderInvalid() {
        // Arrange
        String orderNumber = "INVALID-ORDER";
        when(orderService.getOrderByOrderNumberAndUser(eq(orderNumber), any())).thenReturn(Optional.empty());

        // Act
        String result = orderHistoryController.searchOrder(orderNumber, authentication, redirectAttributes);

        // Assert
        assertEquals("redirect:/orders", result);
        verify(redirectAttributes).addFlashAttribute("error", "Order med nummer INVALID-ORDER inte hittad.");

        System.out.println("✅ Search order invalid - PASS");
    }

    @Test
    @DisplayName("Search order - Empty order number should redirect with error")
    void testSearchOrderEmpty() {
        // Arrange
        String orderNumber = "";

        // Act
        String result = orderHistoryController.searchOrder(orderNumber, authentication, redirectAttributes);

        // Assert
        assertEquals("redirect:/orders", result);
        verify(redirectAttributes).addFlashAttribute("error", "Order med nummer  inte hittad.");

        System.out.println("✅ Search order empty - PASS");
    }

    @Test
    @DisplayName("View order history Swedish - Should return Swedish orders view")
    void testViewOrderHistorySwedish() {
        // Arrange
        int page = 0;
        int size = 10;
        when(orderService.getOrdersByUserWithPagination(any(), eq(page), eq(size))).thenReturn(testOrderPage);

        // Act
        String result = orderHistoryController.viewOrderHistorySwedish(page, size, authentication, model);

        // Assert
        assertEquals("order-history", result);
        verify(model).addAttribute("orderPage", testOrderPage);
        verify(model).addAttribute("currentPage", page);
        verify(model).addAttribute("totalPages", testOrderPage.getTotalPages());

        System.out.println("✅ View order history Swedish - PASS");
    }

    @Test
    @DisplayName("View order details Swedish - Valid order should return Swedish order details")
    void testViewOrderDetailsSwedishValid() {
        // Arrange
        Long orderId = 1L;
        when(orderService.getOrderByIdAndUser(eq(orderId), any())).thenReturn(Optional.of(testOrder));

        // Act
        String result = orderHistoryController.viewOrderDetailsSwedish(orderId, authentication, model, redirectAttributes);

        // Assert
        assertEquals("order-details", result);
        verify(model).addAttribute("order", testOrder);

        System.out.println("✅ View order details Swedish valid - PASS");
    }

    @Test
    @DisplayName("View order details Swedish - Invalid order should redirect to Swedish orders")
    void testViewOrderDetailsSwedishInvalid() {
        // Arrange
        Long orderId = 999L;
        when(orderService.getOrderByIdAndUser(eq(orderId), any())).thenReturn(Optional.empty());

        // Act
        String result = orderHistoryController.viewOrderDetailsSwedish(orderId, authentication, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/orders", result);
        verify(redirectAttributes).addFlashAttribute("error", "Order inte hittad eller tillhör inte dig.");

        System.out.println("✅ View order details Swedish invalid - PASS");
    }

    @Test
    @DisplayName("View order history - With service exception should handle gracefully")
    void testViewOrderHistoryException() {
        // Arrange
        when(orderService.getOrdersByUserWithPagination(any(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderHistoryController.viewOrderHistory(0, 10, authentication, model);
        });

        System.out.println("✅ View order history exception - PASS");
    }
}