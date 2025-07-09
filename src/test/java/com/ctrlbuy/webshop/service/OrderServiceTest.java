package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private OrderService orderService;

    // ✅ Inject EntityManager manually since @PersistenceContext doesn't work in unit tests
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // Use reflection to inject the mock EntityManager
        try {
            java.lang.reflect.Field field = OrderService.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(orderService, entityManager);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject EntityManager", e);
        }
    }

    @Test
    void generateOrderNumber_ShouldCreateUniqueNumberWithCorrectFormat() throws Exception {
        // Arrange
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // ✅ FIXED: Proper mock chain setup
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        // Act - Use reflection to access private method
        Method method = OrderService.class.getDeclaredMethod("generateOrderNumber");
        method.setAccessible(true);
        String orderNumber = (String) method.invoke(orderService);

        // Assert
        assertNotNull(orderNumber);
        assertTrue(orderNumber.startsWith("CB" + today));
        assertTrue(orderNumber.endsWith("001"));
        assertEquals("CB" + today + "001", orderNumber);

        System.out.println("✅ Generated order number: " + orderNumber);
    }

    @Test
    void generateOrderNumber_ShouldIncrementWhenOrdersExist() throws Exception {
        // Arrange
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String existingOrder1 = "CB" + today + "001";
        String existingOrder2 = "CB" + today + "002";

        List<String> existingOrders = Arrays.asList(existingOrder1, existingOrder2);

        // ✅ FIXED: Proper mock chain setup
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(existingOrders);

        // Act
        Method method = OrderService.class.getDeclaredMethod("generateOrderNumber");
        method.setAccessible(true);
        String orderNumber = (String) method.invoke(orderService);

        // Assert
        assertNotNull(orderNumber);
        assertEquals("CB" + today + "003", orderNumber);

        System.out.println("✅ Incremented order number: " + orderNumber);
    }

    @Test
    void generateOrderNumber_ShouldHandleGapsInSequence() throws Exception {
        // Arrange
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String existingOrder1 = "CB" + today + "001";
        String existingOrder2 = "CB" + today + "005"; // Gap in sequence

        List<String> existingOrders = Arrays.asList(existingOrder1, existingOrder2);

        // ✅ FIXED: Proper mock chain setup
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(existingOrders);

        // Act
        Method method = OrderService.class.getDeclaredMethod("generateOrderNumber");
        method.setAccessible(true);
        String orderNumber = (String) method.invoke(orderService);

        // Assert
        assertNotNull(orderNumber);
        assertEquals("CB" + today + "006", orderNumber); // Should use highest + 1

        System.out.println("✅ Handled gap in sequence: " + orderNumber);
    }

    @Test
    void generateOrderNumber_ShouldHandleNonNumericSuffixes() throws Exception {
        // Arrange
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String validOrder = "CB" + today + "001";
        String invalidOrder = "CB" + today + "ABC"; // Non-numeric

        List<String> existingOrders = Arrays.asList(validOrder, invalidOrder);

        // ✅ FIXED: Proper mock chain setup
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(existingOrders);

        // Act
        Method method = OrderService.class.getDeclaredMethod("generateOrderNumber");
        method.setAccessible(true);
        String orderNumber = (String) method.invoke(orderService);

        // Assert
        assertNotNull(orderNumber);
        assertEquals("CB" + today + "002", orderNumber); // Should ignore invalid and increment valid

        System.out.println("✅ Handled non-numeric suffixes: " + orderNumber);
    }

    @Test
    void generateOrderNumber_ShouldResetSequenceForNewDay() throws Exception {
        // Arrange
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // ✅ FIXED: Proper mock chain setup - no orders for today
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        // Act
        Method method = OrderService.class.getDeclaredMethod("generateOrderNumber");
        method.setAccessible(true);
        String orderNumber = (String) method.invoke(orderService);

        // Assert
        assertNotNull(orderNumber);
        assertEquals("CB" + today + "001", orderNumber); // Should start fresh for new day

        System.out.println("✅ Reset sequence for new day: " + orderNumber);
    }

    @Test
    void generateOrderNumber_ShouldHandleLargeNumbers() throws Exception {
        // Arrange
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String existingOrder = "CB" + today + "999";

        List<String> existingOrders = Arrays.asList(existingOrder);

        // ✅ FIXED: Proper mock chain setup
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(existingOrders);

        // Act
        Method method = OrderService.class.getDeclaredMethod("generateOrderNumber");
        method.setAccessible(true);
        String orderNumber = (String) method.invoke(orderService);

        // Assert
        assertNotNull(orderNumber);
        assertEquals("CB" + today + "1000", orderNumber); // Should handle 4+ digits

        System.out.println("✅ Handled large numbers: " + orderNumber);
    }
}