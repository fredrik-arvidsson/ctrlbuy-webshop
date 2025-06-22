package com.ctrlbuy.webshop.service.impl;

import com.ctrlbuy.webshop.model.Order;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendVerificationEmail(User user, String token) {
        sendVerificationEmail(user.getEmail(), token, user.getFirstName());
    }

    @Override
    public void sendVerificationEmail(String email, String token, String firstName) {
        String verificationUrl = "http://localhost:8080/verify?token=" + token;

        logger.info("📧 =========================");
        logger.info("📧 EMAIL-VERIFIERING");
        logger.info("📧 =========================");
        logger.info("📧 Till: {} ({})", email, firstName);
        logger.info("📧 Verifierings-länk: {}", verificationUrl);
        logger.info("📧 =========================");

        logger.info("✅ Verifieringsmail simulerat skickat till: {}", email);
    }

    @Override
    public boolean sendVerificationEmail(String email, String token) {
        try {
            sendVerificationEmail(email, token, "Användare");
            return true;
        } catch (Exception e) {
            logger.error("Fel vid skickande av verifieringsmail", e);
            return false;
        }
    }

    @Override
    public void sendWelcomeEmail(User user) {
        logger.info("📧 Välkomstmail simulerat skickat till: {} ({})",
                user.getEmail(), user.getFirstName());
    }

    @Override
    public void sendPasswordResetEmail(User user, String resetToken) {
        sendPasswordResetEmail(user.getEmail(), resetToken, user.getFirstName());
    }

    @Override
    public void sendPasswordResetEmail(String email, String resetToken, String firstName) {
        String resetUrl = "http://localhost:8080/reset-password?token=" + resetToken;

        logger.info("📧 =========================");
        logger.info("📧 LÖSENORDSÅTERSTÄLLNING");
        logger.info("📧 =========================");
        logger.info("📧 Till: {} ({})", email, firstName);
        logger.info("📧 Reset-länk: {}", resetUrl);
        logger.info("📧 Token: {}", resetToken);
        logger.info("📧 =========================");
        logger.info("📧 KOPIERA LÄNKEN OVAN FÖR ATT TESTA!");
        logger.info("📧 =========================");

        logger.info("✅ Reset-mail simulerat skickat till: {}", email);
    }

    @Override
    public boolean sendPasswordResetEmail(String email, String resetToken) {
        try {
            sendPasswordResetEmail(email, resetToken, "Användare");
            return true;
        } catch (Exception e) {
            logger.error("Fel vid skickande av reset-mail", e);
            return false;
        }
    }

    @Override
    public void sendOrderConfirmationEmail(User user, String orderNumber) {
        logger.info("📧 Orderbekräftelse simulerat skickat till: {} för order: {}",
                user.getEmail(), orderNumber);
    }

    @Override
    public void sendOrderConfirmation(Order order, String email) {
        logger.info("📧 Orderbekräftelse simulerat skickat till: {} för order: {}",
                email, order.getOrderNumber());
    }

    @Override
    public boolean sendOrderConfirmation(String email, Order order) {
        try {
            sendOrderConfirmation(order, email);
            return true;
        } catch (Exception e) {
            logger.error("Fel vid skickande av orderbekräftelse", e);
            return false;
        }
    }

    @Override
    public void sendAccountDeletionNotification(User deletedUser, String adminUsername, String reason) {
        logger.info("📧 Kontoborttagning-meddelande simulerat skickat till: {} av admin: {} (skäl: {})",
                deletedUser.getEmail(), adminUsername, reason);
    }

    @Override
    public void sendAccountDeactivationNotification(User deactivatedUser, String adminUsername, String reason) {
        logger.info("📧 Kontodeaktivering-meddelande simulerat skickat till: {} av admin: {} (skäl: {})",
                deactivatedUser.getEmail(), adminUsername, reason);
    }

    @Override
    public void sendAccountReactivationNotification(User reactivatedUser, String adminUsername) {
        logger.info("📧 Kontoaktivering-meddelande simulerat skickat till: {} av admin: {}",
                reactivatedUser.getEmail(), adminUsername);
    }

    @Override
    public boolean testEmailConnection() {
        logger.info("📧 Email-anslutning testad (simulerad)");
        return true; // Simulerar framgångsrik test
    }

    @Override
    public boolean isConfigured() {
        logger.info("📧 Email-tjänst är konfigurerad (simulerad)");
        return true; // Simulerar att email är konfigurerat
    }
}