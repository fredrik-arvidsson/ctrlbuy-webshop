package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.entity.Order;
import com.ctrlbuy.webshop.entity.Product;
import com.ctrlbuy.webshop.repository.OrderRepository;
import com.ctrlbuy.webshop.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportsController {

    private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ReportsController(ProductRepository productRepository,
                             OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    // 🏠 HUVUDSIDA - /admin/reports
    @GetMapping("")
    public String reports(Model model) {
        logger.info("Genererar rapport huvudsida");

        try {
            List<Product> allProducts = productRepository.findAll();
            long totalProducts = allProducts.size();
            long activeProducts = allProducts.stream().filter(p -> p.getStockQuantity() != null && p.getStockQuantity() > 0).count();

            model.addAttribute("totalProducts", totalProducts);
            model.addAttribute("activeProducts", activeProducts);

            logger.debug("Huvudsida - Total: {}, Aktiva: {}", totalProducts, activeProducts);
        } catch (Exception e) {
            logger.error("Fel vid generering av rapport huvudsida", e);
            model.addAttribute("totalProducts", 0);
            model.addAttribute("activeProducts", 0);
        }

        return "admin/reports/index";
    }

    // 📊 PRODUKTRAPPORT - /admin/reports/products
    @GetMapping("/products")
    public String productReport(Model model) {
        try {
            logger.info("Genererar produktrapport");

            // Använd standard JPA findAll() - enklast och mest pålitlig
            List<Product> allProducts = productRepository.findAll();
            logger.debug("Hämtade {} produkter", allProducts.size());

            // Grundläggande statistik
            int totalProducts = allProducts.size();
            model.addAttribute("totalProducts", totalProducts);

            // Prisstatistik - MANUELL BERÄKNING
            BigDecimal maxPrice = BigDecimal.ZERO;
            BigDecimal minPrice = new BigDecimal("999999");
            BigDecimal totalPrice = BigDecimal.ZERO;
            int validPriceCount = 0;
            Product mostExpensive = null;
            Product cheapest = null;

            for (Product product : allProducts) {
                BigDecimal currentPrice = product.getCurrentPrice(); // Använd getCurrentPrice() istället för getPrice()
                if (currentPrice != null && currentPrice.compareTo(BigDecimal.ZERO) > 0) {
                    validPriceCount++;
                    totalPrice = totalPrice.add(currentPrice);

                    // Kontrollera max pris
                    if (currentPrice.compareTo(maxPrice) > 0) {
                        maxPrice = currentPrice;
                        mostExpensive = product;
                    }

                    // Kontrollera min pris
                    if (currentPrice.compareTo(minPrice) < 0) {
                        minPrice = currentPrice;
                        cheapest = product;
                    }
                }
            }

            // Säkerställ att vi inte har "999999" som min-värde
            if (minPrice.equals(new BigDecimal("999999"))) {
                minPrice = BigDecimal.ZERO;
            }

            // Beräkna genomsnitt
            BigDecimal averagePrice = BigDecimal.ZERO;
            if (validPriceCount > 0) {
                averagePrice = totalPrice.divide(new BigDecimal(validPriceCount), 2, RoundingMode.HALF_UP);
            }

            logger.debug("Prisstatistik - Max: {}, Min: {}, Genomsnitt: {}", maxPrice, minPrice, averagePrice);

            // Sätt attribut för frontend - ALLA VARIANTER
            model.addAttribute("maxPrice", maxPrice);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("highestPrice", maxPrice);  // Template-kompatibilitet
            model.addAttribute("lowestPrice", minPrice);   // Template-kompatibilitet
            model.addAttribute("averagePrice", averagePrice);
            model.addAttribute("mostExpensiveProduct", mostExpensive);
            model.addAttribute("cheapestProduct", cheapest);

            // Lågt lager beräkning
            int lowStockCount = 0;
            List<Product> lowStockProducts = new ArrayList<>();
            for (Product product : allProducts) {
                if (product.getStockQuantity() != null && product.getStockQuantity() < 10) {
                    lowStockCount++;
                    lowStockProducts.add(product);
                }
            }
            model.addAttribute("lowStockCount", lowStockCount);
            model.addAttribute("lowStockProducts", lowStockProducts);

            // Kategori-räkning
            Map<String, Long> categoryMap = new HashMap<>();
            for (Product product : allProducts) {
                String category = "Okänd";
                if (product.getCategory() != null && !product.getCategory().trim().isEmpty()) {
                    category = product.getCategory();
                }
                categoryMap.put(category, categoryMap.getOrDefault(category, 0L) + 1L);
            }

            model.addAttribute("productsByCategory", categoryMap);

            // Extra statistik för rapporter
            long inStockProducts = allProducts.stream()
                    .mapToLong(p -> p.getStockQuantity() != null && p.getStockQuantity() > 0 ? 1 : 0)
                    .sum();

            long outOfStockProducts = totalProducts - inStockProducts;

            model.addAttribute("inStockProducts", inStockProducts);
            model.addAttribute("outOfStockProducts", outOfStockProducts);

            // Alla produkter för tabellen
            model.addAttribute("products", allProducts);

            logger.info("Produktrapport genererad framgångsrikt");

        } catch (Exception e) {
            logger.error("Fel vid generering av produktrapport", e);

            // Fallback-värden
            model.addAttribute("totalProducts", 0);
            model.addAttribute("averagePrice", BigDecimal.ZERO);
            model.addAttribute("maxPrice", BigDecimal.ZERO);
            model.addAttribute("minPrice", BigDecimal.ZERO);
            model.addAttribute("highestPrice", BigDecimal.ZERO);
            model.addAttribute("lowestPrice", BigDecimal.ZERO);
            model.addAttribute("lowStockCount", 0);
            model.addAttribute("products", new ArrayList<>());
            model.addAttribute("productsByCategory", new HashMap<>());
            model.addAttribute("inStockProducts", 0L);
            model.addAttribute("outOfStockProducts", 0L);
        }

        return "admin/reports/products";
    }

    // 💰 FÖRSÄLJNINGSRAPPORT - /admin/reports/sales
    @GetMapping("/sales")
    public String salesReport(Model model) {
        try {
            logger.info("Genererar försäljningsrapport");

            List<Product> allProducts = productRepository.findAll();
            List<Order> allOrders = orderRepository.findAll();

            // Grundläggande produktstatistik
            long totalProducts = allProducts.size();
            long saleProducts = allProducts.stream().filter(Product::isOnSale).count();

            // Beräkna totala besparingar (för produkter på rea)
            BigDecimal totalSavings = allProducts.stream()
                    .filter(Product::isOnSale)
                    .map(p -> {
                        if (p.getOriginalPrice() != null && p.getCurrentPrice() != null) {
                            return p.getOriginalPrice().subtract(p.getCurrentPrice());
                        }
                        return BigDecimal.ZERO;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Beräkna total potentiell intäkt (aktuella priser * lager)
            BigDecimal totalPotentialRevenue = allProducts.stream()
                    .map(p -> {
                        if (p.getCurrentPrice() != null && p.getStockQuantity() != null) {
                            return p.getCurrentPrice().multiply(BigDecimal.valueOf(p.getStockQuantity()));
                        }
                        return BigDecimal.ZERO;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Genomsnittlig rea-rabatt som BigDecimal
            BigDecimal avgDiscount = BigDecimal.ZERO;
            try {
                if (saleProducts > 0) {
                    double tempAvg = allProducts.stream()
                            .filter(Product::isOnSale)
                            .mapToDouble(p -> p.getDiscountPercentage() != null ? p.getDiscountPercentage().doubleValue() : 0.0)
                            .average()
                            .orElse(0.0);
                    avgDiscount = BigDecimal.valueOf(tempAvg).setScale(2, RoundingMode.HALF_UP);
                }
            } catch (Exception e) {
                logger.warn("Fel vid beräkning av genomsnittlig rabatt", e);
                avgDiscount = BigDecimal.ZERO;
            }

            // Produkter per kategori
            Map<String, Long> productsByCategory = allProducts.stream()
                    .collect(Collectors.groupingBy(
                            p -> p.getCategory() != null ? p.getCategory() : "Okänd",
                            Collectors.counting()
                    ));

            // REA-produkter per kategori
            Map<String, Long> saleProductsByCategory = allProducts.stream()
                    .filter(Product::isOnSale)
                    .collect(Collectors.groupingBy(
                            p -> p.getCategory() != null ? p.getCategory() : "Okänd",
                            Collectors.counting()
                    ));

            // Lägg till i modellen
            model.addAttribute("totalProducts", totalProducts);
            model.addAttribute("saleProducts", saleProducts);
            model.addAttribute("totalSavings", totalSavings);
            model.addAttribute("totalRevenue", totalPotentialRevenue); // Total potentiell intäkt
            model.addAttribute("avgDiscount", avgDiscount);
            model.addAttribute("productsByCategory", productsByCategory);
            model.addAttribute("saleProductsByCategory", saleProductsByCategory);

            // Order-statistik
            model.addAttribute("totalOrders", allOrders.size());
            model.addAttribute("recentOrders", allOrders);

            // REA-översikt
            List<Product> activeDeals = allProducts.stream()
                    .filter(Product::isOnSale)
                    .limit(10)
                    .collect(Collectors.toList());
            model.addAttribute("activeDeals", activeDeals);

            logger.debug("Försäljningsstatistik - Produkter: {}, REA: {}, Besparingar: {} kr",
                    totalProducts, saleProducts, totalSavings);
            logger.info("Försäljningsrapport genererad framgångsrikt");

        } catch (Exception e) {
            logger.error("Fel vid generering av försäljningsrapport", e);

            // Fallback-värden
            model.addAttribute("totalProducts", 0);
            model.addAttribute("saleProducts", 0);
            model.addAttribute("totalSavings", BigDecimal.ZERO);
            model.addAttribute("totalRevenue", BigDecimal.ZERO);
            model.addAttribute("avgDiscount", BigDecimal.ZERO);
            model.addAttribute("productsByCategory", new HashMap<>());
            model.addAttribute("saleProductsByCategory", new HashMap<>());
            model.addAttribute("totalOrders", 0);
            model.addAttribute("recentOrders", new ArrayList<>());
            model.addAttribute("activeDeals", new ArrayList<>());
        }

        return "admin/reports/sales";
    }

    @GetMapping("/users")
    public String userReport(Model model) {
        logger.info("Genererar användarrapport");

        // Temporärt inaktiverad - kan implementeras senare
        model.addAttribute("totalUsers", 0);
        model.addAttribute("adminUsers", 0);
        model.addAttribute("customerUsers", 0);
        model.addAttribute("users", new ArrayList<>());

        logger.debug("Användarrapport: Temporärt inaktiverad");

        return "admin/reports/users";
    }

    /**
     * Hjälpmetod för att kontrollera produktdata (används endast för debugging)
     */
    @SuppressWarnings("unused")
    private void logProductSample(List<Product> products) {
        if (logger.isDebugEnabled()) {
            logger.debug("Produktsampling (första 3):");
            for (int i = 0; i < Math.min(3, products.size()); i++) {
                Product p = products.get(i);
                logger.debug("[{}] {} - Pris: {} kr - Lager: {} - Kategori: {} - REA: {}",
                        p.getId(), p.getName(), p.getCurrentPrice(), p.getStockQuantity(),
                        p.getCategory(), p.isOnSale());
            }
        }
    }
}