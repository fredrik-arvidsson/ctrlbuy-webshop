package com.ctrlbuy.webshop.service;

import com.ctrlbuy.webshop.model.CustomerEntity;
import com.ctrlbuy.webshop.repository.CustomerRepository;

// ===== JPA IMPORTS =====
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

// ===== SPRING IMPORTS =====
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ===== LOGGING IMPORTS =====
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 🛡️ RAILWAY COMPATIBLE CustomerService
 *
 * Enhanced CustomerService med säkra EntityManager queries för att undvika
 * Spring Data JPA query generation problem. Inkluderar förbättrad customer
 * management med proper logging och validation.
 */
@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    // Constructor injection för dependencies
    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        logger.info("✅ CustomerService initialiserad med Repository och PasswordEncoder");
    }

    // ===== BASIC CRUD OPERATIONS =====

    /**
     * 📋 findAll - Hämta alla kunder
     */
    public List<CustomerEntity> findAll() {
        try {
            logger.info("🔍 Hämtar alla kunder");
            List<CustomerEntity> customers = customerRepository.findAll();
            logger.info("✅ Hittade {} kunder totalt", customers.size());
            return customers;
        } catch (Exception e) {
            logger.error("❌ Fel vid hämtning av alla kunder: {}", e.getMessage(), e);
            return List.of(); // Returnera tom lista vid fel
        }
    }

    /**
     * 🔍 findById - Hitta kund med ID
     */
    public Optional<CustomerEntity> findById(Long id) {
        try {
            logger.info("🔍 Söker kund med ID: {}", id);

            if (id == null) {
                logger.warn("⚠️ Customer ID är null");
                return Optional.empty();
            }

            Optional<CustomerEntity> customer = customerRepository.findById(id);

            if (customer.isPresent()) {
                logger.info("✅ Kund hittad: ID {} - {}", id, customer.get().getName());
            } else {
                logger.info("⚠️ Ingen kund hittades med ID: {}", id);
            }

            return customer;

        } catch (Exception e) {
            logger.error("❌ Fel vid sökning av kund med ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 💾 save - Spara kund med förbättrad säkerhet
     */
    @Transactional
    public CustomerEntity save(CustomerEntity customer) {
        try {
            if (customer == null) {
                logger.error("❌ Försöker spara null customer");
                throw new IllegalArgumentException("Customer kan inte vara null");
            }

            logger.info("💾 Sparar kund: {}", customer.getName());

            // Validera customer data
            validateCustomer(customer);

            // Hantera lösenord om det finns
            if (customer.getPassword() != null && !customer.getPassword().isEmpty()) {
                // Kontrollera om lösenordet redan är krypterat
                if (!isPasswordEncoded(customer.getPassword())) {
                    logger.info("🔐 Krypterar lösenord för kund: {}", customer.getName());
                    customer.setPassword(passwordEncoder.encode(customer.getPassword()));
                } else {
                    logger.info("🔐 Lösenord redan krypterat för kund: {}", customer.getName());
                }
            }

            // Sätt timestamps
            if (customer.getId() == null) {
                // Ny kund
                customer.setCreatedAt(LocalDateTime.now());
                logger.info("🆕 Ny kund skapas: {}", customer.getName());
            }
            customer.setUpdatedAt(LocalDateTime.now());

            CustomerEntity savedCustomer = customerRepository.save(customer);
            logger.info("✅ Kund sparad framgångsrikt: ID {} - {}", savedCustomer.getId(), savedCustomer.getName());

            return savedCustomer;

        } catch (Exception e) {
            logger.error("❌ Fel vid sparande av kund '{}': {}",
                    customer != null ? customer.getName() : "null", e.getMessage(), e);
            throw new RuntimeException("Kunde inte spara kund: " + e.getMessage(), e);
        }
    }

    /**
     * 🗑️ deleteById - Radera kund med säkerhetsvalidering
     */
    @Transactional
    public void deleteById(Long id) {
        try {
            logger.info("🗑️ Försöker radera kund med ID: {}", id);

            if (id == null) {
                logger.error("❌ Customer ID är null för delete operation");
                throw new IllegalArgumentException("Customer ID kan inte vara null");
            }

            // Kontrollera att kunden existerar först
            Optional<CustomerEntity> customer = findById(id);
            if (customer.isEmpty()) {
                logger.warn("⚠️ Kund med ID {} existerar inte för radering", id);
                throw new RuntimeException("Kund med ID " + id + " hittades inte");
            }

            logger.info("👤 Raderar kund: {} (ID: {})", customer.get().getName(), id);

            customerRepository.deleteById(id);

            logger.info("✅ Kund raderad framgångsrikt: ID {}", id);

        } catch (Exception e) {
            logger.error("❌ Fel vid radering av kund med ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Kunde inte radera kund: " + e.getMessage(), e);
        }
    }

    // ===== SAFE CUSTOMER SEARCH METHODS =====

    /**
     * 🛡️ SAFE findByEmail - använder EntityManager för säker query
     */
    public Optional<CustomerEntity> findByEmail(String email) {
        try {
            logger.info("🔍 Söker kund med email: {}", email);

            if (email == null || email.trim().isEmpty()) {
                logger.warn("⚠️ Email är null eller tom");
                return Optional.empty();
            }

            // Använd EntityManager för säker query
            Query query = entityManager.createQuery(
                    "SELECT c FROM CustomerEntity c WHERE c.email = :email", CustomerEntity.class);
            query.setParameter("email", email.trim());

            @SuppressWarnings("unchecked")
            List<CustomerEntity> results = query.getResultList();

            if (results.isEmpty()) {
                logger.info("⚠️ Ingen kund hittades med email: {}", email);
                return Optional.empty();
            }

            CustomerEntity customer = results.get(0);
            logger.info("✅ Kund hittad med email: {} - {}", email, customer.getName());
            return Optional.of(customer);

        } catch (Exception e) {
            logger.error("❌ Fel vid sökning av kund med email '{}': {}", email, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 🛡️ SAFE findByPhone - använder EntityManager för säker query
     */
    public Optional<CustomerEntity> findByPhone(String phone) {
        try {
            logger.info("🔍 Söker kund med telefon: {}", phone);

            if (phone == null || phone.trim().isEmpty()) {
                logger.warn("⚠️ Telefonnummer är null eller tomt");
                return Optional.empty();
            }

            Query query = entityManager.createQuery(
                    "SELECT c FROM CustomerEntity c WHERE c.phone = :phone", CustomerEntity.class);
            query.setParameter("phone", phone.trim());

            @SuppressWarnings("unchecked")
            List<CustomerEntity> results = query.getResultList();

            if (results.isEmpty()) {
                logger.info("⚠️ Ingen kund hittades med telefon: {}", phone);
                return Optional.empty();
            }

            CustomerEntity customer = results.get(0);
            logger.info("✅ Kund hittad med telefon: {} - {}", phone, customer.getName());
            return Optional.of(customer);

        } catch (Exception e) {
            logger.error("❌ Fel vid sökning av kund med telefon '{}': {}", phone, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 🛡️ SAFE existsByEmail - kontrollera om email existerar
     */
    public boolean existsByEmail(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return false;
            }

            Query query = entityManager.createQuery(
                    "SELECT COUNT(c) FROM CustomerEntity c WHERE c.email = :email");
            query.setParameter("email", email.trim());

            Long count = (Long) query.getSingleResult();
            boolean exists = count > 0;

            logger.info("📊 Email '{}' existerar: {}", email, exists);
            return exists;

        } catch (Exception e) {
            logger.error("❌ Fel vid kontroll av email '{}': {}", email, e.getMessage());
            return false;
        }
    }

    // ===== ENHANCED SEARCH AND FILTERING =====

    /**
     * 🔍 searchCustomersByName - Sök kunder med namn
     */
    public List<CustomerEntity> searchCustomersByName(String name) {
        try {
            logger.info("🔍 Söker kunder med namn som innehåller: {}", name);

            if (name == null || name.trim().isEmpty()) {
                logger.warn("⚠️ Sökterm är tom, returnerar alla kunder");
                return findAll();
            }

            Query query = entityManager.createQuery(
                    "SELECT c FROM CustomerEntity c WHERE LOWER(c.name) LIKE LOWER(:name) ORDER BY c.name",
                    CustomerEntity.class);
            query.setParameter("name", "%" + name.trim() + "%");

            @SuppressWarnings("unchecked")
            List<CustomerEntity> results = query.getResultList();

            logger.info("✅ Hittade {} kunder med namn som innehåller '{}'", results.size(), name);
            return results;

        } catch (Exception e) {
            logger.error("❌ Fel vid sökning av kunder med namn '{}': {}", name, e.getMessage());
            return List.of();
        }
    }

    /**
     * 📊 getActiveCustomers - Hämta aktiva kunder
     */
    public List<CustomerEntity> getActiveCustomers() {
        try {
            logger.info("🔍 Hämtar aktiva kunder");

            Query query = entityManager.createQuery(
                    "SELECT c FROM CustomerEntity c WHERE c.active = true ORDER BY c.name",
                    CustomerEntity.class);

            @SuppressWarnings("unchecked")
            List<CustomerEntity> results = query.getResultList();

            logger.info("✅ Hittade {} aktiva kunder", results.size());
            return results;

        } catch (Exception e) {
            logger.error("❌ Fel vid hämtning av aktiva kunder: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * 📊 getCustomerStats - Hämta kundstatistik
     */
    public CustomerStats getCustomerStats() {
        try {
            logger.info("📊 Beräknar kundstatistik");

            // Total kunder
            Query totalQuery = entityManager.createQuery("SELECT COUNT(c) FROM CustomerEntity c");
            Long totalCustomers = (Long) totalQuery.getSingleResult();

            // Aktiva kunder
            Query activeQuery = entityManager.createQuery("SELECT COUNT(c) FROM CustomerEntity c WHERE c.active = true");
            Long activeCustomers = (Long) activeQuery.getSingleResult();

            CustomerStats stats = new CustomerStats(totalCustomers, activeCustomers);
            logger.info("📊 Kundstatistik: {} totalt, {} aktiva", totalCustomers, activeCustomers);

            return stats;

        } catch (Exception e) {
            logger.error("❌ Fel vid beräkning av kundstatistik: {}", e.getMessage());
            return new CustomerStats(0L, 0L);
        }
    }

    // ===== VALIDATION METHODS =====

    /**
     * 🔍 validateCustomer - Validera customer data
     */
    private void validateCustomer(CustomerEntity customer) {
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Kundnamn är obligatoriskt");
        }

        if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()) {
            if (!isValidEmail(customer.getEmail())) {
                throw new IllegalArgumentException("Ogiltig email-format: " + customer.getEmail());
            }
        }

        logger.info("✅ Customer validering lyckades för: {}", customer.getName());
    }

    /**
     * 📧 isValidEmail - Enkel email validering
     */
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    /**
     * 🔐 isPasswordEncoded - Kontrollera om lösenord är krypterat
     */
    private boolean isPasswordEncoded(String password) {
        // BCrypt lösenord börjar vanligtvis med $2a$, $2b$, $2y$ etc
        return password != null && (password.startsWith("$2a$") ||
                password.startsWith("$2b$") ||
                password.startsWith("$2y$") ||
                password.length() > 50); // Krypterade lösenord är längre
    }

    // ===== CUSTOMER ACTIVATION METHODS =====

    /**
     * 🔓 activateCustomer - Aktivera kund
     */
    @Transactional
    public boolean activateCustomer(Long customerId) {
        try {
            logger.info("🔓 Aktiverar kund med ID: {}", customerId);

            Optional<CustomerEntity> customerOpt = findById(customerId);
            if (customerOpt.isEmpty()) {
                logger.warn("⚠️ Kund med ID {} hittades inte för aktivering", customerId);
                return false;
            }

            CustomerEntity customer = customerOpt.get();
            customer.setActive(true);
            customer.setUpdatedAt(LocalDateTime.now());

            save(customer);

            logger.info("✅ Kund aktiverad: {} (ID: {})", customer.getName(), customerId);
            return true;

        } catch (Exception e) {
            logger.error("❌ Fel vid aktivering av kund {}: {}", customerId, e.getMessage());
            return false;
        }
    }

    /**
     * 🔒 deactivateCustomer - Inaktivera kund
     */
    @Transactional
    public boolean deactivateCustomer(Long customerId) {
        try {
            logger.info("🔒 Inaktiverar kund med ID: {}", customerId);

            Optional<CustomerEntity> customerOpt = findById(customerId);
            if (customerOpt.isEmpty()) {
                logger.warn("⚠️ Kund med ID {} hittades inte för inaktivering", customerId);
                return false;
            }

            CustomerEntity customer = customerOpt.get();
            customer.setActive(false);
            customer.setUpdatedAt(LocalDateTime.now());

            save(customer);

            logger.info("✅ Kund inaktiverad: {} (ID: {})", customer.getName(), customerId);
            return true;

        } catch (Exception e) {
            logger.error("❌ Fel vid inaktivering av kund {}: {}", customerId, e.getMessage());
            return false;
        }
    }

    // ===== CUSTOMER STATS CLASS =====

    /**
     * 📊 CustomerStats - Enkel statistik klass
     */
    public static class CustomerStats {
        private final long totalCustomers;
        private final long activeCustomers;
        private final long inactiveCustomers;

        public CustomerStats(long totalCustomers, long activeCustomers) {
            this.totalCustomers = totalCustomers;
            this.activeCustomers = activeCustomers;
            this.inactiveCustomers = totalCustomers - activeCustomers;
        }

        public long getTotalCustomers() { return totalCustomers; }
        public long getActiveCustomers() { return activeCustomers; }
        public long getInactiveCustomers() { return inactiveCustomers; }

        @Override
        public String toString() {
            return String.format("CustomerStats{total=%d, active=%d, inactive=%d}",
                    totalCustomers, activeCustomers, inactiveCustomers);
        }
    }
}