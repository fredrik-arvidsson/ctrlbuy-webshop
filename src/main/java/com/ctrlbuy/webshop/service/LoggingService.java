
package com.ctrlbuy.webshop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);

    public void logUserAction(String username, String action, String details) {
        try {
            MDC.put("username", username);
            MDC.put("action", action);
            logger.info("User action: {} - {}", action, details);
        } finally {
            MDC.clear();
        }
    }

    public void logSystemEvent(String event, String details) {
        logger.info("System event: {} - {}", event, details);
    }

    public void logError(String operation, Exception e) {
        logger.error("Error in operation '{}': {}", operation, e.getMessage(), e);
    }

    public void logPerformance(String operation, long durationMs) {
        if (durationMs > 1000) {
            logger.warn("Slow operation detected: {} took {}ms", operation, durationMs);
        } else {
            logger.debug("Operation timing: {} took {}ms", operation, durationMs);
        }
    }
}