// ==============================================
// GRUPP 3: SERVICE & CONTROLLER - Kör efter modellerna
// ==============================================

// FILE: src/main/java/com/ctrlbuy/webshop/service/CartService.java
package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.model.Cart;
import com.ctrlbuy.webshop.model.CartItem;
import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.repository.CartRepository;
import com.ctrlbuy.webshop.security.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    // Hämta eller skapa kundvagn för inloggad användare
    public Cart getOrCreateCartForUser(User user) {
        Optional<Cart> existingCart = cartRepository.findByUserWithItems(user);

        if (existingCart.isPresent()) {
            log.debug("Found existing cart for user: {}", user.getEmail());
            return existingCart.get();
        }

        log.debug("Creating new cart for user: {}", user.getEmail());
        Cart newCart = Cart.builder()
                .user(user)
                .build();

        return cartRepository.save(newCart);
    }

    // Hämta eller skapa kundvagn för session (icke-inloggad användare)
    public Cart getOrCreateCartForSession(String sessionId) {
        Optional<Cart> existingCart = cartRepository.findBySessionIdWithItems(sessionId);

        if (existingCart.isPresent()) {
            log.debug("Found existing cart for session: {}", sessionId);
            return existingCart.get();
        }

        log.debug("Creating new cart for session: {}", sessionId);
        Cart newCart = Cart.builder()
                .sessionId(sessionId)
                .build();

        return cartRepository.save(newCart);
    }

    // Lägg till produkt i kundvagn
    public Cart addProductToCart(Cart cart, Long productId, Integer quantity) {
        Product product = productService.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // Kontrollera om produkten redan finns i kundvagnen
        CartItem existingItem = cart.findItemByProduct(product);

        if (existingItem != null) {
            // Öka kvantiteten för befintlig produkt
            existingItem.increaseQuantity(quantity);
            log.debug("Updated quantity for product {} in cart. New quantity: {}",
                    product.getName(), existingItem.getQuantity());
        } else {
            // Skapa ny cart item
            CartItem newItem = CartItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .cart(cart)
                    .build();

            cart.addItem(newItem);
            log.debug("Added new product {} to cart with quantity: {}",
                    product.getName(), quantity);
        }

        return cartRepository.save(cart);
    }

    // Uppdatera kvantitet för en produkt i kundvagnen
    public Cart updateCartItemQuantity(Cart cart, Long productId, Integer newQuantity) {
        Product product = productService.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        CartItem item = cart.findItemByProduct(product);
        if (item == null) {
            throw new RuntimeException("Product not found in cart: " + productId);
        }

        if (newQuantity <= 0) {
            cart.removeItem(item);
            log.debug("Removed product {} from cart", product.getName());
        } else {
            item.updateQuantity(newQuantity);
            log.debug("Updated quantity for product {} to {}", product.getName(), newQuantity);
        }

        return cartRepository.save(cart);
    }

    // Ta bort produkt från kundvagn
    public Cart removeProductFromCart(Cart cart, Long productId) {
        Product product = productService.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        CartItem item = cart.findItemByProduct(product);
        if (item != null) {
            cart.removeItem(item);
            log.debug("Removed product {} from cart", product.getName());
        }

        return cartRepository.save(cart);
    }

    // Rensa hela kundvagnen
    public void clearCart(Cart cart) {
        cart.clear();
        cartRepository.save(cart);
        log.debug("Cleared cart with id: {}", cart.getId());
    }

    // Slå ihop session-cart med användar-cart vid inloggning
    public Cart mergeSessionCartWithUserCart(String sessionId, User user) {
        Optional<Cart> sessionCart = cartRepository.findBySessionIdWithItems(sessionId);

        if (sessionCart.isEmpty() || sessionCart.get().isEmpty()) {
            log.debug("No session cart to merge for user: {}", user.getEmail());
            return getOrCreateCartForUser(user);
        }

        Cart userCart = getOrCreateCartForUser(user);
        Cart sessionCartEntity = sessionCart.get();

        // Flytta över alla items från session-cart till user-cart
        for (CartItem sessionItem : sessionCartEntity.getItems()) {
            CartItem existingUserItem = userCart.findItemByProduct(sessionItem.getProduct());

            if (existingUserItem != null) {
                // Lägg ihop kvantiteterna
                existingUserItem.increaseQuantity(sessionItem.getQuantity());
            } else {
                // Skapa ny item i user-cart
                CartItem newItem = CartItem.builder()
                        .product(sessionItem.getProduct())
                        .quantity(sessionItem.getQuantity())
                        .unitPrice(sessionItem.getUnitPrice())
                        .cart(userCart)
                        .build();
                userCart.addItem(newItem);
            }
        }

        // Ta bort session-cart
        cartRepository.delete(sessionCartEntity);

        Cart savedCart = cartRepository.save(userCart);
        log.debug("Merged session cart with user cart for user: {}", user.getEmail());

        return savedCart;
    }

    // Hämta kundvagn baserat på användare eller session
    public Cart getCurrentCart(User user, String sessionId) {
        if (user != null) {
            return getOrCreateCartForUser(user);
        } else {
            return getOrCreateCartForSession(sessionId);
        }
    }

    // Räkna totalt antal items i kundvagn (för navigation badge)
    public Integer getCartItemCount(User user, String sessionId) {
        Cart cart = getCurrentCart(user, sessionId);
        return cart.getTotalItems();
    }

    // Hämta total summa för kundvagn
    public BigDecimal getCartTotal(User user, String sessionId) {
        Cart cart = getCurrentCart(user, sessionId);
        return cart.getTotalAmount();
    }
}