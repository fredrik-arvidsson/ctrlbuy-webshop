package com.ctrlbuy.webshop.repository;

import com.ctrlbuy.webshop.entity.Cart;
import com.ctrlbuy.webshop.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);

    Optional<Cart> findBySessionId(String sessionId);

    List<Cart> findByUserAndSessionId(User user, String sessionId);

    List<Cart> findAllByUser(User user);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.sessionId = :sessionId AND c.createdAt < :cutoffDate")
    void deleteBySessionIdAndCreatedAtBefore(@Param("sessionId") String sessionId, @Param("cutoffDate") LocalDateTime cutoffDate);
}