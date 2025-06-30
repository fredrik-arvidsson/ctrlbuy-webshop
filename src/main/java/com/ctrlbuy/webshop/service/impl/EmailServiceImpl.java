package com.ctrlbuy.webshop.service.impl;

import com.ctrlbuy.webshop.model.Order;
import com.ctrlbuy.webshop.security.entity.User;
import com.ctrlbuy.webshop.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Primary
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(User user, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Verifiera ditt CTRLBUY-konto");
        message.setText("Klicka här för att verifiera: /verify?token=" + token);
        mailSender.send(message);
    }

    @Override
    public void sendVerificationEmail(String email, String token, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verifiera ditt CTRLBUY-konto");
        message.setText("Hej " + firstName + "! Klicka här för att verifiera: /verify?token=" + token);
        mailSender.send(message);
    }

    @Override
    public boolean sendVerificationEmail(String email, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Verifiera ditt CTRLBUY-konto");
            message.setText("Klicka här för att verifiera: /verify?token=" + token);
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
        message.setSubject("Välkommen till CTRLBUY!");
        message.setText("Hej " + user.getUsername() + "! Välkommen till CTRLBUY!");
        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(User user, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Återställ lösenord - CTRLBUY");
        message.setText("Klicka här för att återställa: /reset?token=" + resetToken);
        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String email, String resetToken, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Återställ lösenord - CTRLBUY");
        message.setText("Hej " + firstName + "! Klicka här för att återställa: /reset?token=" + resetToken);
        mailSender.send(message);
    }

    @Override
    public boolean sendPasswordResetEmail(String email, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Återställ lösenord - CTRLBUY");
            message.setText("Klicka här för att återställa: /reset?token=" + resetToken);
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
        message.setSubject("Orderbekräftelse - " + orderNumber);
        message.setText("Tack för din beställning! Ordernummer: " + orderNumber);
        mailSender.send(message);
    }

    @Override
    public void sendOrderConfirmation(Order order, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Orderbekräftelse - Order #" + order.getId());
        message.setText("Tack för din beställning!\nOrdernummer: " + order.getId() + 
                       "\nTotalt: " + order.getTotalAmount() + " kr");
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
        message.setSubject("Konto borttaget - CTRLBUY");
        message.setText("Ditt konto har tagits bort av " + adminUsername + ". Anledning: " + reason);
        mailSender.send(message);
    }

    @Override
    public void sendAccountDeactivationNotification(User deactivatedUser, String adminUsername, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(deactivatedUser.getEmail());
        message.setSubject("Konto inaktiverat - CTRLBUY");
        message.setText("Ditt konto har inaktiverats av " + adminUsername + ". Anledning: " + reason);
        mailSender.send(message);
    }

    @Override
    public void sendAccountReactivationNotification(User reactivatedUser, String adminUsername) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(reactivatedUser.getEmail());
        message.setSubject("Konto återaktiverat - CTRLBUY");
        message.setText("Ditt konto har återaktiverats av " + adminUsername);
        mailSender.send(message);
    }

    @Override
    public void sendShippingNotification(Order order, String customerEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(customerEmail);
        message.setSubject("Order skickad - Order #" + order.getId());
        message.setText("Din order har skickats!\nOrdernummer: " + order.getId());
        mailSender.send(message);
    }

    @Override
    public void sendDeliveryConfirmation(Order order, String customerEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(customerEmail);
        message.setSubject("Order levererad - Order #" + order.getId());
        message.setText("Din order har levererats!\nOrdernummer: " + order.getId());
        mailSender.send(message);
    }

    @Override
    public void sendOrderCancellation(Order order, String customerEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(customerEmail);
        message.setSubject("Order avbruten - Order #" + order.getId());
        message.setText("Din order har avbrutits.\nOrdernummer: " + order.getId() + 
                       "\nÅterbetalning sker inom 3-5 arbetsdagar.");
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
