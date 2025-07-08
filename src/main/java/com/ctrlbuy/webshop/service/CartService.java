package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.entity.Cart;
import com.ctrlbuy.webshop.entity.CartItem;
import com.ctrlbuy.webshop.entity.Product;
import com.ctrlbuy.webshop.repository.CartRepository;
import com.ctrlbuy.webshop.repository.ProductRepository;
import com.ctrlbuy.webshop.security.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public Cart getOrCreateCart(User user) {
        if (user == null) {
            return new Cart();
        }

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart(user);
                    return cartRepository.save(newCart);
                });
    }

    public Cart getOrCreateSessionCart(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return new Cart();
        }

        return cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    Cart newCart = new Cart(sessionId);
                    return cartRepository.save(newCart);
                });
    }

    public Cart addProductToCart(Cart cart, Long productId, Integer quantity) {
        if (cart == null || productId == null || quantity == null || quantity <= 0) {
            return cart;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        // Check if item already exists in cart
        CartItem existingItem = cart.findItemByProduct(product);

        if (existingItem != null) {
            // Update quantity of existing item
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.calculateSubtotal();
        } else {
            // Create new cart item
            CartItem newItem = new CartItem(cart, product, quantity);
            cart.addItem(newItem);
        }

        cart.updateTotalPrice();
        return cartRepository.save(cart);
    }

    public Cart updateCartItemQuantity(Cart cart, Long productId, Integer newQuantity) {
        if (cart == null || productId == null || newQuantity == null) {
            return cart;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        CartItem item = cart.findItemByProduct(product);
        if (item != null) {
            if (newQuantity <= 0) {
                // Remove item if quantity is 0 or negative
                cart.removeItem(item);
            } else {
                // Update quantity
                item.setQuantity(newQuantity);
                item.calculateSubtotal();
            }

            cart.updateTotalPrice();
            return cartRepository.save(cart);
        }

        return cart;
    }

    public Cart removeProductFromCart(Cart cart, Long productId) {
        if (cart == null || productId == null) {
            return cart;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        CartItem item = cart.findItemByProduct(product);
        if (item != null) {
            cart.removeItem(item);
            cart.updateTotalPrice();
            return cartRepository.save(cart);
        }

        return cart;
    }

    public void clearCart(Cart cart) {
        if (cart != null && !cart.isEmpty()) {
            // Clear the cart - JPA will handle cascade delete
            cart.clear();
            cart.updateTotalPrice();
            cartRepository.save(cart);
        }
    }

    public BigDecimal getCartTotal(User user, String sessionId) {
        Cart cart;
        if (user != null) {
            cart = cartRepository.findByUser(user).orElse(null);
        } else {
            cart = cartRepository.findBySessionId(sessionId).orElse(null);
        }

        return cart != null ? cart.getTotalPrice() : BigDecimal.ZERO;
    }

    public int getCartItemCount(User user, String sessionId) {
        Cart cart;
        if (user != null) {
            cart = cartRepository.findByUser(user).orElse(null);
        } else {
            cart = cartRepository.findBySessionId(sessionId).orElse(null);
        }

        return cart != null ? cart.getTotalItems() : 0;
    }

    public Cart mergeSessionCartWithUserCart(String sessionId, User user) {
        if (sessionId == null || user == null) {
            return getOrCreateCart(user);
        }

        Optional<Cart> sessionCartOpt = cartRepository.findBySessionId(sessionId);
        if (sessionCartOpt.isEmpty()) {
            return getOrCreateCart(user);
        }

        Cart sessionCart = sessionCartOpt.get();
        Cart userCart = getOrCreateCart(user);

        // Merge items from session cart to user cart
        for (CartItem sessionItem : sessionCart.getItems()) {
            CartItem existingUserItem = userCart.findItemByProduct(sessionItem.getProduct());

            if (existingUserItem != null) {
                // Merge quantities
                existingUserItem.setQuantity(existingUserItem.getQuantity() + sessionItem.getQuantity());
                existingUserItem.calculateSubtotal();
            } else {
                // Move item to user cart
                sessionItem.setCart(userCart);
                userCart.addItem(sessionItem);
            }
        }

        // Delete session cart
        cartRepository.delete(sessionCart);

        // Update and save user cart
        userCart.updateTotalPrice();
        return cartRepository.save(userCart);
    }

    @Transactional
    public void cleanupOldCarts() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        cartRepository.deleteBySessionIdAndCreatedAtBefore(null, cutoffDate);
    }

    public List<Cart> findCartsByUser(User user) {
        return cartRepository.findAllByUser(user);
    }

    public void deleteCart(Cart cart) {
        if (cart != null) {
            cartRepository.delete(cart);
        }
    }
}