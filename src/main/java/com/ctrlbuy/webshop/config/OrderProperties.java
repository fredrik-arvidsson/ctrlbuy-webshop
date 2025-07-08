package com.ctrlbuy.webshop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.order")
public class OrderProperties {

    private Confirmation confirmation = new Confirmation();

    public Confirmation getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(Confirmation confirmation) {
        this.confirmation = confirmation;
    }

    public static class Confirmation {
        private boolean sendEmail = true;
        private boolean adminNotification = true;
        private String adminEmail;

        public boolean isSendEmail() {
            return sendEmail;
        }

        public void setSendEmail(boolean sendEmail) {
            this.sendEmail = sendEmail;
        }

        public boolean isAdminNotification() {
            return adminNotification;
        }

        public void setAdminNotification(boolean adminNotification) {
            this.adminNotification = adminNotification;
        }

        public String getAdminEmail() {
            return adminEmail;
        }

        public void setAdminEmail(String adminEmail) {
            this.adminEmail = adminEmail;
        }
    }
}