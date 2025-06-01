// FILE: src/main/java/com/ctrlbuy/webshop/repository/CartRepository.java
package com.ctrlbuy.webshop.repository;

import com.ctrlbuy.webshop.model.Cart;
import com.ctrlbuy.webshop.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Hitta kundvagn för inloggad användare
    Optional<Cart> findByUser(User user);

    // Hitta kundvagn baserat på session ID (för icke-inloggade användare)
    Optional<Cart> findBySessionId(String sessionId);

    // Hitta kundvagn med items för användare (optimerad query)
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.user = :user")
    Optional<Cart> findByUserWithItems(@Param("user") User user);

    // Hitta kundvagn med items för session ID
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.sessionId = :sessionId")
    Optional<Cart> findBySessionIdWithItems(@Param("sessionId") String sessionId);

    // Ta bort gamla session-baserade kundvagnar (cleanup)
    void deleteBySessionIdAndCreatedAtBefore(String sessionId, java.time.LocalDateTime dateTime);
}