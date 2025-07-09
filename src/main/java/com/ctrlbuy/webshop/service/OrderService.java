package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.entity.Cart;
import com.ctrlbuy.webshop.entity.CartItem;
import com.ctrlbuy.webshop.entity.Order;
import com.ctrlbuy.webshop.entity.OrderItem;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.repository.OrderRepository;

// ===== JPA IMPORTS =====
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

// ===== SPRING IMPORTS =====
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ===== LOGGING IMPORTS =====
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 🛡️ RAILWAY COMPATIBLE OrderService
 * KOMPLETT VERSION MED ALLA IMPORTER OCH METODER
 */
@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    // Constructor injection
    public OrderService(OrderRepository orderRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        logger.info("✅ OrderService initialiserad med Repository och UserService");
    }

    // ===== ADMIN METHODS FOR ADMIN CONTROLLER - ENTITYMANAGER VERSIONER =====

    /**
     * Get all orders (for admin) - ANVÄND ENTITYMANAGER
     */
    public List<Order> getAllOrders() {
        try {
            Query query = entityManager.createQuery(
                    "SELECT o FROM Order o ORDER BY o.orderDate DESC", Order.class);

            @SuppressWarnings("unchecked")
            List<Order> results = query.getResultList();

            logger.info("✅ Hämtade {} orders för admin", results.size());
            return results;
        } catch (Exception e) {
            logger.error("Error fetching all orders", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get orders by user ID - ANVÄND ENTITYMANAGER
     */
    public List<Order> getOrdersByUserId(Long userId) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.orderDate DESC", Order.class);
            query.setParameter("userId", userId);

            @SuppressWarnings("unchecked")
            List<Order> results = query.getResultList();

            logger.info("✅ Hämtade {} orders för user ID: {}", results.size(), userId);
            return results;
        } catch (Exception e) {
            logger.error("Error fetching orders for user: {}", userId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Get order by ID - ANVÄND ENTITYMANAGER
     */
    public Order getOrderById(Long id) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id", Order.class);
            query.setParameter("id", id);

            @SuppressWarnings("unchecked")
            List<Order> results = query.getResultList();

            if (results.isEmpty()) {
                logger.warn("Ingen order hittades med ID: {}", id);
                return null;
            }

            Order order = results.get(0);
            logger.info("✅ Order hittades: {} (ID: {})", order.getOrderNumber(), id);
            return order;
        } catch (Exception e) {
            logger.error("Error fetching order by ID: {}", id, e);
            return null;
        }
    }

    /**
     * Update order status - ENKEL VERSION
     */
    public void updateOrderStatus(Long orderId, String status) {
        try {
            Order order = entityManager.find(Order.class, orderId);
            if (order != null) {
                // Sätt status som String direkt (Order entity hanterar konvertering)
                order.setStatus(status);
                entityManager.merge(order);
                logger.info("Updated order {} status to {}", orderId, status);
            } else {
                logger.warn("Order med ID {} hittades inte", orderId);
            }
        } catch (Exception e) {
            logger.error("Error updating order status", e);
        }
    }

    /**
     * Search orders by order number and user ID - ANVÄND ENTITYMANAGER
     */
    public List<Order> searchOrdersByNumberAndUserId(String orderNumber, Long userId) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT o FROM Order o WHERE o.orderNumber LIKE :orderPattern AND o.user.id = :userId ORDER BY o.orderDate DESC",
                    Order.class);
            query.setParameter("orderPattern", "%" + orderNumber + "%");
            query.setParameter("userId", userId);

            @SuppressWarnings("unchecked")
            List<Order> results = query.getResultList();

            logger.info("✅ Hittade {} orders för sökning: {} (user {})", results.size(), orderNumber, userId);
            return results;
        } catch (Exception e) {
            logger.error("Error searching orders", e);
            return new ArrayList<>();
        }
    }

    /**
     * Calculate total spent by user - ANVÄND ENTITYMANAGER
     */
    public double getTotalSpentByUser(Long userId) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o WHERE o.user.id = :userId");
            query.setParameter("userId", userId);

            Double total = (Double) query.getSingleResult();
            if (total == null) {
                total = 0.0;
            }

            logger.info("✅ Total spenderat för user {}: {} kr", userId, total);
            return total;
        } catch (Exception e) {
            logger.error("Error calculating total spent for user: {}", userId, e);
            return 0.0;
        }
    }

    // ===== BEFINTLIGA METODER - SAKNADE METODER FÖR COMPILATION FIX =====

    /**
     * ✅ NYTT: getLatestOrderByUser - Hämta senaste order för användare (för OrderHistoryController)
     */
    public Optional<Order> getLatestOrderByUser(User user) {
        try {
            logger.info("🔍 Hämtar senaste order för användare: {}", user.getUsername());

            Query query = entityManager.createQuery(
                    "SELECT o FROM Order o WHERE o.user = :user ORDER BY o.orderDate DESC", Order.class);
            query.setParameter("user", user);
            query.setMaxResults(1); // Bara första (senaste) resultat

            @SuppressWarnings("unchecked")
            List<Order> results = query.getResultList();

            if (results.isEmpty()) {
                logger.info("ℹ️ Ingen order hittades för användare: {}", user.getUsername());
                return Optional.empty();
            }

            Order latestOrder = results.get(0);
            logger.info("✅ Senaste order hittades: {} för användare {}",
                    latestOrder.getOrderNumber(), user.getUsername());
            return Optional.of(latestOrder);

        } catch (Exception e) {
            logger.error("❌ Fel vid hämtning av senaste order för användare {}: {}",
                    user.getUsername(), e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * ✅ NYTT: getOrderByOrderNumberAndUser - Hämta order baserat på ordernummer och användare (för OrderHistoryController)
     */
    public Optional<Order> getOrderByOrderNumberAndUser(String orderNumber, User user) {
        try {
            logger.info("🔍 Hämtar order {} för användare: {}", orderNumber, user.getUsername());

            if (orderNumber == null || orderNumber.trim().isEmpty()) {
                logger.error("❌ Tomt ordernummer för användare: {}", user.getUsername());
                return Optional.empty();
            }

            Query query = entityManager.createQuery(
                    "SELECT o FROM Order o WHERE o.orderNumber = :orderNumber AND o.user = :user", Order.class);
            query.setParameter("orderNumber", orderNumber.trim());
            query.setParameter("user", user);

            @SuppressWarnings("unchecked")
            List<Order> results = query.getResultList();

            if (results.isEmpty()) {
                logger.warn("⚠️ Ingen order med nummer {} hittades för användare {}",
                        orderNumber, user.getUsername());
                return Optional.empty();
            }

            Order order = results.get(0);
            logger.info("✅ Order {} hittades för användare {}",
                    order.getOrderNumber(), user.getUsername());
            return Optional.of(order);

        } catch (Exception e) {
            logger.error("❌ Fel vid hämtning av order {} för användare {}: {}",
                    orderNumber, user.getUsername(), e.getMessage());
            return Optional.empty();
        }
    }

    // ===== ORDER CREATION METHODS =====

    /**
     * 🛒 Skapa beställning från checkout-formulär
     */
    public Order createOrder(Cart cart, String email, String firstName, String lastName,
                             String address, String city, String postalCode, String phone,
                             String notes, String paymentMethod, Authentication auth) {

        logger.info("🛒 Skapar order från checkout för: {} {}", firstName, lastName);

        try {
            if (cart == null || cart.getItems().isEmpty()) {
                logger.error("❌ Försöker skapa order med tom kundvagn");
                throw new RuntimeException("Kundvagnen är tom");
            }

            // 🛡️ SAFE USER LOOKUP - använd UserService istället för repository direkt
            User user = null;
            if (auth != null && auth.isAuthenticated()) {
                Optional<User> userOpt = userService.findByUsername(auth.getName());
                user = userOpt.orElse(null);
                if (user != null) {
                    logger.info("👤 Order skapas för inloggad användare: {}", user.getUsername());
                } else {
                    logger.warn("⚠️ Användare inte hittad för autentiserad session: {}", auth.getName());
                }
            } else {
                logger.info("👤 Order skapas för gäst");
            }

            // Beräkna totaler
            BigDecimal subtotal = cart.getTotalAmount();
            BigDecimal shipping = BigDecimal.valueOf(49.00);
            BigDecimal total = subtotal.add(shipping);

            logger.info("💰 Order totaler - Subtotal: {}, Frakt: {}, Total: {}", subtotal, shipping, total);

            // Generera ordernummer
            String orderNumber = generateOrderNumber();
            logger.info("🔢 Genererat ordernummer: {}", orderNumber);

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
            logger.info("💾 Bas-order sparad med ID: {}", order.getId());

            // Lägg till orderitems från cart
            for (CartItem cartItem : cart.getItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getUnitPrice().doubleValue());
                orderItem.setProductName(cartItem.getProduct().getName());

                order.addOrderItem(orderItem);
                logger.info("📦 Lagt till orderitem: {} x{}", cartItem.getProduct().getName(), cartItem.getQuantity());
            }

            // Spara igen med orderitems
            order = orderRepository.save(order);
            logger.info("✅ Order komplett sparad: {} med {} items", orderNumber, order.getOrderItems().size());

            return order;

        } catch (Exception e) {
            logger.error("❌ Fel vid skapande av order från checkout: {}", e.getMessage(), e);
            throw new RuntimeException("Kunde inte skapa beställning: " + e.getMessage(), e);
        }
    }

    /**
     * 🛒 Skapa ny beställning från kundvagn (för inloggade användare)
     */
    public Order createOrderFromCart(User user, List<com.ctrlbuy.webshop.controller.CartController.CartItem> cartItems, OrderDetails orderDetails) {

        logger.info("🛒 Skapar order från kundvagn för användare: {}", user.getUsername());

        try {
            if (cartItems == null || cartItems.isEmpty()) {
                logger.error("❌ Försöker skapa order med tom kundvagn för användare: {}", user.getUsername());
                throw new RuntimeException("Kundvagnen är tom");
            }

            // Beräkna totaler
            BigDecimal subtotal = cartItems.stream()
                    .map(com.ctrlbuy.webshop.controller.CartController.CartItem::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal shipping = subtotal.compareTo(new BigDecimal("499")) >= 0
                    ? BigDecimal.ZERO
                    : new BigDecimal("49");
            BigDecimal total = subtotal.add(shipping);

            logger.info("💰 User order totaler - Subtotal: {}, Frakt: {}, Total: {}", subtotal, shipping, total);

            // Generera ordernummer
            String orderNumber = generateOrderNumber();
            logger.info("🔢 Genererat ordernummer för användare: {}", orderNumber);

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
            order.setPaymentMethod(orderDetails.getPaymentMethod());

            // Spara beställning först
            order = orderRepository.save(order);
            logger.info("💾 User bas-order sparad med ID: {}", order.getId());

            // Lägg till orderitems
            for (com.ctrlbuy.webshop.controller.CartController.CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getUnitPrice().doubleValue());
                orderItem.setProductName(cartItem.getProduct().getName());

                order.addOrderItem(orderItem);
                logger.info("📦 User orderitem tillagt: {} x{}", cartItem.getProduct().getName(), cartItem.getQuantity());
            }

            // Spara igen med orderitems
            order = orderRepository.save(order);
            logger.info("✅ User order komplett: {} med {} items", orderNumber, order.getOrderItems().size());

            return order;

        } catch (Exception e) {
            logger.error("❌ Fel vid skapande av user order: {}", e.getMessage(), e);
            throw new RuntimeException("Kunde inte skapa användarens beställning: " + e.getMessage(), e);
        }
    }

    /**
     * 👤 Skapa beställning för icke-inloggad användare (gäst)
     */
    public Order createGuestOrder(List<com.ctrlbuy.webshop.controller.CartController.CartItem> cartItems, GuestOrderDetails guestDetails) {

        logger.info("👤 Skapar gäst-order för: {} {}", guestDetails.getFirstName(), guestDetails.getLastName());

        try {
            if (cartItems == null || cartItems.isEmpty()) {
                logger.error("❌ Försöker skapa gäst-order med tom kundvagn");
                throw new RuntimeException("Kundvagnen är tom");
            }

            // Beräkna totaler
            BigDecimal subtotal = cartItems.stream()
                    .map(com.ctrlbuy.webshop.controller.CartController.CartItem::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal shipping = subtotal.compareTo(new BigDecimal("499")) >= 0
                    ? BigDecimal.ZERO
                    : new BigDecimal("49");
            BigDecimal total = subtotal.add(shipping);

            logger.info("💰 Gäst order totaler - Subtotal: {}, Frakt: {}, Total: {}", subtotal, shipping, total);

            // Generera ordernummer
            String orderNumber = generateOrderNumber();
            logger.info("🔢 Genererat ordernummer för gäst: {}", orderNumber);

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
            order.setPaymentMethod(guestDetails.getPaymentMethod());

            // Spara beställning först
            order = orderRepository.save(order);
            logger.info("💾 Gäst bas-order sparad med ID: {}", order.getId());

            // Lägg till orderitems
            for (com.ctrlbuy.webshop.controller.CartController.CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getUnitPrice().doubleValue());
                orderItem.setProductName(cartItem.getProduct().getName());

                order.addOrderItem(orderItem);
                logger.info("📦 Gäst orderitem tillagt: {} x{}", cartItem.getProduct().getName(), cartItem.getQuantity());
            }

            // Spara igen med orderitems
            order = orderRepository.save(order);
            logger.info("✅ Gäst order komplett: {} med {} items", orderNumber, order.getOrderItems().size());

            return order;

        } catch (Exception e) {
            logger.error("❌ Fel vid skapande av gäst-order: {}", e.getMessage(), e);
            throw new RuntimeException("Kunde inte skapa gäst-beställning: " + e.getMessage(), e);
        }
    }

    // ===== SAFE ORDER LOOKUP METHODS =====

    /**
     * 🛡️ SAFE findByOrderNumberAndUser - använder EntityManager
     */
    public Order findByOrderNumberAndUser(String orderNumber, User user) {
        try {
            logger.info("🔍 Söker order med nummer {} för användare: {}", orderNumber, user.getUsername());

            Query query = entityManager.createQuery(
                    "SELECT o FROM Order o WHERE o.orderNumber = :orderNumber AND o.user = :user", Order.class);
            query.setParameter("orderNumber", orderNumber);
            query.setParameter("user", user);

            @SuppressWarnings("unchecked")
            List<Order> results = query.getResultList();

            if (results.isEmpty()) {
                logger.warn("⚠️ Ingen order hittades med nummer {} för användare {}", orderNumber, user.getUsername());
                return null;
            }

            Order order = results.get(0);
            logger.info("✅ Order hittades: {} för användare {}", orderNumber, user.getUsername());
            return order;

        } catch (Exception e) {
            logger.error("❌ Fel vid sökning av order {} för användare {}: {}", orderNumber, user.getUsername(), e.getMessage());
            return null;
        }
    }

    /**
     * 🛡️ SAFE findByOrderNumber - använder EntityManager
     */
    public Order findByOrderNumber(String orderNumber) {
        try {
            logger.info("🔍 Söker order med nummer: {}", orderNumber);

            Query query = entityManager.createQuery(
                    "SELECT o FROM Order o WHERE o.orderNumber = :orderNumber", Order.class);
            query.setParameter("orderNumber", orderNumber);

            @SuppressWarnings("unchecked")
            List<Order> results = query.getResultList();

            if (results.isEmpty()) {
                logger.warn("⚠️ Ingen order hittades med nummer: {}", orderNumber);
                return null;
            }

            Order order = results.get(0);
            logger.info("✅ Order hittades: {}", orderNumber);
            return order;

        } catch (Exception e) {
            logger.error("❌ Fel vid sökning av order {}: {}", orderNumber, e.getMessage());
            return null;
        }
    }

    /**
     * 🛡️ SAFE findByUser - använder EntityManager
     */
    public List<Order> findByUser(User user) {
        try {
            logger.info("🔍 Hämtar alla orders för användare: {}", user.getUsername());

            Query query = entityManager.createQuery(
                    "SELECT o FROM Order o WHERE o.user = :user ORDER BY o.orderDate DESC", Order.class);
            query.setParameter("user", user);

            @SuppressWarnings("unchecked")
            List<Order> results = query.getResultList();

            logger.info("✅ Hittade {} orders för användare: {}", results.size(), user.getUsername());
            return results;

        } catch (Exception e) {
            logger.error("❌ Fel vid hämtning av orders för användare {}: {}", user.getUsername(), e.getMessage());
            return List.of();
        }
    }

    // ===== ORDER STATUS MANAGEMENT =====

    /**
     * 🔄 Uppdatera orderstatus
     */
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        try {
            logger.info("🔄 Uppdaterar orderstatus för ID {} till: {}", orderId, status);

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Beställning hittades inte"));

            Order.OrderStatus oldStatus = order.getStatus();
            order.setStatus(status);
            Order savedOrder = orderRepository.save(order);

            logger.info("✅ Orderstatus uppdaterad: {} från {} till {}", order.getOrderNumber(), oldStatus, status);
            return savedOrder;

        } catch (Exception e) {
            logger.error("❌ Fel vid uppdatering av orderstatus för ID {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Kunde inte uppdatera orderstatus: " + e.getMessage(), e);
        }
    }

    /**
     * 🔄 Uppdatera orderstatus med notifieringar (för AdminController)
     */
    @Transactional
    public void updateOrderStatusWithNotifications(Long orderId, Order.OrderStatus newStatus) {
        try {
            logger.info("🔄 Uppdaterar orderstatus med notifieringar för ID {} till: {}", orderId, newStatus);

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

            Order.OrderStatus oldStatus = order.getStatus();
            order.setStatus(newStatus);
            orderRepository.save(order);

            logger.info("✅ Orderstatus med notifieringar uppdaterad: {} från {} till {}",
                    order.getOrderNumber(), oldStatus, newStatus);

        } catch (Exception e) {
            logger.error("❌ Fel vid uppdatering av orderstatus med notifieringar för ID {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Kunde inte uppdatera orderstatus: " + e.getMessage(), e);
        }
    }

    // ===== ORDER NUMBER GENERATION =====

    /**
     * 🔢 Generera unikt ordernummer med datum och sekvens
     * Format: CB20250707001, CB20250707002, etc.
     */
    private String generateOrderNumber() {
        try {
            // Hämta dagens datum i format YYYYMMDD
            String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            logger.info("📅 Genererar ordernummer för datum: {}", datePrefix);

            // Sök efter högsta ordernummer för idag
            String todayPattern = "CB" + datePrefix + "%";

            // 🛡️ SAFE QUERY - använd EntityManager istället för repository method
            Query query = entityManager.createQuery(
                    "SELECT o.orderNumber FROM Order o WHERE o.orderNumber LIKE :pattern");
            query.setParameter("pattern", todayPattern);

            @SuppressWarnings("unchecked")
            List<String> todaysOrders = query.getResultList();

            int nextSequence = 1;
            if (!todaysOrders.isEmpty()) {
                // Hitta högsta sekvensen för idag
                int maxSequence = 0;
                for (String orderNum : todaysOrders) {
                    if (orderNum.length() >= 13) { // CB + 8 datum + 3 sekvens = 13
                        try {
                            String sequencePart = orderNum.substring(10); // Ta sista 3 siffrorna
                            int sequence = Integer.parseInt(sequencePart);
                            maxSequence = Math.max(maxSequence, sequence);
                        } catch (NumberFormatException e) {
                            logger.warn("⚠️ Felaktigt ordernummer format: {}", orderNum);
                        }
                    }
                }
                nextSequence = maxSequence + 1;
            }

            // Generera unikt ordernummer: CB + datum + 3-siffrig sekvens
            String orderNumber = String.format("CB%s%03d", datePrefix, nextSequence);
            logger.info("🔢 Genererat ordernummer: {} (sekvens: {})", orderNumber, nextSequence);

            return orderNumber;

        } catch (Exception e) {
            logger.error("❌ Fel vid generering av ordernummer: {}", e.getMessage(), e);
            // Fallback: använd timestamp
            String fallbackNumber = "CB" + System.currentTimeMillis();
            logger.warn("⚠️ Använder fallback ordernummer: {}", fallbackNumber);
            return fallbackNumber;
        }
    }

    // ===== ORDER RETRIEVAL METHODS =====

    /**
     * 🔍 Hitta beställning baserat på ID
     */
    public Order findById(Long id) {
        try {
            logger.info("🔍 Söker order med ID: {}", id);

            return orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Beställning hittades inte"));

        } catch (Exception e) {
            logger.error("❌ Fel vid sökning av order med ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    /**
     * 🔍 Hitta beställning med orderItems (för att undvika LazyInitializationException)
     */
    @Transactional(readOnly = true)
    public Order findOrderWithItemsById(Long orderId) {
        try {
            logger.info("🔍 Söker order med items för ID: {}", orderId);

            Query query = entityManager.createQuery(
                    "SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :orderId", Order.class);
            query.setParameter("orderId", orderId);

            @SuppressWarnings("unchecked")
            List<Order> results = query.getResultList();

            if (results.isEmpty()) {
                logger.warn("⚠️ Ingen order med items hittades för ID: {}", orderId);
                return null;
            }

            Order order = results.get(0);
            logger.info("✅ Order med {} items hittades för ID: {}",
                    order.getOrderItems().size(), orderId);
            return order;

        } catch (Exception e) {
            logger.error("❌ Fel vid hämtning av order med items för ID {}: {}", orderId, e.getMessage());
            return null;
        }
    }

    /**
     * 📋 Alla beställningar (admin) - ANVÄND ENTITYMANAGER
     */
    public List<Order> findAll() {
        return getAllOrders(); // Använd vår EntityManager-version
    }

    // ===== USER-SPECIFIC ORDER METHODS =====

    /**
     * 📊 Räkna antal beställningar för användare
     */
    public Long countOrdersByUser(User user) {
        try {
            logger.info("📊 Räknar orders för användare: {}", user.getUsername());

            Query query = entityManager.createQuery(
                    "SELECT COUNT(o) FROM Order o WHERE o.user = :user");
            query.setParameter("user", user);

            Long count = (Long) query.getSingleResult();
            logger.info("📊 Användare {} har {} orders", user.getUsername(), count);
            return count;

        } catch (Exception e) {
            logger.error("❌ Fel vid räkning av orders för användare {}: {}", user.getUsername(), e.getMessage());
            return 0L;
        }
    }

    /**
     * 📄 Hämta orders med paginering för orderhistorik
     */
    public Page<Order> getOrdersByUserWithPagination(User user, int page, int size) {
        try {
            logger.info("📄 Hämtar orders med paginering för användare: {} (sida {}, storlek {})",
                    user.getUsername(), page, size);

            Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());

            // 🛡️ SAFE PAGINATION - använd findByUser och manuell paginering
            List<Order> allOrders = findByUser(user);

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allOrders.size());

            List<Order> pageContent = allOrders.subList(start, end);
            Page<Order> result = new PageImpl<>(pageContent, pageable, allOrders.size());

            logger.info("✅ Returnerar sida {} av {} orders för användare {}",
                    page, pageContent.size(), user.getUsername());
            return result;

        } catch (Exception e) {
            logger.error("❌ Fel vid hämtning av paginerade orders för användare {}: {}",
                    user.getUsername(), e.getMessage());
            return new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
        }
    }

    /**
     * 🔐 Hämta specifik order för användare (säkerhetscheck)
     */
    public Optional<Order> getOrderByIdAndUser(Long orderId, User user) {
        try {
            logger.info("🔐 Säkerhetscheck - hämtar order ID {} för användare: {}", orderId, user.getUsername());

            Query query = entityManager.createQuery(
                    "SELECT o FROM Order o WHERE o.id = :orderId AND o.user = :user", Order.class);
            query.setParameter("orderId", orderId);
            query.setParameter("user", user);

            @SuppressWarnings("unchecked")
            List<Order> results = query.getResultList();

            if (results.isEmpty()) {
                logger.warn("⚠️ Ingen order med ID {} hittades för användare {}", orderId, user.getUsername());
                return Optional.empty();
            }

            Order order = results.get(0);
            logger.info("✅ Säkerhetscheck OK - order {} tillhör användare {}",
                    order.getOrderNumber(), user.getUsername());
            return Optional.of(order);

        } catch (Exception e) {
            logger.error("❌ Fel vid säkerhetscheck av order ID {} för användare {}: {}",
                    orderId, user.getUsername(), e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 💰 Beräkna total summa för alla orders av användare
     */
    public Double getTotalSpentByUser(User user) {
        try {
            logger.info("💰 Beräknar total summa för användare: {}", user.getUsername());

            Query query = entityManager.createQuery(
                    "SELECT SUM(o.totalAmount) FROM Order o WHERE o.user = :user AND o.status != :cancelledStatus");
            query.setParameter("user", user);
            query.setParameter("cancelledStatus", Order.OrderStatus.CANCELLED);

            Double total = (Double) query.getSingleResult();
            if (total == null) {
                total = 0.0;
            }

            logger.info("💰 Användare {} har spenderat totalt: {} kr", user.getUsername(), total);
            return total;

        } catch (Exception e) {
            logger.error("❌ Fel vid beräkning av total summa för användare {}: {}",
                    user.getUsername(), e.getMessage());
            return 0.0;
        }
    }

    // ===== UTILITY METHODS =====

    /**
     * 📊 countOrdersForUser - AdminController-kompatibel
     */
    public long countOrdersForUser(User user) {
        Long count = countOrdersByUser(user);
        return count != null ? count : 0L;
    }

    /**
     * 🕐 getRecentOrdersForUser - Hämta senaste beställningar
     */
    public List<Order> getRecentOrdersForUser(User user) {
        List<Order> allOrders = findByUser(user);
        return allOrders.stream().limit(5).collect(Collectors.toList());
    }

    /**
     * ❌ cancelOrder - Avbryt beställning (endast om PENDING)
     */
    @Transactional
    public boolean cancelOrder(Long orderId, User user) {
        try {
            logger.info("❌ Försöker avbryta order ID {} för användare: {}", orderId, user.getUsername());

            Optional<Order> orderOpt = getOrderByIdAndUser(orderId, user);
            if (!orderOpt.isPresent()) {
                logger.warn("⚠️ Order ID {} tillhör inte användare {}", orderId, user.getUsername());
                return false;
            }

            Order order = orderOpt.get();

            // Kan bara avbryta väntande beställningar
            if (order.getStatus() != Order.OrderStatus.PENDING) {
                logger.warn("⚠️ Kan inte avbryta order {} med status: {}",
                        order.getOrderNumber(), order.getStatus());
                return false;
            }

            order.setStatus(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);

            logger.info("✅ Order {} avbruten för användare: {}",
                    order.getOrderNumber(), user.getUsername());

            return true;

        } catch (Exception e) {
            logger.error("❌ Fel vid avbrytning av order ID {} för användare {}: {}",
                    orderId, user.getUsername(), e.getMessage());
            return false;
        }
    }

    // ===== ADMIN METHODS =====

    /**
     * 📄 getAllOrders - Hämta alla beställningar med paginering (för admin)
     */
    public Page<Order> getAllOrders(Pageable pageable) {
        try {
            logger.info("📄 Admin - hämtar alla orders med paginering");

            List<Order> allOrders = findAll();

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allOrders.size());

            List<Order> pageContent = allOrders.subList(start, end);
            Page<Order> result = new PageImpl<>(pageContent, pageable, allOrders.size());

            logger.info("✅ Admin - returnerar {} orders av {} totalt", pageContent.size(), allOrders.size());
            return result;

        } catch (Exception e) {
            logger.error("❌ Fel vid admin-hämtning av paginerade orders: {}", e.getMessage());
            return new PageImpl<>(List.of(), pageable, 0);
        }
    }

    // ===== DTO CLASSES =====

    /**
     * 📋 OrderDetails - DTO för beställningsdata
     */
    public static class OrderDetails {
        private String deliveryName;
        private String deliveryAddress;
        private String deliveryCity;
        private String deliveryPostalCode;
        private String deliveryPhone;
        private String paymentMethod;

        // Getters
        public String getDeliveryName() { return deliveryName; }
        public String getDeliveryAddress() { return deliveryAddress; }
        public String getDeliveryCity() { return deliveryCity; }
        public String getDeliveryPostalCode() { return deliveryPostalCode; }
        public String getDeliveryPhone() { return deliveryPhone; }
        public String getPaymentMethod() { return paymentMethod; }

        // Setters
        public void setDeliveryName(String deliveryName) { this.deliveryName = deliveryName; }
        public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
        public void setDeliveryCity(String deliveryCity) { this.deliveryCity = deliveryCity; }
        public void setDeliveryPostalCode(String deliveryPostalCode) { this.deliveryPostalCode = deliveryPostalCode; }
        public void setDeliveryPhone(String deliveryPhone) { this.deliveryPhone = deliveryPhone; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }

    /**
     * 👤 GuestOrderDetails - DTO för gäst-beställningar
     */
    public static class GuestOrderDetails {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private String city;
        private String postalCode;
        private String paymentMethod;

        // Konstruktor
        public GuestOrderDetails() {}

        // Getters
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
        public String getCity() { return city; }
        public String getPostalCode() { return postalCode; }
        public String getPaymentMethod() { return paymentMethod; }

        // Setters
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public void setEmail(String email) { this.email = email; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setAddress(String address) { this.address = address; }
        public void setCity(String city) { this.city = city; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }
}