package com.ctrlbuy.webshop.config;

import com.ctrlbuy.webshop.entity.Product;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.repository.ProductRepository;
import com.ctrlbuy.webshop.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    // 🔒 SÄKRA MILJÖVARIABLER FÖR ADMIN
    @Value("${ADMIN_USERNAME:admin}")
    private String adminUsername;

    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;

    @Value("${ADMIN_EMAIL:admin@ctrlbuy.se}")
    private String adminEmail;

    @Value("${ADMIN_FIRSTNAME:Admin}")
    private String adminFirstName;

    @Value("${ADMIN_LASTNAME:Administratör}")
    private String adminLastName;

    @PostConstruct
    public void initializeData() {
        createUsers();
        createProducts();
    }

    private void createUsers() {
        logger.info("🚀 DataInitializer: SKAPAR ANVÄNDARE!");

        try {
            if (userRepository != null) {
                long userCount = userRepository.count();
                logger.info("🔍 Antal användare i databas: {}", userCount);

                if (userCount == 0) {
                    // 🔒 SÄKERHETSCHECK: Kontrollera att admin-lösenord är satt
                    if (adminPassword == null || adminPassword.trim().isEmpty()) {
                        logger.error("🚨 SÄKERHETSFEL: ADMIN_PASSWORD miljövariabel saknas!");
                        logger.error("🔒 Sätt ADMIN_PASSWORD i din .env fil");
                        throw new IllegalStateException("ADMIN_PASSWORD miljövariabel måste sättas för säkerhet!");
                    }

                    logger.info("👤 Skapar admin-användare från miljövariabler...");

                    // 🎯 ADMIN-ANVÄNDARE MED ROLE_ADMIN (FIXAD VERSION)
                    User admin = User.builder()
                            .username(adminUsername)
                            .email(adminEmail)
                            .password(passwordEncoder.encode(adminPassword))
                            .role(User.Role.ADMIN)  // ✅ ÄNDRAT: Använd enum istället för roles-lista
                            .firstName(adminFirstName)
                            .lastName(adminLastName)
                            .enabled(true)
                            .emailVerified(true)
                            .build();

                    userRepository.save(admin);
                    logger.info("✅ Skapad: Admin-användare ({}/***DOLT***) med ROLE_ADMIN", adminUsername);

                    // 👤 TEST-ANVÄNDARE (för utveckling)
                    User testUser = User.builder()
                            .username("testuser")
                            .email("test@ctrlbuy.se")
                            .password(passwordEncoder.encode("test123"))
                            .role(User.Role.USER)  // ✅ ÄNDRAT: Använd enum
                            .firstName("Test")
                            .lastName("Användare")
                            .enabled(true)
                            .emailVerified(true)
                            .build();

                    userRepository.save(testUser);
                    logger.info("✅ Skapad: Test-användare (testuser/test123)");

                    // 🛒 KUND-ANVÄNDARE
                    User customer = User.builder()
                            .username("kund")
                            .email("kund@example.com")
                            .password(passwordEncoder.encode("kund123"))
                            .role(User.Role.USER)  // ✅ ÄNDRAT: Använd enum
                            .firstName("Anna")
                            .lastName("Andersson")
                            .enabled(true)
                            .emailVerified(true)
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
                Product iphone = new Product(
                        "iPhone 15 Pro Max",
                        "Den mest avancerade iPhone hittills med titanium-design och A17 Pro-chip.",
                        new BigDecimal("14999"),
                        "Smartphones",
                        25
                );
                iphone.setImageUrl("https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400");
                iphone.setFeatured(true);
                // 🔥 REA-SETUP för iPhone
                iphone.setOnSale(true);
                iphone.setSalePrice(new BigDecimal("13499"));
                iphone.setOriginalPrice(new BigDecimal("14999"));
                iphone.setSaleDescription("Premium REA! Spara 1500 kr på iPhone 15 Pro Max!");
                iphone.setSaleStartDate(LocalDateTime.now().minusDays(1));
                iphone.setSaleEndDate(LocalDateTime.now().plusDays(30));
                productRepository.save(iphone);
                logger.info("🏷️ PÅ REA: iPhone 15 Pro Max - {}kr → {}kr",
                        iphone.getOriginalPrice(), iphone.getSalePrice());

                Product samsung = new Product(
                        "Samsung Galaxy S24 Ultra",
                        "Galaxy AI och S Pen för ultimat produktivitet. 200MP kamera och 6.8 Dynamic AMOLED 2X.",
                        new BigDecimal("13999"),
                        "Smartphones",
                        18
                );
                samsung.setImageUrl("https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400");
                samsung.setFeatured(true);
                productRepository.save(samsung);
                logger.info("✅ Skapad: Samsung Galaxy S24 Ultra");

                // 💻 LAPTOPS
                Product macbook = new Product(
                        "MacBook Pro 16 M3 Max",
                        "Extremt kraftfull för proffs. M3 Max-chip med 14-kärnig CPU och upp till 40-kärnig GPU.",
                        new BigDecimal("34999"),
                        "Laptops",
                        12
                );
                macbook.setImageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400");
                macbook.setFeatured(true);
                productRepository.save(macbook);
                logger.info("✅ Skapad: MacBook Pro M3 Max");

                Product gaming = new Product(
                        "ASUS ROG Strix Gaming Laptop",
                        "RTX 4070, AMD Ryzen 9, 32GB RAM. Perfekt för gaming och kreativt arbete.",
                        new BigDecimal("24999"),
                        "Laptops",
                        8
                );
                gaming.setImageUrl("https://images.unsplash.com/photo-1593642702821-c8da6771f0c6?w=400");
                // 🔥 REA-SETUP för Gaming Laptop
                gaming.setOnSale(true);
                gaming.setSalePrice(new BigDecimal("21999"));
                gaming.setOriginalPrice(new BigDecimal("24999"));
                gaming.setSaleDescription("Gaming REA! 3000 kr rabatt på ROG Strix!");
                gaming.setSaleStartDate(LocalDateTime.now().minusHours(12));
                gaming.setSaleEndDate(LocalDateTime.now().plusDays(14));
                productRepository.save(gaming);
                logger.info("🏷️ PÅ REA: ASUS ROG Gaming - {}kr → {}kr",
                        gaming.getOriginalPrice(), gaming.getSalePrice());

                // 🎧 AUDIO
                Product airpods = new Product(
                        "AirPods Pro (3:e gen)",
                        "Adaptiv transparens, personlig spatial audio och H2-chip för kristallklar ljudkvalitet.",
                        new BigDecimal("2799"),
                        "Audio",
                        45
                );
                airpods.setImageUrl("https://images.unsplash.com/photo-1588423771073-b8903fbb85b5?w=400");
                airpods.setFeatured(true);
                productRepository.save(airpods);
                logger.info("✅ Skapad: AirPods Pro");

                Product sony = new Product(
                        "Sony WH-1000XM5",
                        "Branschledande brusreducering och 30 timmars batteritid. Perfekt för resor och fokus.",
                        new BigDecimal("3999"),
                        "Audio",
                        22
                );
                sony.setImageUrl("https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?w=400");
                productRepository.save(sony);
                logger.info("✅ Skapad: Sony WH-1000XM5");

                // 🎮 GAMING
                Product ps5 = new Product(
                        "PlayStation 5",
                        "Nästa generations gaming med blixtrande SSD och Ray Tracing.",
                        new BigDecimal("5999"),
                        "Gaming",
                        15
                );
                ps5.setImageUrl("https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=400");
                ps5.setFeatured(true);
                productRepository.save(ps5);
                logger.info("✅ Skapad: PlayStation 5");

                Product steam = new Product(
                        "Steam Deck OLED",
                        "Handhållen PC-gaming med fantastisk OLED-skärm och Steam-biblioteket.",
                        new BigDecimal("6999"),
                        "Gaming",
                        10
                );
                steam.setImageUrl("https://images.unsplash.com/photo-1612287230202-1ff1d85d1bdf?w=400");
                productRepository.save(steam);
                logger.info("✅ Skapad: Steam Deck OLED");

                // 🥽 VR & TECH
                Product vision = new Product(
                        "Apple Vision Pro",
                        "Spatial computing som förändrar hur vi arbetar och spelar. Den första rumsliga datorn.",
                        new BigDecimal("39999"),
                        "VR",
                        5
                );
                vision.setImageUrl("https://images.unsplash.com/photo-1622979135225-d2ba269cf1ac?w=400");
                vision.setFeatured(true);
                productRepository.save(vision);
                logger.info("✅ Skapad: Apple Vision Pro");

                Product meta = new Product(
                        "Meta Quest 3",
                        "Mixed Reality med 4K+ Infinite Display och Touch Plus-kontroller.",
                        new BigDecimal("5999"),
                        "VR",
                        12
                );
                meta.setImageUrl("https://images.unsplash.com/photo-1593508512255-86ab42a8e620?w=400");
                // 🔥 REA-SETUP för Meta Quest
                meta.setOnSale(true);
                meta.setSalePrice(new BigDecimal("4999"));
                meta.setOriginalPrice(new BigDecimal("5999"));
                meta.setSaleDescription("VR REA! 1000 kr billigare på Quest 3!");
                meta.setSaleStartDate(LocalDateTime.now().minusDays(2));
                meta.setSaleEndDate(LocalDateTime.now().plusDays(21));
                productRepository.save(meta);
                logger.info("🏷️ PÅ REA: Meta Quest 3 - {}kr → {}kr",
                        meta.getOriginalPrice(), meta.getSalePrice());

                // ⌚ SMARTWATCHES
                Product watch = new Product(
                        "Apple Watch Ultra 2",
                        "Extremt hållbar för äventyr. Precision Dual-Frequency GPS och 36 timmars batteritid.",
                        new BigDecimal("9999"),
                        "Smartwatches",
                        20
                );
                watch.setImageUrl("https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=400");
                productRepository.save(watch);
                logger.info("✅ Skapad: Apple Watch Ultra 2");

                // 🖥️ MONITORS
                Product monitor = new Product(
                        "Samsung 49 Odyssey G9",
                        "Curved gaming-monitor med 240Hz och 1ms responstid. Ultimat gaming-upplevelse.",
                        new BigDecimal("19999"),
                        "Monitors",
                        6
                );
                monitor.setImageUrl("https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400");
                // 🔥 REA-SETUP för Samsung Monitor
                monitor.setOnSale(true);
                monitor.setSalePrice(new BigDecimal("16999"));
                monitor.setOriginalPrice(new BigDecimal("19999"));
                monitor.setSaleDescription("Gaming Monitor REA! 3000 kr rabatt på Odyssey G9!");
                monitor.setSaleStartDate(LocalDateTime.now().minusHours(6));
                monitor.setSaleEndDate(LocalDateTime.now().plusDays(7));
                productRepository.save(monitor);
                logger.info("🏷️ PÅ REA: Samsung Odyssey G9 - {}kr → {}kr",
                        monitor.getOriginalPrice(), monitor.getSalePrice());

                // 🎯 RÄKNA PRODUKTER OCH REA
                long finalCount = productRepository.count();
                logger.info("🎉 KLART! Skapat {} fantastiska produkter!", finalCount);

                try {
                    long totalProductCount = productRepository.count();
                    logger.info("🏷️ TOTALT ANTAL PRODUKTER: {} st", totalProductCount);

                    // Manuell filtrering för REA-produkter
                    List<Product> allProducts = productRepository.findAll();
                    long actualSaleCount = allProducts.stream()
                            .filter(p -> p.getSalePrice() != null && p.getSalePrice().compareTo(BigDecimal.ZERO) > 0)
                            .count();
                    logger.info("🏷️ PRODUKTER PÅ REA: {} st", actualSaleCount);

                    // Beräkna totala besparingar
                    BigDecimal totalSavings = BigDecimal.ZERO;
                    for (Product product : allProducts) {
                        if (product.getOriginalPrice() != null && product.getSalePrice() != null) {
                            totalSavings = totalSavings.add(product.getOriginalPrice().subtract(product.getSalePrice()));
                        }
                    }
                    logger.info("💰 Totala besparingar: {}kr", totalSavings);
                } catch (Exception e) {
                    logger.warn("⚠️ Kunde inte beräkna besparingar: {}", e.getMessage());
                }

            } else {
                logger.info("⚠️ Produkter finns redan ({}st), hoppar över", productCount);

                try {
                    long totalProductCount = productRepository.count();
                    List<Product> allProducts = productRepository.findAll();
                    long saleProductCount = allProducts.stream()
                            .filter(p -> p.getSalePrice() != null && p.getSalePrice().compareTo(BigDecimal.ZERO) > 0)
                            .count();

                    logger.info("🏷️ Befintliga produkter - Totalt: {} st, REA: {} st", totalProductCount, saleProductCount);
                } catch (Exception e) {
                    logger.warn("⚠️ Kunde inte räkna befintliga produkter: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            logger.error("🚨 FEL vid produktskapande: {}", e.getMessage(), e);
        }
    }
}