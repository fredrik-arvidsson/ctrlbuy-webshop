package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.repository.OrderRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void generateOrderNumber_ShouldCreateUniqueNumberWithCorrectFormat() throws Exception {
        // Arrange
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        when(orderRepository.findOrderNumbersByPattern(anyString())).thenReturn(Collections.emptyList());

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

        when(orderRepository.findOrderNumbersByPattern("CB" + today + "%"))
                .thenReturn(Arrays.asList(existingOrder1, existingOrder2));

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

        when(orderRepository.findOrderNumbersByPattern("CB" + today + "%"))
                .thenReturn(Arrays.asList(existingOrder1, existingOrder2));

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

        when(orderRepository.findOrderNumbersByPattern("CB" + today + "%"))
                .thenReturn(Arrays.asList(validOrder, invalidOrder));

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

        // Mock: no orders for today
        when(orderRepository.findOrderNumbersByPattern("CB" + today + "%"))
                .thenReturn(Collections.emptyList());

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

        when(orderRepository.findOrderNumbersByPattern("CB" + today + "%"))
                .thenReturn(Arrays.asList(existingOrder));

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