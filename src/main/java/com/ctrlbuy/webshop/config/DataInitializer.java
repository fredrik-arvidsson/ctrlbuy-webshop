package com.ctrlbuy.webshop.config;

import com.ctrlbuy.webshop.model.Product;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.repository.ProductRepository;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeData() {
        createUsers();
        createProducts();
    }

    private void createUsers() {
        logger.info("🚀 DataInitializer: SKAPAR ANVÄNDARE!");

        try {
            // Kontrollera om UserRepository finns och är tillgänglig
            if (userRepository != null) {
                long userCount = userRepository.count();
                logger.info("🔍 Antal användare i databas: {}", userCount);

                if (userCount == 0) {
                    logger.info("👤 Skapar demo-användare...");

                    // Admin användare
                    User admin = User.builder()
                            .username("admin")
                            .email("admin@ctrlbuy.se")
                            .password(passwordEncoder.encode("admin123"))
                            .firstName("Admin")
                            .lastName("Administratör")
                            .roles(List.of("USER", "ADMIN"))
                            .active(true)
                            .emailVerified(true)  // Redan verifierad!
                            .build();
                    userRepository.save(admin);
                    logger.info("✅ Skapad: Admin-användare (admin/admin123)");

                    // SUPERADMIN användare - FULLA RÄTTIGHETER (SÄKER VERSION)
                    User superAdmin = User.builder()
                            .username("superadmin")
                            .email(System.getenv("SUPERADMIN_EMAIL") != null ?
                                    System.getenv("SUPERADMIN_EMAIL") : "admin@company.com")
                            .password(passwordEncoder.encode(
                                    System.getenv("SUPERADMIN_PASSWORD") != null ?
                                            System.getenv("SUPERADMIN_PASSWORD") : "ChangeMe123"))
                            .firstName(System.getenv("SUPERADMIN_FIRSTNAME") != null ?
                                    System.getenv("SUPERADMIN_FIRSTNAME") : "Super")
                            .lastName(System.getenv("SUPERADMIN_LASTNAME") != null ?
                                    System.getenv("SUPERADMIN_LASTNAME") : "Admin")
                            .roles(List.of("ROLE_USER", "ROLE_ADMIN", "ROLE_SUPERADMIN"))
                            .active(true)
                            .emailVerified(true)
                            .build();
                    userRepository.save(superAdmin);
                    logger.info("✅ Skapad: SuperAdmin-användare (säker version med miljövariabler)");

                    // Test användare
                    User testUser = User.builder()
                            .username("testuser")
                            .email("test@ctrlbuy.se")
                            .password(passwordEncoder.encode("test123"))
                            .firstName("Test")
                            .lastName("Användare")
                            .roles(List.of("USER"))
                            .active(true)
                            .emailVerified(true)  // Redan verifierad!
                            .build();
                    userRepository.save(testUser);
                    logger.info("✅ Skapad: Test-användare (testuser/test123)");

                    // Kund-användare
                    User customer = User.builder()
                            .username("kund")
                            .email("kund@example.com")
                            .password(passwordEncoder.encode("kund123"))
                            .firstName("Anna")
                            .lastName("Andersson")
                            .roles(List.of("USER"))
                            .active(true)
                            .emailVerified(true)  // Redan verifierad!
                            .build();
                    userRepository.save(customer);
                    logger.info("✅ Skapad: Kund-användare (kund/kund123)");

                    long finalUserCount = userRepository.count();
                    logger.info("🎉 KLART! Skapat {} användare!", finalUserCount);
                } else {
                    logger.info("⚠️ Användare finns redan ({}st), hoppar över", userCount);
                }
            } else {
                logger.warn("⚠️ UserRepository inte tillgängligt, hoppar över användarskapandet");
            }

        } catch (Exception e) {
            logger.error("🚨 FEL vid användarskapande: {}", e.getMessage(), e);
            // Fortsätt ändå - produkter är viktigare
        }
    }

    private void createProducts() {
        logger.info("🚀 DataInitializer: SKAPAR PRODUKTER MED @PostConstruct!");

        try {
            long productCount = productRepository.count();
            logger.info("🔍 Antal produkter i databas: {}", productCount);

            if (productCount == 0) {
                logger.info("📦 Skapar demo-produkter...");

                // 📱 SMARTPHONES
                Product iphone = new Product("iPhone 15 Pro Max", "Smartphones", new BigDecimal("14999"), 25, "Den mest avancerade iPhone hittills med titanium-design och A17 Pro-chip.");
                iphone.setImageUrl("https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400");
                iphone.setFeatured(true);
                iphone.setSalePrice(new BigDecimal("13499"));
                productRepository.save(iphone);
                logger.info("✅ Skapad: iPhone 15 Pro Max");

                Product samsung = new Product("Samsung Galaxy S24 Ultra", "Smartphones", new BigDecimal("13999"), 18, "Galaxy AI och S Pen för ultimat produktivitet. 200MP kamera och 6.8 Dynamic AMOLED 2X.");
                samsung.setImageUrl("https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400");
                samsung.setFeatured(true);
                productRepository.save(samsung);
                logger.info("✅ Skapad: Samsung Galaxy S24 Ultra");

                // 💻 LAPTOPS
                Product macbook = new Product("MacBook Pro 16 M3 Max", "Laptops", new BigDecimal("34999"), 12, "Extremt kraftfull för proffs. M3 Max-chip med 14-kärnig CPU och upp till 40-kärnig GPU.");
                macbook.setImageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400");
                macbook.setFeatured(true);
                productRepository.save(macbook);
                logger.info("✅ Skapad: MacBook Pro M3 Max");

                Product gaming = new Product("ASUS ROG Strix Gaming Laptop", "Laptops", new BigDecimal("24999"), 8, "RTX 4070, AMD Ryzen 9, 32GB RAM. Perfekt för gaming och kreativt arbete.");
                gaming.setImageUrl("https://images.unsplash.com/photo-1593642702821-c8da6771f0c6?w=400");
                gaming.setSalePrice(new BigDecimal("21999"));
                productRepository.save(gaming);
                logger.info("✅ Skapad: ASUS ROG Strix Gaming Laptop");

                // 🎧 AUDIO
                Product airpods = new Product("AirPods Pro (3:e gen)", "Audio", new BigDecimal("2799"), 45, "Adaptiv transparens, personlig spatial audio och H2-chip för kristallklar ljudkvalitet.");
                airpods.setImageUrl("https://images.unsplash.com/photo-1588423771073-b8903fbb85b5?w=400");
                airpods.setFeatured(true);
                productRepository.save(airpods);
                logger.info("✅ Skapad: AirPods Pro");

                Product sony = new Product("Sony WH-1000XM5", "Audio", new BigDecimal("3999"), 22, "Branschledande brusreducering och 30 timmars batteritid. Perfekt för resor och fokus.");
                sony.setImageUrl("https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?w=400");
                productRepository.save(sony);
                logger.info("✅ Skapad: Sony WH-1000XM5");

                // 🎮 GAMING
                Product ps5 = new Product("PlayStation 5", "Gaming", new BigDecimal("5999"), 15, "Nästa generations gaming med blixtrande SSD och Ray Tracing.");
                ps5.setImageUrl("https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=400");
                ps5.setFeatured(true);
                productRepository.save(ps5);
                logger.info("✅ Skapad: PlayStation 5");

                Product steam = new Product("Steam Deck OLED", "Gaming", new BigDecimal("6999"), 10, "Handhållen PC-gaming med fantastisk OLED-skärm och Steam-biblioteket.");
                steam.setImageUrl("https://images.unsplash.com/photo-1612287230202-1ff1d85d1bdf?w=400");
                productRepository.save(steam);
                logger.info("✅ Skapad: Steam Deck OLED");

                // 🥽 VR & TECH
                Product vision = new Product("Apple Vision Pro", "VR", new BigDecimal("39999"), 5, "Spatial computing som förändrar hur vi arbetar och spelar. Den första rumsliga datorn.");
                vision.setImageUrl("https://images.unsplash.com/photo-1622979135225-d2ba269cf1ac?w=400");
                vision.setFeatured(true);
                productRepository.save(vision);
                logger.info("✅ Skapad: Apple Vision Pro");

                Product meta = new Product("Meta Quest 3", "VR", new BigDecimal("5999"), 12, "Mixed Reality med 4K+ Infinite Display och Touch Plus-kontroller.");
                meta.setImageUrl("https://images.unsplash.com/photo-1593508512255-86ab42a8e620?w=400");
                meta.setSalePrice(new BigDecimal("4999"));
                productRepository.save(meta);
                logger.info("✅ Skapad: Meta Quest 3");

                // ⌚ SMARTWATCHES
                Product watch = new Product("Apple Watch Ultra 2", "Smartwatches", new BigDecimal("9999"), 20, "Extremt hållbar för äventyr. Precision Dual-Frequency GPS och 36 timmars batteritid.");
                watch.setImageUrl("https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=400");
                productRepository.save(watch);
                logger.info("✅ Skapad: Apple Watch Ultra 2");

                // 🖥️ MONITORS
                Product monitor = new Product("Samsung 49 Odyssey G9", "Monitors", new BigDecimal("19999"), 6, "Curved gaming-monitor med 240Hz och 1ms responstid. Ultimat gaming-upplevelse.");
                monitor.setImageUrl("https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400");
                monitor.setSalePrice(new BigDecimal("16999"));
                productRepository.save(monitor);
                logger.info("✅ Skapad: Samsung Odyssey G9");

                long finalCount = productRepository.count();
                logger.info("🎉 KLART! Skapat {} fantastiska produkter!", finalCount);
            } else {
                logger.info("⚠️ Produkter finns redan ({}st), hoppar över", productCount);
            }

        } catch (Exception e) {
            logger.error("🚨 FEL vid produktskapande: {}", e.getMessage(), e);
        }
    }
}