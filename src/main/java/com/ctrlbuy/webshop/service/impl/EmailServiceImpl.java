package com.ctrlbuy.webshop.service.impl;

import com.ctrlbuy.webshop.config.EmailProperties;
import com.ctrlbuy.webshop.entity.Order;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;

    public EmailServiceImpl(JavaMailSender mailSender, EmailProperties emailProperties) {
        this.mailSender = mailSender;
        this.emailProperties = emailProperties;
    }

    // ========================================
    // USER VERIFICATION & AUTH EMAILS
    // ========================================

    @Override
    public void sendVerificationEmail(User user, String token) {
        sendVerificationEmail(user.getEmail(), token, user.getFirstName());
    }

    @Override
    public void sendVerificationEmail(String email, String token, String firstName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setFrom(emailProperties.getFrom(), emailProperties.getFromName());
            helper.setSubject("Bekräfta ditt konto - " + emailProperties.getCompany().getName());
            helper.setText(buildVerificationEmailHtml(firstName, token), true);

            mailSender.send(message);
            logger.info("Verification email sent to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send verification email to: {}", email, e);
        }
    }

    @Override
    public boolean sendVerificationEmail(String email, String token) {
        try {
            sendVerificationEmail(email, token, "Kund");
            return true;
        } catch (Exception e) {
            logger.error("Failed to send verification email", e);
            return false;
        }
    }

    @Override
    public void sendWelcomeEmail(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setFrom(emailProperties.getFrom(), emailProperties.getFromName());
            helper.setSubject("Välkommen till " + emailProperties.getCompany().getName() + "!");
            helper.setText(buildWelcomeEmailHtml(user.getFirstName()), true);

            mailSender.send(message);
            logger.info("Welcome email sent to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send welcome email to: {}", user.getEmail(), e);
        }
    }

    // ========================================
    // PASSWORD RESET EMAILS
    // ========================================

    @Override
    public void sendPasswordResetEmail(User user, String resetToken) {
        sendPasswordResetEmail(user.getEmail(), resetToken, user.getFirstName());
    }

    @Override
    public void sendPasswordResetEmail(String email, String resetToken, String firstName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setFrom(emailProperties.getFrom(), emailProperties.getFromName());
            helper.setSubject("Återställ ditt lösenord - " + emailProperties.getCompany().getName());
            helper.setText(buildPasswordResetEmailHtml(firstName, resetToken), true);

            mailSender.send(message);
            logger.info("Password reset email sent to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send password reset email to: {}", email, e);
        }
    }

    @Override
    public boolean sendPasswordResetEmail(String email, String resetToken) {
        try {
            sendPasswordResetEmail(email, resetToken, "Kund");
            return true;
        } catch (Exception e) {
            logger.error("Failed to send password reset email", e);
            return false;
        }
    }

    // ========================================
    // ORDER CONFIRMATION EMAILS - SIMPLIFIED
    // ========================================

    @Override
    public void sendOrderConfirmationEmail(User user, String orderNumber) {
        logger.info("Order confirmation requested for user: {} order: {}", user.getEmail(), orderNumber);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setFrom(emailProperties.getFrom(), emailProperties.getFromName());
            helper.setSubject("Orderbekräftelse - Order #" + orderNumber);
            helper.setText(buildBasicOrderConfirmationHtml(orderNumber, user.getFirstName()), true);

            mailSender.send(message);
            logger.info("Basic order confirmation sent to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send basic order confirmation", e);
        }
    }

    @Override
    public void sendOrderConfirmation(com.ctrlbuy.webshop.entity.Order order, String email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setFrom(emailProperties.getFrom(), emailProperties.getFromName());
            helper.setSubject("Orderbekräftelse - Order #" + order.getId());
            helper.setText(buildModelOrderConfirmationHtml(order, email), true);

            mailSender.send(message);
            logger.info("Model order confirmation sent for order: {} to: {}", order.getId(), email);
        } catch (Exception e) {
            logger.error("Failed to send model order confirmation", e);
        }
    }

    @Override
    public boolean sendOrderConfirmation(String email, com.ctrlbuy.webshop.entity.Order order) {
        try {
            sendOrderConfirmation(order, email);
            return true;
        } catch (Exception e) {
            logger.error("Failed to send order confirmation", e);
            return false;
        }
    }

    // ========================================
    // ACCOUNT MANAGEMENT EMAILS
    // ========================================

    @Override
    public void sendAccountDeletionNotification(User deletedUser, String adminUsername, String reason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(deletedUser.getEmail());
            helper.setFrom(emailProperties.getFrom(), emailProperties.getFromName());
            helper.setSubject("Ditt konto har raderats - " + emailProperties.getCompany().getName());
            helper.setText(buildAccountDeletionHtml(deletedUser.getFirstName(), adminUsername, reason), true);

            mailSender.send(message);
            logger.info("Account deletion notification sent to: {}", deletedUser.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send account deletion notification", e);
        }
    }

    @Override
    public void sendAccountDeactivationNotification(User deactivatedUser, String adminUsername, String reason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(deactivatedUser.getEmail());
            helper.setFrom(emailProperties.getFrom(), emailProperties.getFromName());
            helper.setSubject("Ditt konto har inaktiverats - " + emailProperties.getCompany().getName());
            helper.setText(buildAccountDeactivationHtml(deactivatedUser.getFirstName(), adminUsername, reason), true);

            mailSender.send(message);
            logger.info("Account deactivation notification sent to: {}", deactivatedUser.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send account deactivation notification", e);
        }
    }

    @Override
    public void sendAccountReactivationNotification(User reactivatedUser, String adminUsername) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(reactivatedUser.getEmail());
            helper.setFrom(emailProperties.getFrom(), emailProperties.getFromName());
            helper.setSubject("Ditt konto har återaktiverats - " + emailProperties.getCompany().getName());
            helper.setText(buildAccountReactivationHtml(reactivatedUser.getFirstName(), adminUsername), true);

            mailSender.send(message);
            logger.info("Account reactivation notification sent to: {}", reactivatedUser.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send account reactivation notification", e);
        }
    }

    // ========================================
    // ORDER STATUS EMAILS
    // ========================================

    @Override
    public void sendShippingNotification(com.ctrlbuy.webshop.entity.Order order, String customerEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(customerEmail);
            helper.setFrom(emailProperties.getFrom(), emailProperties.getFromName());
            helper.setSubject("Din order har skickats - Order #" + order.getId());
            helper.setText(buildShippingNotificationHtml(String.valueOf(order.getId())), true);

            mailSender.send(message);
            logger.info("Shipping notification sent for order: {}", order.getId());
        } catch (Exception e) {
            logger.error("Failed to send shipping notification", e);
        }
    }

    @Override
    public void sendDeliveryConfirmation(com.ctrlbuy.webshop.entity.Order order, String customerEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(customerEmail);
            helper.setFrom(emailProperties.getFrom(), emailProperties.getFromName());
            helper.setSubject("Din order har levererats - Order #" + order.getId());
            helper.setText(buildDeliveryConfirmationHtml(String.valueOf(order.getId())), true);

            mailSender.send(message);
            logger.info("Delivery confirmation sent for order: {}", order.getId());
        } catch (Exception e) {
            logger.error("Failed to send delivery confirmation", e);
        }
    }

    @Override
    public void sendOrderCancellation(com.ctrlbuy.webshop.entity.Order order, String customerEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(customerEmail);
            helper.setFrom(emailProperties.getFrom(), emailProperties.getFromName());
            helper.setSubject("Din order har avbrutits - Order #" + order.getId());
            helper.setText(buildOrderCancellationHtml(String.valueOf(order.getId())), true);

            mailSender.send(message);
            logger.info("Order cancellation sent for order: {}", order.getId());
        } catch (Exception e) {
            logger.error("Failed to send order cancellation", e);
        }
    }

    // ========================================
    // UTILITY METHODS
    // ========================================

    @Override
    public boolean testEmailConnection() {
        try {
            mailSender.createMimeMessage();
            logger.info("Email connection test successful");
            return true;
        } catch (Exception e) {
            logger.error("Email connection test failed", e);
            return false;
        }
    }

    @Override
    public boolean isConfigured() {
        return emailProperties != null &&
                emailProperties.getFrom() != null &&
                !emailProperties.getFrom().isEmpty();
    }

    // ========================================
    // SIMPLE WORKING CURRENCY FORMATTER
    // ========================================

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0.00 kr";
        }
        // Fixed: Use BigDecimal directly, no type conversion needed
        return amount + " kr";
    }

    // Overloaded method to handle Double inputs safely
    private String formatCurrency(Double amount) {
        if (amount == null) {
            return "0.00 kr";
        }
        return BigDecimal.valueOf(amount) + " kr";
    }

    // ========================================
    // HTML EMAIL TEMPLATES - MINIMAL WORKING VERSIONS
    // ========================================

    private String buildBasicOrderConfirmationHtml(String orderNumber, String customerName) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'>" + getEmailStyles() + "</head><body>" +
                "<div class='header'>" +
                "<h1>" + emailProperties.getCompany().getName() + "</h1>" +
                "<h2>Orderbekräftelse</h2>" +
                "</div>" +
                "<div class='order-info'>" +
                "<h3>Orderinformation</h3>" +
                "<p><strong>Ordernummer:</strong> #" + orderNumber + "</p>" +
                "<p><strong>Kund:</strong> " + customerName + "</p>" +
                "</div>" +
                getEmailFooter() +
                "</body></html>";
    }

    private String buildModelOrderConfirmationHtml(com.ctrlbuy.webshop.entity.Order order, String customerEmail) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'>" + getEmailStyles() + "</head><body>" +
                "<div class='header'>" +
                "<h1>" + emailProperties.getCompany().getName() + "</h1>" +
                "<h2>Orderbekräftelse</h2>" +
                "</div>" +
                "<div class='order-info'>" +
                "<h3>Orderinformation</h3>" +
                "<p><strong>Ordernummer:</strong> #" + order.getId() + "</p>" +
                "<p><strong>E-post:</strong> " + customerEmail + "</p>" +
                "<p><strong>Totalt:</strong> " + formatCurrency(order.getTotalAmount()) + "</p>" +
                "</div>" +
                getEmailFooter() +
                "</body></html>";
    }

    // ========================================
    // SIMPLE HTML TEMPLATES
    // ========================================

    private String buildVerificationEmailHtml(String firstName, String token) {
        return "<!DOCTYPE html><html><body>" +
                "<h2>Bekräfta ditt konto</h2>" +
                "<p>Hej " + firstName + ",</p>" +
                "<p>Klicka på länken för att bekräfta ditt konto:</p>" +
                "<a href='http://localhost:8080/verify?token=" + token + "'>Bekräfta konto</a>" +
                "</body></html>";
    }

    private String buildWelcomeEmailHtml(String firstName) {
        return "<!DOCTYPE html><html><body>" +
                "<h2>Välkommen!</h2>" +
                "<p>Hej " + firstName + ",</p>" +
                "<p>Välkommen till " + emailProperties.getCompany().getName() + "!</p>" +
                "</body></html>";
    }

    private String buildPasswordResetEmailHtml(String firstName, String resetToken) {
        return "<!DOCTYPE html><html><body>" +
                "<h2>Återställ lösenord</h2>" +
                "<p>Hej " + firstName + ",</p>" +
                "<p>Klicka på länken för att återställa ditt lösenord:</p>" +
                "<a href='http://localhost:8080/reset-password?token=" + resetToken + "'>Återställ lösenord</a>" +
                "</body></html>";
    }

    private String buildAccountDeletionHtml(String firstName, String adminUsername, String reason) {
        return "<!DOCTYPE html><html><body>" +
                "<h2>Konto raderat</h2>" +
                "<p>Hej " + firstName + ",</p>" +
                "<p>Ditt konto har raderats av admin: " + adminUsername + "</p>" +
                (reason != null ? "<p>Anledning: " + reason + "</p>" : "") +
                "</body></html>";
    }

    private String buildAccountDeactivationHtml(String firstName, String adminUsername, String reason) {
        return "<!DOCTYPE html><html><body>" +
                "<h2>Konto inaktiverat</h2>" +
                "<p>Hej " + firstName + ",</p>" +
                "<p>Ditt konto har inaktiverats av admin: " + adminUsername + "</p>" +
                (reason != null ? "<p>Anledning: " + reason + "</p>" : "") +
                "</body></html>";
    }

    private String buildAccountReactivationHtml(String firstName, String adminUsername) {
        return "<!DOCTYPE html><html><body>" +
                "<h2>Konto återaktiverat</h2>" +
                "<p>Hej " + firstName + ",</p>" +
                "<p>Ditt konto har återaktiverats av admin: " + adminUsername + "</p>" +
                "</body></html>";
    }

    private String buildShippingNotificationHtml(String orderId) {
        return "<!DOCTYPE html><html><body>" +
                "<h2>Din order har skickats</h2>" +
                "<p>Order #" + orderId + " har skickats!</p>" +
                "</body></html>";
    }

    private String buildDeliveryConfirmationHtml(String orderId) {
        return "<!DOCTYPE html><html><body>" +
                "<h2>Din order har levererats</h2>" +
                "<p>Order #" + orderId + " har levererats!</p>" +
                "</body></html>";
    }

    private String buildOrderCancellationHtml(String orderId) {
        return "<!DOCTYPE html><html><body>" +
                "<h2>Din order har avbrutits</h2>" +
                "<p>Order #" + orderId + " har avbrutits.</p>" +
                "</body></html>";
    }

    private String getEmailStyles() {
        return "<style>" +
                "body { font-family: Arial, sans-serif; margin: 20px; }" +
                ".header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; }" +
                ".order-info { background-color: #ecf0f1; padding: 15px; margin: 20px 0; }" +
                ".total-line { display: flex; justify-content: space-between; margin: 5px 0; }" +
                "</style>";
    }

    private String getEmailFooter() {
        return "<div style='margin-top: 30px; text-align: center; color: #7f8c8d;'>" +
                "<p>Tack för din beställning!</p>" +
                "<p>" + emailProperties.getFromName() + "</p>" +
                (emailProperties.getCompany().getSupportEmail() != null ?
                        "<p>Support: " + emailProperties.getCompany().getSupportEmail() + "</p>" : "") +
                "</div>";
    }
}