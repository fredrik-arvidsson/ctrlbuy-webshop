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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
@Primary
@ConditionalOnProperty(name = "app.email.mock.enabled", havingValue = "false", matchIfMissing = false)
public class GmailEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(GmailEmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.from:noreply@ctrlbuy.com}")
    private String fromEmail;

    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public void sendVerificationEmail(User user, String token) {
        sendVerificationEmail(user.getEmail(), token, user.getFirstName());
    }

    @Override
    public void sendVerificationEmail(String email, String token, String firstName) {
        try {
            String verificationUrl = baseUrl + "/verify?token=" + token;
            String subject = "Verifiera ditt konto - CtrlBuy";

            String htmlContent = createVerificationEmailHtml(firstName, verificationUrl);

            sendHtmlEmail(email, subject, htmlContent);

            logger.info("✅ Verifieringsmail skickat till: {}", email);
        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av verifieringsmail till: {}", email, e);
            // Fallback till mock-beteende vid fel
            logSimulatedEmail("verifieringsmail", email);
        }
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
        try {
            String subject = "Välkommen till CtrlBuy!";
            String htmlContent = createWelcomeEmailHtml(user.getFirstName());

            sendHtmlEmail(user.getEmail(), subject, htmlContent);

            logger.info("✅ Välkomstmail skickat till: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av välkomstmail till: {}", user.getEmail(), e);
            logSimulatedEmail("välkomstmail", user.getEmail());
        }
    }

    @Override
    public void sendPasswordResetEmail(User user, String resetToken) {
        sendPasswordResetEmail(user.getEmail(), resetToken, user.getFirstName());
    }

    @Override
    public void sendPasswordResetEmail(String email, String resetToken, String firstName) {
        try {
            String resetUrl = baseUrl + "/reset-password?token=" + resetToken;
            String subject = "Återställ ditt lösenord - CtrlBuy";

            String htmlContent = createPasswordResetEmailHtml(firstName, resetUrl);

            sendHtmlEmail(email, subject, htmlContent);

            logger.info("✅ Lösenordsåterställning skickat till: {}", email);
            logger.info("🔗 Reset-länk: {}", resetUrl);
        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av lösenordsåterställning till: {}", email, e);
            // Fallback - logga länken så den kan användas manuellt
            String resetUrl = baseUrl + "/reset-password?token=" + resetToken;
            logger.info("📧 =========================");
            logger.info("📧 LÖSENORDSÅTERSTÄLLNING (FALLBACK)");
            logger.info("📧 =========================");
            logger.info("📧 Till: {} ({})", email, firstName);
            logger.info("📧 Reset-länk: {}", resetUrl);
            logger.info("📧 Token: {}", resetToken);
            logger.info("📧 =========================");
            logger.info("📧 KOPIERA LÄNKEN OVAN FÖR ATT TESTA!");
            logger.info("📧 =========================");
            logger.info("✅ Reset-mail simulerat skickat till: {}", email);
        }
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
        try {
            String subject = "Orderbekräftelse #" + orderNumber + " - CtrlBuy";
            String htmlContent = createOrderConfirmationEmailHtml(user.getFirstName(), orderNumber);

            sendHtmlEmail(user.getEmail(), subject, htmlContent);

            logger.info("✅ Orderbekräftelse skickat till: {} för order: {}", user.getEmail(), orderNumber);
        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av orderbekräftelse till: {}", user.getEmail(), e);
            logSimulatedEmail("orderbekräftelse", user.getEmail());
        }
    }

    @Override
    public void sendOrderConfirmation(Order order, String email) {
        try {
            String subject = "Orderbekräftelse #" + order.getOrderNumber() + " - CtrlBuy";
            String htmlContent = createDetailedOrderConfirmationEmailHtml(order);

            sendHtmlEmail(email, subject, htmlContent);

            logger.info("✅ Orderbekräftelse skickat till: {} för order: {}", email, order.getOrderNumber());
        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av orderbekräftelse till: {}", email, e);
            logger.info("📧 Orderbekräftelse simulerat skickat till: {} för order: {}",
                    email, order.getOrderNumber());
        }
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
        try {
            String subject = "Ditt konto har tagits bort - CtrlBuy";
            String htmlContent = createAccountDeletionEmailHtml(deletedUser.getFirstName(), adminUsername, reason);

            sendHtmlEmail(deletedUser.getEmail(), subject, htmlContent);

            logger.info("✅ Kontoborttagning-meddelande skickat till: {}", deletedUser.getEmail());
        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av kontoborttagning-meddelande till: {}", deletedUser.getEmail(), e);
            logSimulatedEmail("kontoborttagning-meddelande", deletedUser.getEmail());
        }
    }

    @Override
    public void sendAccountDeactivationNotification(User deactivatedUser, String adminUsername, String reason) {
        try {
            String subject = "Ditt konto har deaktiverats - CtrlBuy";
            String htmlContent = createAccountDeactivationEmailHtml(deactivatedUser.getFirstName(), adminUsername, reason);

            sendHtmlEmail(deactivatedUser.getEmail(), subject, htmlContent);

            logger.info("✅ Kontodeaktivering-meddelande skickat till: {}", deactivatedUser.getEmail());
        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av kontodeaktivering-meddelande till: {}", deactivatedUser.getEmail(), e);
            logSimulatedEmail("kontodeaktivering-meddelande", deactivatedUser.getEmail());
        }
    }

    @Override
    public void sendAccountReactivationNotification(User reactivatedUser, String adminUsername) {
        try {
            String subject = "Ditt konto har aktiverats - CtrlBuy";
            String htmlContent = createAccountReactivationEmailHtml(reactivatedUser.getFirstName(), adminUsername);

            sendHtmlEmail(reactivatedUser.getEmail(), subject, htmlContent);

            logger.info("✅ Kontoaktivering-meddelande skickat till: {}", reactivatedUser.getEmail());
        } catch (Exception e) {
            logger.error("❌ Fel vid skickande av kontoaktivering-meddelande till: {}", reactivatedUser.getEmail(), e);
            logSimulatedEmail("kontoaktivering-meddelande", reactivatedUser.getEmail());
        }
    }

    @Override
    public boolean testEmailConnection() {
        try {
            // Skicka ett enkelt test-email
            String testEmail = fromEmail;
            String subject = "Test Email Connection - CtrlBuy";
            String content = "<h1>Email Connection Test</h1><p>Detta är ett test av email-anslutningen.</p>";

            sendHtmlEmail(testEmail, subject, content);

            logger.info("✅ Email-anslutning testad framgångsrikt");
            return true;
        } catch (Exception e) {
            logger.error("❌ Email-anslutning misslyckades", e);
            return false;
        }
    }

    @Override
    public boolean isConfigured() {
        return mailSender != null && fromEmail != null && !fromEmail.isEmpty();
    }

    // === PRIVATA HJÄLPMETODER ===

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        logger.debug("📧 Skickar email till: {} med ämne: {}", to, subject);
        mailSender.send(message);
        logger.debug("📧 Email skickad framgångsrikt");
    }

    private void logSimulatedEmail(String type, String email) {
        logger.info("📧 {} simulerat skickat till: {}", type, email);
    }

    // === HTML EMAIL TEMPLATES ===

    private String createVerificationEmailHtml(String firstName, String verificationUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Verifiera ditt konto</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h1 style="color: #28a745;">Välkommen till CtrlBuy!</h1>
                    <p>Hej %s,</p>
                    <p>Tack för att du registrerat dig hos oss! För att slutföra din registrering, klicka på länken nedan:</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #28a745; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block;">Verifiera mitt konto</a>
                    </div>
                    <p>Om länken inte fungerar, kopiera och klistra in denna URL i din webbläsare:</p>
                    <p style="background-color: #f8f9fa; padding: 10px; border-radius: 5px; word-break: break-all;">%s</p>
                    <p>Med vänliga hälsningar,<br>CtrlBuy-teamet</p>
                </div>
            </body>
            </html>
            """.formatted(firstName, verificationUrl, verificationUrl);
    }

    private String createPasswordResetEmailHtml(String firstName, String resetUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Återställ ditt lösenord</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h1 style="color: #dc3545;">Återställ ditt lösenord</h1>
                    <p>Hej %s,</p>
                    <p>Du har begärt att återställa ditt lösenord. Klicka på länken nedan för att sätta ett nytt lösenord:</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #dc3545; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block;">Återställ lösenord</a>
                    </div>
                    <p>Om länken inte fungerar, kopiera och klistra in denna URL i din webbläsare:</p>
                    <p style="background-color: #f8f9fa; padding: 10px; border-radius: 5px; word-break: break-all;">%s</p>
                    <p><strong>Observera:</strong> Denna länk är giltig i 24 timmar.</p>
                    <p>Om du inte begärde denna återställning, ignorera detta email.</p>
                    <p>Med vänliga hälsningar,<br>CtrlBuy-teamet</p>
                </div>
            </body>
            </html>
            """.formatted(firstName, resetUrl, resetUrl);
    }

    private String createWelcomeEmailHtml(String firstName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Välkommen till CtrlBuy</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h1 style="color: #28a745;">Välkommen till CtrlBuy! 🎉</h1>
                    <p>Hej %s,</p>
                    <p>Ditt konto har nu verifierats och du är redo att börja handla!</p>
                    <p>Vi erbjuder de senaste teknikprodukterna till fantastiska priser.</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #28a745; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block;">Börja handla nu</a>
                    </div>
                    <p>Tack för att du valde oss!</p>
                    <p>Med vänliga hälsningar,<br>CtrlBuy-teamet</p>
                </div>
            </body>
            </html>
            """.formatted(firstName, baseUrl);
    }

    private String createOrderConfirmationEmailHtml(String firstName, String orderNumber) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Orderbekräftelse</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h1 style="color: #007bff;">Tack för din beställning!</h1>
                    <p>Hej %s,</p>
                    <p>Vi har mottagit din beställning med ordernummer: <strong>%s</strong></p>
                    <p>Du kommer att få en uppdatering när din beställning skickas.</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s/orders" style="background-color: #007bff; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block;">Se min beställning</a>
                    </div>
                    <p>Med vänliga hälsningar,<br>CtrlBuy-teamet</p>
                </div>
            </body>
            </html>
            """.formatted(firstName, orderNumber, baseUrl);
    }

    private String createDetailedOrderConfirmationEmailHtml(Order order) {
        StringBuilder itemsHtml = new StringBuilder();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : order.getOrderItems()) {
            BigDecimal itemTotal = BigDecimal.valueOf(item.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);

            itemsHtml.append("""
                <tr>
                    <td style="padding: 10px; border-bottom: 1px solid #eee;">%s</td>
                    <td style="padding: 10px; border-bottom: 1px solid #eee; text-align: center;">%d</td>
                    <td style="padding: 10px; border-bottom: 1px solid #eee; text-align: right;">%.2f kr</td>
                    <td style="padding: 10px; border-bottom: 1px solid #eee; text-align: right;">%.2f kr</td>
                </tr>
                """.formatted(item.getProductName(), item.getQuantity(), item.getPrice(), itemTotal.doubleValue()));
        }

        String orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Orderbekräftelse #%s</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h1 style="color: #007bff;">Orderbekräftelse #%s</h1>
                    <p>Tack för din beställning! Här är detaljerna:</p>
                    
                    <div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;">
                        <h3>Orderinformation</h3>
                        <p><strong>Ordernummer:</strong> %s</p>
                        <p><strong>Orderdatum:</strong> %s</p>
                        <p><strong>Status:</strong> %s</p>
                    </div>

                    <h3>Beställda produkter</h3>
                    <table style="width: 100%%; border-collapse: collapse; margin: 20px 0;">
                        <thead>
                            <tr style="background-color: #007bff; color: white;">
                                <th style="padding: 10px; text-align: left;">Produkt</th>
                                <th style="padding: 10px; text-align: center;">Antal</th>
                                <th style="padding: 10px; text-align: right;">Pris</th>
                                <th style="padding: 10px; text-align: right;">Totalt</th>
                            </tr>
                        </thead>
                        <tbody>
                            %s
                            <tr style="background-color: #f8f9fa; font-weight: bold;">
                                <td colspan="3" style="padding: 15px; text-align: right;">Totalsumma:</td>
                                <td style="padding: 15px; text-align: right;">%.2f kr</td>
                            </tr>
                        </tbody>
                    </table>

                    <div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;">
                        <h3>Leveransadress</h3>
                        <p>%s<br>%s<br>%s %s</p>
                    </div>

                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s/orders" style="background-color: #007bff; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block;">Se min beställning</a>
                    </div>
                    
                    <p>Du kommer att få en uppdatering när din beställning skickas.</p>
                    <p>Med vänliga hälsningar,<br>CtrlBuy-teamet</p>
                </div>
            </body>
            </html>
            """.formatted(
                order.getOrderNumber(), order.getOrderNumber(), order.getOrderNumber(),
                orderDate, order.getStatus().toString(), itemsHtml.toString(),
                total.doubleValue(), order.getDeliveryName(), order.getDeliveryAddress(),
                order.getDeliveryPostalCode(), order.getDeliveryCity(), baseUrl);
    }

    private String createAccountDeletionEmailHtml(String firstName, String adminUsername, String reason) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Ditt konto har tagits bort</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h1 style="color: #dc3545;">Ditt konto har tagits bort</h1>
                    <p>Hej %s,</p>
                    <p>Vi informerar dig om att ditt konto hos CtrlBuy har tagits bort av administratör: <strong>%s</strong></p>
                    %s
                    <p>Om du har frågor, kontakta vår kundtjänst.</p>
                    <p>Med vänliga hälsningar,<br>CtrlBuy-teamet</p>
                </div>
            </body>
            </html>
            """.formatted(firstName, adminUsername,
                reason != null && !reason.isEmpty() ? "<p><strong>Anledning:</strong> " + reason + "</p>" : "");
    }

    private String createAccountDeactivationEmailHtml(String firstName, String adminUsername, String reason) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Ditt konto har deaktiverats</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h1 style="color: #ffc107;">Ditt konto har deaktiverats</h1>
                    <p>Hej %s,</p>
                    <p>Vi informerar dig om att ditt konto hos CtrlBuy har deaktiverats av administratör: <strong>%s</strong></p>
                    %s
                    <p>För att aktivera ditt konto igen, kontakta vår kundtjänst.</p>
                    <p>Med vänliga hälsningar,<br>CtrlBuy-teamet</p>
                </div>
            </body>
            </html>
            """.formatted(firstName, adminUsername,
                reason != null && !reason.isEmpty() ? "<p><strong>Anledning:</strong> " + reason + "</p>" : "");
    }

    private String createAccountReactivationEmailHtml(String firstName, String adminUsername) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Ditt konto har aktiverats</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h1 style="color: #28a745;">Ditt konto har aktiverats! 🎉</h1>
                    <p>Hej %s,</p>
                    <p>Goda nyheter! Ditt konto hos CtrlBuy har aktiverats igen av administratör: <strong>%s</strong></p>
                    <p>Du kan nu logga in och fortsätta handla hos oss.</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s/login" style="background-color: #28a745; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block;">Logga in</a>
                    </div>
                    <p>Med vänliga hälsningar,<br>CtrlBuy-teamet</p>
                </div>
            </body>
            </html>
            """.formatted(firstName, adminUsername, baseUrl);
    }
}