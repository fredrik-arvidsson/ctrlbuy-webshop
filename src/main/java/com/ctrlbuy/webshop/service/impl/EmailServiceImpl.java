package com.ctrlbuy.webshop.service.impl;

import com.ctrlbuy.webshop.model.Order;
import com.ctrlbuy.webshop.model.OrderItem;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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

    @Autowired
    private Environment env;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.email.from:noreply@ctrlbuy.com}")
    private String fromEmail;

    @Value("${app.email.company-name:CtrlBuy}")
    private String companyName;

    // ✅ NY METOD: Kontrollera om email är konfigurerat
    public boolean isConfigured() {
        String host = env.getProperty("spring.mail.host");
        String username = env.getProperty("spring.mail.username");
        String password = env.getProperty("spring.mail.password");

        boolean configured = host != null && !host.isEmpty() &&
                username != null && !username.isEmpty() &&
                password != null && !password.isEmpty();

        logger.debug("Email service configured: {}", configured);
        return configured;
    }

    // ✅ 1. EMAIL VERIFICATION - New interface method
    @Override
    public void sendVerificationEmail(User user, String token) {
        sendVerificationEmail(user.getEmail(), token, user.getFirstName());
    }

    // ✅ 2. EMAIL VERIFICATION - Legacy method
    @Override
    public void sendVerificationEmail(String email, String token, String firstName) {
        try {
            logger.info("📧 Skickar verifieringsmail till: {}", email);

            String verificationUrl = baseUrl + "/verify-email?token=" + token;

            // Försök med Thymeleaf template först
            try {
                Context context = new Context();
                context.setVariable("firstName", firstName);
                context.setVariable("email", email);
                context.setVariable("verificationUrl", verificationUrl);
                context.setVariable("companyName", companyName);
                context.setVariable("currentYear", LocalDateTime.now().getYear());

                String htmlContent = templateEngine.process("emails/email-verification", context);
                sendHtmlEmail(email, "Verifiera din email - " + companyName, htmlContent);
            } catch (Exception templateError) {
                // Om template inte finns, använd enkel HTML
                logger.warn("Kunde inte använda Thymeleaf template, använder enkel HTML");
                String htmlContent = createSimpleVerificationEmail(firstName, verificationUrl);
                sendHtmlEmail(email, "Verifiera din email - " + companyName, htmlContent);
            }

            logger.info("✅ Verifieringsmail skickat till: {}", email);

        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av verifieringsmail till {}: {}", email, e.getMessage());
            throw new RuntimeException("Kunde inte skicka verifieringsmail", e);
        }
    }

    // ✅ NY METOD: Email verification som returnerar boolean (för AdminController)
    public boolean sendVerificationEmail(String email, String token) {
        try {
            if (!isConfigured()) {
                logger.warn("Email service is not configured - skipping email to: {}", email);
                return false;
            }

            sendVerificationEmail(email, token, "");
            return true;
        } catch (Exception e) {
            logger.error("Failed to send verification email to: {}", email, e);
            return false;
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
            sendHtmlEmail(user.getEmail(), "Välkommen till " + companyName + "!", htmlContent);

            logger.info("✅ Välkomstmail skickat till: {}", user.getEmail());

        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av välkomstmail till {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Kunde inte skicka välkomstmail", e);
        }
    }

    // ✅ 4. PASSWORD RESET - New interface method
    @Override
    public void sendPasswordResetEmail(User user, String resetToken) {
        sendPasswordResetEmail(user.getEmail(), resetToken, user.getFirstName());
    }

    // ✅ 5. PASSWORD RESET - Legacy method
    @Override
    public void sendPasswordResetEmail(String email, String resetToken, String firstName) {
        try {
            logger.info("🔐 Skickar lösenordsåterställning till: {}", email);

            String resetUrl = baseUrl + "/reset-password?token=" + resetToken;

            // Försök med Thymeleaf template först
            try {
                Context context = new Context();
                context.setVariable("firstName", firstName);
                context.setVariable("resetUrl", resetUrl);
                context.setVariable("companyName", companyName);
                context.setVariable("currentYear", LocalDateTime.now().getYear());
                context.setVariable("expiryHours", 1);

                String htmlContent = templateEngine.process("emails/password-reset", context);
                sendHtmlEmail(email, "Återställ ditt lösenord - " + companyName, htmlContent);
            } catch (Exception templateError) {
                // Om template inte finns, använd enkel HTML
                logger.warn("Kunde inte använda Thymeleaf template, använder enkel HTML");
                String htmlContent = createSimplePasswordResetEmail(firstName, resetUrl);
                sendHtmlEmail(email, "Återställ ditt lösenord - " + companyName, htmlContent);
            }

            logger.info("✅ Lösenordsåterställning skickad till: {}", email);

        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av lösenordsåterställning till {}: {}", email, e.getMessage());
            throw new RuntimeException("Kunde inte skicka lösenordsåterställning", e);
        }
    }

    // ✅ NY METOD: Password reset som returnerar boolean
    public boolean sendPasswordResetEmail(String email, String resetToken) {
        try {
            if (!isConfigured()) {
                logger.warn("Email service is not configured - skipping email to: {}", email);
                return false;
            }

            sendPasswordResetEmail(email, resetToken, "");
            return true;
        } catch (Exception e) {
            logger.error("Failed to send password reset email to: {}", email, e);
            return false;
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
            sendHtmlEmail(user.getEmail(), "Orderbekräftelse #" + orderNumber + " - " + companyName, htmlContent);

            logger.info("✅ Orderbekräftelse skickad till: {} för order: {}", user.getEmail(), orderNumber);

        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av orderbekräftelse till {} för order {}: {}", user.getEmail(), orderNumber, e.getMessage());
            throw new RuntimeException("Kunde inte skicka orderbekräftelse", e);
        }
    }

    // ✅ 7. ORDER CONFIRMATION - Legacy method with full Order object
    @Override
    public void sendOrderConfirmation(Order order, String email) {
        sendOrderConfirmation(email, order);
    }

    // ✅ NY METOD: Order confirmation som returnerar boolean (för CheckoutController)
    public boolean sendOrderConfirmation(String toEmail, Order order) {
        try {
            if (!isConfigured()) {
                logger.error("E-posttjänsten är inte konfigurerad");
                return false;
            }

            logger.info("🧾 Skickar detaljerad orderbekräftelse för order: {} till: {}", order.getId(), toEmail);

            // Försök med Thymeleaf template först
            try {
                Context context = new Context();
                context.setVariable("order", order);
                context.setVariable("orderNumber", order.getOrderNumber());
                context.setVariable("orderDate", order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                context.setVariable("companyName", companyName);
                context.setVariable("currentYear", LocalDateTime.now().getYear());
                context.setVariable("supportEmail", fromEmail);

                String htmlContent = templateEngine.process("emails/order-confirmation-detailed", context);
                sendHtmlEmail(toEmail, "Orderbekräftelse #" + order.getOrderNumber() + " - " + companyName, htmlContent);
            } catch (Exception templateError) {
                // Om template inte finns, använd enkel HTML
                logger.warn("Kunde inte använda Thymeleaf template, använder enkel HTML");
                String htmlContent = createSimpleOrderConfirmationEmail(order);
                sendHtmlEmail(toEmail, "Orderbekräftelse #" + order.getOrderNumber() + " - " + companyName, htmlContent);
            }

            logger.info("✅ Detaljerad orderbekräftelse skickad till: {} för order: {}", toEmail, order.getId());

            return true;

        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av detaljerad orderbekräftelse för order {} till {}: {}",
                    order.getId(), toEmail, e.getMessage());
            return false;
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

    // ✅ 9. ACCOUNT DELETION NOTIFICATION
    @Override
    public void sendAccountDeletionNotification(User deletedUser, String adminUsername, String reason) {
        try {
            logger.info("🗑️ Skickar kontoborttagningsnotifiering till: {}", deletedUser.getEmail());

            String htmlContent = createAccountDeletionEmailContent(deletedUser, adminUsername, reason);
            sendHtmlEmail(deletedUser.getEmail(), "Ditt CtrlBuy-konto har tagits bort", htmlContent);

            logger.info("✅ Kontoborttagningsnotifiering skickad till: {}", deletedUser.getEmail());

        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av kontoborttagningsnotifiering till {}: {}",
                    deletedUser.getEmail(), e.getMessage());
            throw new RuntimeException("Kunde inte skicka kontoborttagningsnotifiering", e);
        }
    }

    // ✅ 10. ACCOUNT DEACTIVATION NOTIFICATION
    @Override
    public void sendAccountDeactivationNotification(User deactivatedUser, String adminUsername, String reason) {
        try {
            logger.info("⏸️ Skickar kontoinaktiveringsnotifiering till: {}", deactivatedUser.getEmail());

            String htmlContent = createAccountDeactivationEmailContent(deactivatedUser, adminUsername, reason);
            sendHtmlEmail(deactivatedUser.getEmail(), "Ditt CtrlBuy-konto har inaktiverats", htmlContent);

            logger.info("✅ Kontoinaktiveringsnotifiering skickad till: {}", deactivatedUser.getEmail());

        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av kontoinaktiveringsnotifiering till {}: {}",
                    deactivatedUser.getEmail(), e.getMessage());
            // Logga bara fel, kasta inte exception för detta
        }
    }

    // ✅ 11. ACCOUNT REACTIVATION NOTIFICATION
    @Override
    public void sendAccountReactivationNotification(User reactivatedUser, String adminUsername) {
        try {
            logger.info("▶️ Skickar kontoåteraktiveringsnotifiering till: {}", reactivatedUser.getEmail());

            String htmlContent = createAccountReactivationEmailContent(reactivatedUser, adminUsername);
            sendHtmlEmail(reactivatedUser.getEmail(), "Ditt CtrlBuy-konto har återaktiverats", htmlContent);

            logger.info("✅ Kontoåteraktiveringsnotifiering skickad till: {}", reactivatedUser.getEmail());

        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av återaktiveringsnotifiering till {}: {}",
                    reactivatedUser.getEmail(), e.getMessage());
            // Logga bara fel, kasta inte exception för detta
        }
    }

    // ✅ UTILITY METODER

    /**
     * Skicka HTML email
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, companyName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new MessagingException("Failed to send email", e);
        }
    }

    /**
     * Formatera pengar för email-templates
     */
    private String formatCurrency(Double amount) {
        return String.format("%.2f kr", amount);
    }

    // ✅ ENKLA HTML TEMPLATES (om Thymeleaf templates saknas)

    private String createSimpleVerificationEmail(String firstName, String verificationUrl) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #007bff;">Välkommen till %s!</h2>
                    <p>Hej %s,</p>
                    <p>Tack för att du registrerat dig. För att slutföra din registrering behöver du verifiera din e-postadress.</p>
                    <div style="margin: 30px 0; text-align: center;">
                        <a href="%s" style="background-color: #007bff; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">
                            Verifiera e-postadress
                        </a>
                    </div>
                    <p>Eller kopiera och klistra in denna länk i din webbläsare:</p>
                    <p style="word-break: break-all; color: #007bff;">%s</p>
                    <p style="margin-top: 30px; font-size: 14px; color: #666;">
                        Denna länk är giltig i 24 timmar. Om du inte har registrerat dig på %s kan du ignorera detta mail.
                    </p>
                    <hr style="margin-top: 30px; border: none; border-top: 1px solid #eee;">
                    <p style="font-size: 12px; color: #999;">
                        Detta är ett automatiskt mail från %s. Vänligen svara inte på detta mail.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(companyName, firstName, verificationUrl, verificationUrl, companyName, companyName);
    }

    private String createSimplePasswordResetEmail(String firstName, String resetUrl) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #007bff;">Återställ ditt lösenord</h2>
                    <p>Hej %s,</p>
                    <p>Vi har mottagit en begäran om att återställa lösenordet för ditt konto.</p>
                    <div style="margin: 30px 0; text-align: center;">
                        <a href="%s" style="background-color: #007bff; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">
                            Återställ lösenord
                        </a>
                    </div>
                    <p>Eller kopiera och klistra in denna länk i din webbläsare:</p>
                    <p style="word-break: break-all; color: #007bff;">%s</p>
                    <p style="margin-top: 30px; font-size: 14px; color: #666;">
                        Denna länk är giltig i 1 timme. Om du inte har begärt att återställa ditt lösenord kan du ignorera detta mail.
                    </p>
                    <hr style="margin-top: 30px; border: none; border-top: 1px solid #eee;">
                    <p style="font-size: 12px; color: #999;">
                        Detta är ett automatiskt mail från %s. Vänligen svara inte på detta mail.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(firstName, resetUrl, resetUrl, companyName);
    }

    private String createSimpleOrderConfirmationEmail(Order order) {
        StringBuilder content = new StringBuilder();
        content.append("<html><body style='font-family: Arial, sans-serif;'>");
        content.append("<div style='max-width: 600px; margin: auto; padding: 20px;'>");

        // Header
        content.append("<div style='background-color: #007bff; color: white; padding: 20px; text-align: center;'>");
        content.append("<h1 style='margin: 0;'>Tack för din beställning!</h1>");
        content.append("</div>");

        // Order info
        content.append("<div style='padding: 20px; background-color: #f8f9fa;'>");
        content.append("<h2>Ordernummer: ").append(order.getOrderNumber()).append("</h2>");
        content.append("<p>Beställningsdatum: ").append(
                order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        ).append("</p>");

        // Leveransadress
        content.append("<h3>Leveransadress:</h3>");
        content.append("<p>");
        content.append(order.getDeliveryName()).append("<br>");
        content.append(order.getDeliveryAddress()).append("<br>");
        content.append(order.getDeliveryPostalCode()).append(" ").append(order.getDeliveryCity()).append("<br>");
        if (order.getDeliveryPhone() != null) {
            content.append("Telefon: ").append(order.getDeliveryPhone()).append("<br>");
        }
        content.append("</p>");

        // Orderrader
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            content.append("<h3>Din beställning:</h3>");
            content.append("<table style='width: 100%; border-collapse: collapse;'>");
            content.append("<tr style='background-color: #e9ecef;'>");
            content.append("<th style='padding: 10px; text-align: left;'>Produkt</th>");
            content.append("<th style='padding: 10px; text-align: center;'>Antal</th>");
            content.append("<th style='padding: 10px; text-align: right;'>Pris</th>");
            content.append("<th style='padding: 10px; text-align: right;'>Summa</th>");
            content.append("</tr>");

            for (OrderItem item : order.getOrderItems()) {
                double itemTotal = item.getPrice().doubleValue() * item.getQuantity();

                content.append("<tr>");
                content.append("<td style='padding: 10px; border-bottom: 1px solid #dee2e6;'>")
                        .append(item.getProductName()).append("</td>");
                content.append("<td style='padding: 10px; text-align: center; border-bottom: 1px solid #dee2e6;'>")
                        .append(item.getQuantity()).append("</td>");
                content.append("<td style='padding: 10px; text-align: right; border-bottom: 1px solid #dee2e6;'>")
                        .append(formatCurrency(item.getPrice().doubleValue())).append("</td>");
                content.append("<td style='padding: 10px; text-align: right; border-bottom: 1px solid #dee2e6;'>")
                        .append(formatCurrency(itemTotal)).append("</td>");
                content.append("</tr>");
            }

            // Total
            content.append("<tr>");
            content.append("<td colspan='3' style='padding: 10px; text-align: right;'><strong>Totalt:</strong></td>");
            content.append("<td style='padding: 10px; text-align: right;'><strong>")
                    .append(formatCurrency(order.getTotalAmount())).append("</strong></td>");
            content.append("</tr>");
            content.append("</table>");
        }

        // Betalningsmetod
        if (order.getPaymentMethod() != null) {
            content.append("<p style='margin-top: 20px;'><strong>Betalningsmetod:</strong> ")
                    .append(order.getPaymentMethod()).append("</p>");
        }

        // Status
        content.append("<p><strong>Status:</strong> ").append(order.getStatus()).append("</p>");

        // Footer
        content.append("<hr style='margin-top: 30px;'>");
        content.append("<p>Vi kommer att behandla din beställning så snart som möjligt.</p>");
        content.append("<p>Om du har frågor, kontakta oss på support@ctrlbuy.se</p>");
        content.append("<p>Med vänliga hälsningar,<br><strong>").append(companyName).append(" Team</strong></p>");
        content.append("</div>");
        content.append("</div>");
        content.append("</body></html>");

        return content.toString();
    }

    // ✅ PRIVATA HJÄLPMETODER FÖR KONTOADMINISTRATION

    private String createAccountDeletionEmailContent(User deletedUser, String adminUsername, String reason) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background: linear-gradient(135deg, #dc3545, #c82333); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                        <h1 style="margin: 0;">⚠️ Kontoborttagning</h1>
                        <p style="margin: 10px 0 0 0;">Viktigt meddelande angående ditt %s-konto</p>
                    </div>
                    
                    <div style="background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px;">
                        <p>Hej %s,</p>
                        
                        <div style="background: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 20px 0;">
                            <strong>🚫 Ditt %s-konto har tagits bort permanent</strong><br>
                            <strong>Datum:</strong> %s<br>
                            <strong>Utförd av:</strong> Administratör (%s)
                            %s
                        </div>
                        
                        <p><strong>Vad detta innebär:</strong></p>
                        <ul>
                            <li>Ditt konto och all associerad data har tagits bort permanent</li>
                            <li>Du kan inte längre logga in på %s</li>
                            <li>All orderhistorik och personlig information har raderats</li>
                            <li>Denna åtgärd kan inte ångras</li>
                        </ul>
                        
                        <div style="background: #e7f3ff; border: 1px solid #b8daff; padding: 15px; border-radius: 5px; margin: 20px 0;">
                            <strong>📞 Har du frågor?</strong><br>
                            Om du anser att detta är ett misstag eller har frågor om borttagningen, 
                            vänligen kontakta vår kundtjänst på <a href="mailto:%s">%s</a>
                        </div>
                        
                        <p>Tack för att du var en del av %s-communityn.</p>
                        
                        <p>Med vänliga hälsningar,<br>
                        <strong>%s Team</strong></p>
                    </div>
                    
                    <div style="text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #dee2e6; color: #6c757d;">
                        <p>Detta är ett automatiskt meddelande från %s<br>
                        <small>Skickat: %s</small></p>
                    </div>
                </div>
            </body>
            </html>
            """,
                companyName,
                deletedUser.getFirstName(),
                companyName,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                adminUsername,
                reason != null && !reason.trim().isEmpty() ?
                        String.format("<br><strong>Anledning:</strong> %s", reason) : "",
                companyName,
                fromEmail,
                fromEmail,
                companyName,
                companyName,
                companyName,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    private String createAccountDeactivationEmailContent(User deactivatedUser, String adminUsername, String reason) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background: linear-gradient(135deg, #fd7e14, #e55100); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                        <h1 style="margin: 0;">⏸️ Konto inaktiverat</h1>
                        <p style="margin: 10px 0 0 0;">Meddelande angående ditt %s-konto</p>
                    </div>
                    
                    <div style="background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px;">
                        <p>Hej %s,</p>
                        
                        <div style="background: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 20px 0;">
                            <strong>⏸️ Ditt %s-konto har inaktiverats</strong><br>
                            <strong>Datum:</strong> %s<br>
                            <strong>Utförd av:</strong> Administratör (%s)
                            %s
                        </div>
                        
                        <p><strong>Vad detta innebär:</strong></p>
                        <ul>
                            <li>Du kan inte logga in på ditt konto för tillfället</li>
                            <li>Dina data är kvar men kontot är temporärt avstängt</li>
                            <li>Inaktiveringen kan eventuellt hävs av en administratör</li>
                        </ul>
                        
                        <div style="background: #e7f3ff; border: 1px solid #b8daff; padding: 15px; border-radius: 5px; margin: 20px 0;">
                            <strong>📞 Överklagande eller frågor?</strong><br>
                            Om du anser att inaktiveringen är felaktig eller har frågor, 
                            vänligen kontakta vår kundtjänst på <a href="mailto:%s">%s</a>
                        </div>
                        
                        <p>Med vänliga hälsningar,<br>
                        <strong>%s Team</strong></p>
                    </div>
                    
                    <div style="text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #dee2e6; color: #6c757d;">
                        <p>Detta är ett automatiskt meddelande från %s<br>
                        <small>Skickat: %s</small></p>
                    </div>
                </div>
            </body>
            </html>
            """,
                companyName,
                deactivatedUser.getFirstName(),
                companyName,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                adminUsername,
                reason != null && !reason.trim().isEmpty() ?
                        String.format("<br><strong>Anledning:</strong> %s", reason) : "",
                fromEmail,
                fromEmail,
                companyName,
                companyName,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    private String createAccountReactivationEmailContent(User reactivatedUser, String adminUsername) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background: linear-gradient(135deg, #28a745, #20c997); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                        <h1 style="margin: 0;">✅ Välkommen tillbaka!</h1>
                        <p style="margin: 10px 0 0 0;">Ditt %s-konto är nu aktivt igen</p>
                    </div>
                    
                    <div style="background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px;">
                        <p>Hej %s,</p>
                        
                        <div style="background: #d4edda; border: 1px solid #c3e6cb; padding: 15px; border-radius: 5px; margin: 20px 0;">
                            <strong>🎉 Ditt %s-konto har återaktiverats</strong><br>
                            <strong>Datum:</strong> %s<br>
                            <strong>Utförd av:</strong> Administratör (%s)
                        </div>
                        
                        <p><strong>Vad detta innebär:</strong></p>
                        <ul>
                            <li>Du kan nu logga in på ditt konto igen</li>
                            <li>All din data och orderhistorik är fortfarande kvar</li>
                            <li>Du har full tillgång till alla %s-funktioner</li>
                        </ul>
                        
                        <div style="background: #e7f3ff; border: 1px solid #b8daff; padding: 15px; border-radius: 5px; margin: 20px 0; text-align: center;">
                            <strong>🛒 Redo att handla?</strong><br>
                            <a href="%s/login" style="background: #28a745; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 10px 0;">Logga in nu</a>
                        </div>
                        
                        <p>Vi ser fram emot att välkomna dig tillbaka till %s!</p>
                        
                        <p>Med vänliga hälsningar,<br>
                        <strong>%s Team</strong></p>
                    </div>
                    
                    <div style="text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #dee2e6; color: #6c757d;">
                        <p>Detta är ett automatiskt meddelande från %s<br>
                        <small>Skickat: %s</small></p>
                    </div>
                </div>
            </body>
            </html>
            """,
                companyName,
                reactivatedUser.getFirstName(),
                companyName,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                adminUsername,
                companyName,
                baseUrl,
                companyName,
                companyName,
                companyName,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }
}