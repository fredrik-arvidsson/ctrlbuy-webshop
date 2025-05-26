package com.ctrlbuy.webshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${server.base-url:http://localhost:8080}")
    private String baseUrl;

    public void sendVerificationEmail(String toEmail, String verificationToken, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Bekräfta ditt konto - CtrlBuy");

            // FIXAD: Ändrat från /verify till /verify-email
            String verificationUrl = baseUrl + "/verify-email?token=" + verificationToken;

            String emailBody = String.format(
                    "Hej %s!\n\n" +
                            "Tack för att du registrerat dig hos CtrlBuy!\n\n" +
                            "För att aktivera ditt konto, klicka på länken nedan:\n" +
                            "%s\n\n" +
                            "Länken är giltig i 24 timmar.\n\n" +
                            "Om du inte registrerat dig hos oss, ignorera detta meddelande.\n\n" +
                            "Vänliga hälsningar,\n" +
                            "CtrlBuy Team",
                    firstName, verificationUrl
            );

            message.setText(emailBody);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Kunde inte skicka bekräftelsemail: " + e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Återställ ditt lösenord - CtrlBuy");

            String resetUrl = baseUrl + "/reset-password?token=" + resetToken;

            String emailBody = String.format(
                    "Hej %s!\n\n" +
                            "Du har begärt att återställa ditt lösenord för CtrlBuy.\n\n" +
                            "Klicka på länken nedan för att skapa ett nytt lösenord:\n" +
                            "%s\n\n" +
                            "Länken är giltig i 1 timme.\n\n" +
                            "Om du inte begärt detta, ignorera detta meddelande.\n\n" +
                            "Vänliga hälsningar,\n" +
                            "CtrlBuy Team",
                    firstName, resetUrl
            );

            message.setText(emailBody);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Kunde inte skicka återställningsmail: " + e.getMessage());
        }
    }
}