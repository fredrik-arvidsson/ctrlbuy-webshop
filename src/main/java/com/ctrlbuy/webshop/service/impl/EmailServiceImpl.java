package com.ctrlbuy.webshop.service.impl;

import com.ctrlbuy.webshop.model.Order;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Email service implementation for handling all email communications
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.email.from:noreply@ctrlbuy.com}")
    private String fromEmail;

    @Value("${app.email.company-name:CtrlBuy}")
    private String companyName;

    // ✅ 1. EMAIL VERIFICATION - New interface method
    @Override
    public void sendVerificationEmail(User user, String token) {
        try {
            logger.info("📧 Skickar verifieringsmail till: {}", user.getEmail());

            String verificationUrl = baseUrl + "/verify-email?token=" + token;

            Context context = new Context();
            context.setVariable("firstName", user.getFirstName());
            context.setVariable("lastName", user.getLastName());
            context.setVariable("email", user.getEmail());
            context.setVariable("verificationUrl", verificationUrl);
            context.setVariable("companyName", companyName);
            context.setVariable("currentYear", LocalDateTime.now().getYear());

            String htmlContent = templateEngine.process("emails/email-verification", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, companyName);
            helper.setTo(user.getEmail());
            helper.setSubject("Verifiera din email - " + companyName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("✅ Verifieringsmail skickat till: {}", user.getEmail());

        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av verifieringsmail till {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Kunde inte skicka verifieringsmail", e);
        }
    }

    // ✅ 2. EMAIL VERIFICATION - Legacy method
    @Override
    public void sendVerificationEmail(String email, String token, String firstName) {
        try {
            logger.info("📧 Skickar verifieringsmail (legacy) till: {}", email);

            String verificationUrl = baseUrl + "/verify-email?token=" + token;

            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("email", email);
            context.setVariable("verificationUrl", verificationUrl);
            context.setVariable("companyName", companyName);
            context.setVariable("currentYear", LocalDateTime.now().getYear());

            String htmlContent = templateEngine.process("emails/email-verification", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, companyName);
            helper.setTo(email);
            helper.setSubject("Verifiera din email - " + companyName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("✅ Verifieringsmail (legacy) skickat till: {}", email);

        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av verifieringsmail (legacy) till {}: {}", email, e.getMessage());
            throw new RuntimeException("Kunde inte skicka verifieringsmail", e);
        }
    }

    // ✅ 3. WELCOME EMAIL - After successful verification
    @Override
    public void sendWelcomeEmail(User user) {
        try {
            logger.info("🎉 Skickar välkomstmail till: {}", user.getEmail());

            Context context = new Context();
            context.setVariable("firstName", user.getFirstName());
            context.setVariable("lastName", user.getLastName());
            context.setVariable("loginUrl", baseUrl + "/login");
            context.setVariable("shopUrl", baseUrl + "/products");
            context.setVariable("companyName", companyName);
            context.setVariable("currentYear", LocalDateTime.now().getYear());

            String htmlContent = templateEngine.process("emails/welcome", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, companyName);
            helper.setTo(user.getEmail());
            helper.setSubject("Välkommen till " + companyName + "!");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("✅ Välkomstmail skickat till: {}", user.getEmail());

        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av välkomstmail till {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Kunde inte skicka välkomstmail", e);
        }
    }

    // ✅ 4. PASSWORD RESET - New interface method
    @Override
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            logger.info("🔐 Skickar lösenordsåterställning till: {}", user.getEmail());

            String resetUrl = baseUrl + "/reset-password?token=" + resetToken;

            Context context = new Context();
            context.setVariable("firstName", user.getFirstName());
            context.setVariable("resetUrl", resetUrl);
            context.setVariable("companyName", companyName);
            context.setVariable("currentYear", LocalDateTime.now().getYear());
            context.setVariable("expiryHours", 1); // 1 timme

            String htmlContent = templateEngine.process("emails/password-reset", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, companyName);
            helper.setTo(user.getEmail());
            helper.setSubject("Återställ ditt lösenord - " + companyName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("✅ Lösenordsåterställning skickad till: {}", user.getEmail());

        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av lösenordsåterställning till {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Kunde inte skicka lösenordsåterställning", e);
        }
    }

    // ✅ 5. PASSWORD RESET - Legacy method
    @Override
    public void sendPasswordResetEmail(String email, String resetToken, String firstName) {
        try {
            logger.info("🔐 Skickar lösenordsåterställning (legacy) till: {}", email);

            String resetUrl = baseUrl + "/reset-password?token=" + resetToken;

            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("resetUrl", resetUrl);
            context.setVariable("companyName", companyName);
            context.setVariable("currentYear", LocalDateTime.now().getYear());
            context.setVariable("expiryHours", 1); // 1 timme

            String htmlContent = templateEngine.process("emails/password-reset", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, companyName);
            helper.setTo(email);
            helper.setSubject("Återställ ditt lösenord - " + companyName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("✅ Lösenordsåterställning (legacy) skickad till: {}", email);

        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av lösenordsåterställning (legacy) till {}: {}", email, e.getMessage());
            throw new RuntimeException("Kunde inte skicka lösenordsåterställning", e);
        }
    }

    // ✅ 6. ORDER CONFIRMATION - New interface method
    @Override
    public void sendOrderConfirmationEmail(User user, String orderNumber) {
        try {
            logger.info("🧾 Skickar orderbekräftelse till: {} för order: {}", user.getEmail(), orderNumber);

            Context context = new Context();
            context.setVariable("firstName", user.getFirstName());
            context.setVariable("orderNumber", orderNumber);
            context.setVariable("orderDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            context.setVariable("companyName", companyName);
            context.setVariable("currentYear", LocalDateTime.now().getYear());
            context.setVariable("supportEmail", fromEmail);

            String htmlContent = templateEngine.process("emails/order-confirmation-simple", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, companyName);
            helper.setTo(user.getEmail());
            helper.setSubject("Orderbekräftelse #" + orderNumber + " - " + companyName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("✅ Orderbekräftelse skickad till: {} för order: {}", user.getEmail(), orderNumber);

        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av orderbekräftelse till {} för order {}: {}", user.getEmail(), orderNumber, e.getMessage());
            throw new RuntimeException("Kunde inte skicka orderbekräftelse", e);
        }
    }

    // ✅ 7. ORDER CONFIRMATION - Legacy method with full Order object
    @Override
    public void sendOrderConfirmation(Order order, String email) {
        try {
            logger.info("🧾 Skickar detaljerad orderbekräftelse för order: {} till: {}", order.getId(), email);

            User user = order.getUser();
            String firstName = (user != null) ? user.getFirstName() : "Kund";

            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("order", order);
            context.setVariable("orderNumber", String.format("ORD-%05d", order.getId()));
            context.setVariable("orderDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            // Note: Order creation date should be: order.getOrderDate() or similar
            // context.setVariable("totalAmount", order.getTotalAmount());
            // context.setVariable("items", order.getOrderItems());
            context.setVariable("companyName", companyName);
            context.setVariable("currentYear", LocalDateTime.now().getYear());
            context.setVariable("supportEmail", fromEmail);

            String htmlContent = templateEngine.process("emails/order-confirmation-detailed", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, companyName);
            helper.setTo(email);
            helper.setSubject("Orderbekräftelse #" + String.format("ORD-%05d", order.getId()) + " - " + companyName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("✅ Detaljerad orderbekräftelse skickad till: {} för order: {}", email, order.getId());

        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av detaljerad orderbekräftelse för order {} till {}: {}", order.getId(), email, e.getMessage());
            throw new RuntimeException("Kunde inte skicka orderbekräftelse", e);
        }
    }

    // ✅ 8. TEST EMAIL CONNECTION
    @Override
    public boolean testEmailConnection() {
        try {
            logger.info("🧪 Testar email-anslutning...");

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, companyName);
            helper.setTo(fromEmail); // Skicka till oss själva
            helper.setSubject("Email Test - " + companyName);
            helper.setText("Detta är ett testmail för att kontrollera email-konfigurationen.\n\nTid: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), false);

            mailSender.send(message);
            logger.info("✅ Email-test lyckades!");
            return true;

        } catch (Exception e) {
            logger.error("❌ Email-test misslyckades: {}", e.getMessage());
            return false;
        }
    }

    // ✅ UTILITY METODER

    /**
     * Skicka enkelt textmail (för debug)
     */
    public boolean sendSimpleEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(fromEmail, companyName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, false);

            mailSender.send(message);
            logger.info("✅ Enkelt email skickat till: {}", to);
            return true;

        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av enkelt email till {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Skicka återskicka verifieringsmail (admin-funktion)
     */
    public boolean resendVerificationEmail(User user) {
        try {
            if (user.getVerificationToken() == null) {
                logger.warn("⚠️ Ingen verifieringstoken för användare: {}", user.getEmail());
                return false;
            }

            sendVerificationEmail(user, user.getVerificationToken());
            return true;

        } catch (Exception e) {
            logger.error("❌ Fel vid återskickande av verifieringsmail till {}: {}", user.getEmail(), e.getMessage());
            return false;
        }
    }

    /**
     * Formatera pengar för email-templates
     */
    private String formatCurrency(BigDecimal amount) {
        return String.format("%.2f kr", amount);
    }
}