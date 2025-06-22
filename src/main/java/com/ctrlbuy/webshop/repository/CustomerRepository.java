package com.ctrlbuy.webshop.repository;

import com.ctrlbuy.webshop.model.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    // Specialmetoder kan läggas till senare
}
