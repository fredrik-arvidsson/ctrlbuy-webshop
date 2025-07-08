package com.ctrlbuy.webshop.service.impl;

import com.ctrlbuy.webshop.entity.Order;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Primary  // ✅ LÄGG TILL DENNA RAD
public class GmailEmailService implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Base URL för alla länkar
    private static final String BASE_URL = "http://localhost:8080";

    @Override
    public void sendVerificationEmail(User user, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("🔐 Verifiera ditt CTRLBUY-konto");
        message.setText("Hej " + user.getUsername() + "!\n\nKlicka här för att verifiera ditt konto: " + BASE_URL + "/verify?token=" + token + "\n\nMvh,\nCTRLBUY Team");
        mailSender.send(message);
    }

    @Override
    public void sendVerificationEmail(String email, String token, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("🔐 Verifiera ditt CTRLBUY-konto");
        message.setText("Hej " + firstName + "!\n\nKlicka här för att verifiera: " + BASE_URL + "/verify?token=" + token + "\n\nMvh,\nCTRLBUY Team");
        mailSender.send(message);
    }

    @Override
    public boolean sendVerificationEmail(String email, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("🔐 Verifiera ditt CTRLBUY-konto");
            message.setText("Klicka här för att verifiera: " + BASE_URL + "/verify?token=" + token + "\n\nMvh,\nCTRLBUY Team");
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void sendWelcomeEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("🎉 Välkommen till CTRLBUY!");
        message.setText("Hej " + user.getUsername() + "!\n\nVälkommen till CTRLBUY - din nya favoritbutik för premium teknik!\n\nMvh,\nCTRLBUY Team");
        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(User user, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("🔑 Återställ lösenord - CTRLBUY");
        message.setText("Hej " + user.getUsername() + "!\n\nKlicka här för att återställa ditt lösenord: " + BASE_URL + "/reset-password?token=" + resetToken + "\n\nMvh,\nCTRLBUY Team");
        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String email, String resetToken, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("🔑 Återställ lösenord - CTRLBUY");
        message.setText("Hej " + firstName + "!\n\nKlicka här för att återställa: " + BASE_URL + "/reset-password?token=" + resetToken + "\n\nMvh,\nCTRLBUY Team");
        mailSender.send(message);
    }

    @Override
    public boolean sendPasswordResetEmail(String email, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("🔑 Återställ lösenord - CTRLBUY");
            message.setText("Klicka här för att återställa: " + BASE_URL + "/reset-password?token=" + resetToken + "\n\nMvh,\nCTRLBUY Team");
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void sendOrderConfirmationEmail(User user, String orderNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("✅ Orderbekräftelse - " + orderNumber);
        message.setText("Hej " + user.getUsername() + "!\n\nTack för din beställning!\nOrdernummer: " + orderNumber + "\n\nMvh,\nCTRLBUY Team");
        mailSender.send(message);
    }

    @Override
    public void sendOrderConfirmation(Order order, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("✅ Orderbekräftelse - Order #" + order.getId());
        message.setText("Tack för din beställning!\n\n📋 Orderdetaljer:\nOrdernummer: " + order.getId() +
                "\nTotalt: " + order.getTotalAmount() + " kr\nStatus: " + order.getStatus() +
                "\n\nVi behandlar din order snarast möjligt!\n\nMvh,\nCTRLBUY Team");
        mailSender.send(message);
    }

    @Override
    public boolean sendOrderConfirmation(String email, Order order) {
        try {
            sendOrderConfirmation(order, email);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void sendAccountDeletionNotification(User deletedUser, String adminUsername, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(deletedUser.getEmail());
        message.setSubject("❌ Konto borttaget - CTRLBUY");
        message.setText("Hej " + deletedUser.getUsername() + ",\n\nDitt konto har tagits bort av " + adminUsername +
                ".\nAnledning: " + reason + "\n\nMvh,\nCTRLBUY Team");
        mailSender.send(message);
    }

    @Override
    public void sendAccountDeactivationNotification(User deactivatedUser, String adminUsername, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(deactivatedUser.getEmail());
        message.setSubject("⏸️ Konto inaktiverat - CTRLBUY");
        message.setText("Hej " + deactivatedUser.getUsername() + ",\n\nDitt konto har inaktiverats av " + adminUsername +
                ".\nAnledning: " + reason + "\n\nMvh,\nCTRLBUY Team");
        mailSender.send(message);
    }

    @Override
    public void sendAccountReactivationNotification(User reactivatedUser, String adminUsername) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(reactivatedUser.getEmail());
        message.setSubject("✅ Konto återaktiverat - CTRLBUY");
        message.setText("Hej " + reactivatedUser.getUsername() + ",\n\nDitt konto har återaktiverats av " + adminUsername +
                ".\n\nVälkommen tillbaka!\n\nMvh,\nCTRLBUY Team");
        mailSender.send(message);
    }

    @Override
    public void sendShippingNotification(Order order, String customerEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(customerEmail);
        message.setSubject("📦 Order skickad - Order #" + order.getId());
        message.setText("Din order har skickats!\n\n📋 Orderdetaljer:\nOrdernummer: " + order.getId() +
                "\nTotalt: " + order.getTotalAmount() + " kr\n\nDu kan förvänta dig leverans inom några dagar.\n\nMvh,\nCTRLBUY Team");
        mailSender.send(message);
    }

    @Override
    public void sendDeliveryConfirmation(Order order, String customerEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(customerEmail);
        message.setSubject("🎉 Order levererad - Order #" + order.getId());
        message.setText("Din order har levererats!\n\n📋 Orderdetaljer:\nOrdernummer: " + order.getId() +
                "\nTotalt: " + order.getTotalAmount() + " kr\n\nTack för att du handlar hos oss!\n\nMvh,\nCTRLBUY Team");
        mailSender.send(message);
    }

    @Override
    public void sendOrderCancellation(Order order, String customerEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(customerEmail);
        message.setSubject("❌ Order avbruten - Order #" + order.getId());
        message.setText("Din order har tyvärr avbrutits.\n\n📋 Orderdetaljer:\nOrdernummer: " + order.getId() +
                "\nBelopp: " + order.getTotalAmount() + " kr\n\n💰 Återbetalning sker automatiskt inom 3-5 arbetsdagar.\n\nMvh,\nCTRLBUY Team");
        mailSender.send(message);
    }

    @Override
    public boolean testEmailConnection() {
        try {
            return mailSender != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isConfigured() {
        return mailSender != null;
    }
}