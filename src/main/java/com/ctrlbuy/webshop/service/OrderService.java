package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.model.Cart;
import com.ctrlbuy.webshop.model.CartItem;
import com.ctrlbuy.webshop.model.Order;
import com.ctrlbuy.webshop.model.OrderItem;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.repository.OrderRepository;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final EmailService emailService; // ✅ NYTT: Lägg till EmailService

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Skapa beställning från checkout-formulär
     */
    public Order createOrder(Cart cart, String email, String firstName, String lastName,
                             String address, String city, String postalCode, String phone,
                             String notes, String paymentMethod, Authentication auth) {

        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Kundvagnen är tom");
        }

        log.info("Skapar beställning från checkout för: {}", email);

        // Hämta användare om inloggad
        User user = null;
        if (auth != null && auth.isAuthenticated()) {
            Optional<User> userOpt = userRepository.findByUsername(auth.getName());
            user = userOpt.orElse(null);
        }

        // Beräkna totaler
        BigDecimal subtotal = cart.getTotalAmount();
        BigDecimal shipping = BigDecimal.valueOf(49.00);
        BigDecimal total = subtotal.add(shipping);

        // Generera ordernummer
        String orderNumber = generateOrderNumber();

        // Skapa beställning
        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(orderNumber);
        order.setTotalAmount(total.doubleValue());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        // Leveransadress
        order.setDeliveryName(firstName + " " + lastName);
        order.setDeliveryAddress(address);
        order.setDeliveryCity(city);
        order.setDeliveryPostalCode(postalCode);
        order.setDeliveryPhone(phone);
        order.setPaymentMethod(paymentMethod);

        // Spara beställning först
        order = orderRepository.save(order);

        // Lägg till orderitems från cart
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getUnitPrice().doubleValue());
            orderItem.setProductName(cartItem.getProduct().getName());

            order.addOrderItem(orderItem);
        }

        // Spara igen med orderitems
        order = orderRepository.save(order);

        // ✅ NYTT: Skicka orderbekräftelse via e-post
        try {
            emailService.sendOrderConfirmation(order, user != null ? user.getEmail() : email);
            log.info("Orderbekräftelse skickad till: {}", user != null ? user.getEmail() : email);
        } catch (Exception e) {
            log.error("Kunde inte skicka orderbekräftelse för order {}: {}", orderNumber, e.getMessage());
            // Logga felet men låt inte det stoppa orderprocessen
        }

        log.info("Beställning skapad med ordernummer: {}", orderNumber);
        return order;
    }

    /**
     * Skapa ny beställning från kundvagn
     */
    public Order createOrderFromCart(User user, List<com.ctrlbuy.webshop.controller.CartController.CartItem> cartItems, OrderDetails orderDetails) {

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Kundvagnen är tom");
        }

        log.info("Skapar beställning för användare: {}", user.getEmail());

        // Beräkna totaler
        BigDecimal subtotal = cartItems.stream()
                .map(com.ctrlbuy.webshop.controller.CartController.CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shipping = subtotal.compareTo(new BigDecimal("499")) >= 0
                ? BigDecimal.ZERO
                : new BigDecimal("49");
        BigDecimal total = subtotal.add(shipping);

        // Generera ordernummer
        String orderNumber = generateOrderNumber();

        // Skapa beställning
        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(orderNumber);
        order.setTotalAmount(total.doubleValue());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        // Leveransadress
        order.setDeliveryName(orderDetails.getDeliveryName());
        order.setDeliveryAddress(orderDetails.getDeliveryAddress());
        order.setDeliveryCity(orderDetails.getDeliveryCity());
        order.setDeliveryPostalCode(orderDetails.getDeliveryPostalCode());
        order.setDeliveryPhone(orderDetails.getDeliveryPhone());

        // Betalningsinformation
        order.setPaymentMethod(orderDetails.getPaymentMethod());

        // Spara beställning först
        order = orderRepository.save(order);

        // Lägg till orderitems
        for (com.ctrlbuy.webshop.controller.CartController.CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getUnitPrice().doubleValue());
            orderItem.setProductName(cartItem.getProduct().getName());

            order.addOrderItem(orderItem);
        }

        // Spara igen med orderitems
        order = orderRepository.save(order);

        // ✅ NYTT: Skicka orderbekräftelse via e-post
        try {
            emailService.sendOrderConfirmation(order, user.getEmail());
            log.info("Orderbekräftelse skickad till: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Kunde inte skicka orderbekräftelse för order {}: {}", orderNumber, e.getMessage());
            // Logga felet men låt inte det stoppa orderprocessen
        }

        log.info("Beställning skapad med ordernummer: {}", orderNumber);
        return order;
    }

    /**
     * Skapa beställning för icke-inloggad användare (gäst)
     */
    public Order createGuestOrder(List<com.ctrlbuy.webshop.controller.CartController.CartItem> cartItems, GuestOrderDetails guestDetails) {

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Kundvagnen är tom");
        }

        log.info("Skapar gästbeställning för: {}", guestDetails.getEmail());

        // Beräkna totaler
        BigDecimal subtotal = cartItems.stream()
                .map(com.ctrlbuy.webshop.controller.CartController.CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shipping = subtotal.compareTo(new BigDecimal("499")) >= 0
                ? BigDecimal.ZERO
                : new BigDecimal("49");
        BigDecimal total = subtotal.add(shipping);

        // Generera ordernummer
        String orderNumber = generateOrderNumber();

        // Skapa beställning utan User
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setTotalAmount(total.doubleValue());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        // Leveransadress från gäst
        order.setDeliveryName(guestDetails.getFirstName() + " " + guestDetails.getLastName());
        order.setDeliveryAddress(guestDetails.getAddress());
        order.setDeliveryCity(guestDetails.getCity());
        order.setDeliveryPostalCode(guestDetails.getPostalCode());
        order.setDeliveryPhone(guestDetails.getPhone());

        // Betalningsinformation
        order.setPaymentMethod(guestDetails.getPaymentMethod());

        // Spara beställning först
        order = orderRepository.save(order);

        // Lägg till orderitems
        for (com.ctrlbuy.webshop.controller.CartController.CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getUnitPrice().doubleValue());
            orderItem.setProductName(cartItem.getProduct().getName());

            order.addOrderItem(orderItem);
        }

        // Spara igen med orderitems
        order = orderRepository.save(order);

        // ✅ NYTT: Skicka orderbekräftelse via e-post för gäster
        try {
            emailService.sendOrderConfirmation(order, guestDetails.getEmail());
            log.info("Gäst-orderbekräftelse skickad till: {}", guestDetails.getEmail());
        } catch (Exception e) {
            log.error("Kunde inte skicka gäst-orderbekräftelse för order {}: {}", orderNumber, e.getMessage());
            // Logga felet men låt inte det stoppa orderprocessen
        }

        log.info("Gästbeställning skapad med ordernummer: {}", orderNumber);
        return order;
    }

    /**
     * Hitta beställning baserat på ordernummer och användare
     */
    public Order findByOrderNumberAndUser(String orderNumber, User user) {
        return orderRepository.findByOrderNumberAndUser(orderNumber, user);
    }

    /**
     * Hitta beställning endast baserat på ordernummer (för gäster)
     */
    public Order findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    /**
     * Hitta alla beställningar för en användare
     */
    public List<Order> findByUser(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    /**
     * Uppdatera orderstatus
     */
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Beställning hittades inte"));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        log.info("Orderstatus uppdaterad för beställning {}: {}", order.getOrderNumber(), status);
        return updatedOrder;
    }

    /**
     * ✅ UPPDATERAD: Generera enkelt ordernummer med löpnummer
     */
    private String generateOrderNumber() {
        // Hämta totalt antal ordrar och lägg till 1 för nästa nummer
        long nextOrderNumber = orderRepository.count() + 1;
        return String.format("CB%03d", nextOrderNumber);
    }

    /**
     * Hitta beställning baserat på ID
     */
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beställning hittades inte"));
    }

    /**
     * Hitta beställning med orderItems (för att undvika LazyInitializationException)
     */
    @Transactional(readOnly = true)
    public Order findOrderWithItemsById(Long orderId) {
        try {
            return entityManager.createQuery(
                            "SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :orderId",
                            Order.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult();
        } catch (Exception e) {
            log.error("Kunde inte hämta beställning med ID: {}", orderId, e);
            return null;
        }
    }

    /**
     * Alla beställningar (admin)
     */
    public List<Order> findAll() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    /**
     * Räkna antal beställningar för användare
     */
    public Long countOrdersByUser(User user) {
        return orderRepository.countByUser(user);
    }

    // ========================================
    // NYA METODER FÖR ORDERHISTORIK
    // ========================================

    /**
     * Hämta orders med paginering för orderhistorik
     */
    public Page<Order> getOrdersByUserWithPagination(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        return orderRepository.findByUser(user, pageable);
    }

    /**
     * Hämta specifik order för användare (säkerhetscheck)
     */
    public Optional<Order> getOrderByIdAndUser(Long orderId, User user) {
        return orderRepository.findByIdAndUser(orderId, user);
    }

    /**
     * Hämta order via ordernummer och användare (säkerhetscheck) - wrapprar befintlig metod
     */
    public Optional<Order> getOrderByOrderNumberAndUser(String orderNumber, User user) {
        Order order = findByOrderNumberAndUser(orderNumber, user);
        return Optional.ofNullable(order);
    }

    /**
     * Beräkna total summa för alla orders av användare
     */
    public Double getTotalSpentByUser(User user) {
        return orderRepository.sumTotalAmountByUser(user);
    }

    /**
     * Hämta senaste order för användare
     */
    public Optional<Order> getLatestOrderByUser(User user) {
        List<Order> orders = findByUser(user); // Använder befintlig metod
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
    }

    /**
     * Hämta orders för användare (orderhistorik - använder befintlig metod)
     */
    public List<Order> getOrdersByUser(User user) {
        return findByUser(user); // Använder din befintliga metod
    }

    // DTO klasser för beställningsdata
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class OrderDetails {
        private String deliveryName;
        private String deliveryAddress;
        private String deliveryCity;
        private String deliveryPostalCode;
        private String deliveryPhone;
        private String paymentMethod;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    public static class GuestOrderDetails {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private String city;
        private String postalCode;
        private String paymentMethod;
    }
}