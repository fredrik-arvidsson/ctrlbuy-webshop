package com.ctrlbuy.webshop.security.repository;

import com.ctrlbuy.webshop.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByActiveTrue();

    boolean existsByUsernameOrEmail(String username, String email);

    @Modifying
    @Query("UPDATE User u SET u.active = ?1 WHERE u.id = ?2")
    void updateActiveStatus(boolean active, Long id);
}