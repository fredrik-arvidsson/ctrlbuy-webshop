package com.ctrlbuy.webshop.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ctrlbuy.webshop.repository.ProductRepository;
import com.ctrlbuy.webshop.model.Product;

@Controller
@RequestMapping("/debug")
public class DebugController {

    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private EntityManager entityManager;

    @GetMapping("/products")
    @ResponseBody
    public String debugProducts() {
        logger.debug("Debug products endpoint called");
        List<Product> products = productRepository.findAll();
        StringBuilder result = new StringBuilder();

        result.append("=== PRODUCT DEBUG ===\n");
        result.append("Number of products found: ").append(products.size()).append("\n");

        if (!products.isEmpty()) {
            result.append("\nFirst 3 products:\n");
            for (int i = 0; i < Math.min(3, products.size()); i++) {
                Product product = products.get(i);
                result.append("Product ").append(i+1).append(":\n");
                result.append("  ID: ").append(product.getId()).append("\n");
                result.append("  Name: ").append(product.getName()).append("\n");
                result.append("  Price: ").append(product.getPrice()).append("\n");
                result.append("  Category: ").append(product.getCategory()).append("\n");
                result.append("  Stock: ").append(product.getStockQuantity()).append("\n");
                result.append("  Description: ").append(
                        product.getDescription() != null ?
                                product.getDescription().substring(0, Math.min(50, product.getDescription().length())) + "..."
                                : "No description").append("\n\n");
            }
        }

        result.append("===================\n");
        logger.debug("Debug products result: {}", result.toString());
        return result.toString().replace("\n", "<br>");
    }

    @GetMapping("/repo")
    @ResponseBody
    public String debugRepo() {
        logger.debug("Debug repo endpoint called");
        try {
            long count = productRepository.count();
            List<Product> products = productRepository.findAll();

            StringBuilder result = new StringBuilder();
            result.append("<h3>REPOSITORY DEBUG</h3>");
            result.append("Repository count(): ").append(count).append("<br>");
            result.append("FindAll() size: ").append(products.size()).append("<br>");
            result.append("Connection: ").append(productRepository != null ? "OK" : "NULL").append("<br><br>");

            if (!products.isEmpty()) {
                result.append("<h4>First product:</h4>");
                Product first = products.get(0);
                result.append("ID: ").append(first.getId()).append("<br>");
                result.append("Name: ").append(first.getName()).append("<br>");
                result.append("Price: ").append(first.getPrice()).append("<br>");
                result.append("Category: ").append(first.getCategory()).append("<br>");
            }

            return result.toString();
        } catch (Exception e) {
            logger.error("Error in debug repo: ", e);
            return "<h3>ERROR</h3>Message: " + e.getMessage() +
                    "<br>Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "No cause") +
                    "<br>Stack: " + e.getClass().getSimpleName();
        }
    }

    @GetMapping("/raw-sql")
    @ResponseBody
    public String debugRawSql() {
        logger.debug("Debug raw SQL endpoint called");
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();

            StringBuilder result = new StringBuilder();
            result.append("<h3>RAW SQL DEBUG</h3>");

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM products");
            if (rs.next()) {
                result.append("Raw SQL COUNT: ").append(rs.getInt("count")).append("<br>");
            }
            rs.close();

            rs = stmt.executeQuery("SELECT id, name, price, category FROM products LIMIT 3");
            result.append("<h4>First 3 products (Raw SQL):</h4>");
            while (rs.next()) {
                result.append("ID: ").append(rs.getLong("id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Price: ").append(rs.getBigDecimal("price"))
                        .append(", Category: ").append(rs.getString("category"))
                        .append("<br>");
            }
            rs.close();

            rs = stmt.executeQuery("SELECT DATABASE() as db_name");
            if (rs.next()) {
                result.append("<br>Current database: ").append(rs.getString("db_name")).append("<br>");
            }
            rs.close();

            stmt.close();
            conn.close();

            return result.toString();
        } catch (Exception e) {
            logger.error("Error in debug raw SQL: ", e);
            return "<h3>RAW SQL ERROR</h3>Message: " + e.getMessage() +
                    "<br>Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "No cause");
        }
    }

    @GetMapping("/native")
    @ResponseBody
    public String debugNative() {
        logger.debug("Debug native endpoint called");
        try {
            Query countQuery = entityManager.createNativeQuery("SELECT COUNT(*) FROM products");
            Object countResult = countQuery.getSingleResult();

            StringBuilder result = new StringBuilder();
            result.append("<h3>NATIVE SQL DEBUG</h3>");
            result.append("Native COUNT: ").append(countResult.toString()).append("<br><br>");

            Query dataQuery = entityManager.createNativeQuery("SELECT id, name, price FROM products LIMIT 3");
            @SuppressWarnings("unchecked")
            List<Object[]> results = dataQuery.getResultList();

            result.append("<h4>First 3 products (Native SQL):</h4>");
            for (Object[] row : results) {
                result.append("ID: ").append(row[0])
                        .append(", Name: ").append(row[1])
                        .append(", Price: ").append(row[2])
                        .append("<br>");
            }

            return result.toString();
        } catch (Exception e) {
            logger.error("Error in debug native: ", e);
            return "<h3>NATIVE SQL ERROR</h3>Message: " + e.getMessage() +
                    "<br>Stack: " + e.getClass().getSimpleName();
        }
    }
}