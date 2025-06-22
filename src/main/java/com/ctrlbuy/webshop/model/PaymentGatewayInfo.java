package com.ctrlbuy.webshop.model;

import java.util.List;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Information om payment gateway
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentGatewayInfo {
    private String name;
    private String version;
    private String provider;
    private List<String> supportedCardTypes;
    private List<String> supportedCurrencies;
    private boolean supportsRefunds;
    private boolean supportsAuthorization;
    private boolean supportsRecurring;
    private String status;

    // Lombok generates constructors automatically

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public List<String> getSupportedCardTypes() {
        return supportedCardTypes;
    }

    public void setSupportedCardTypes(List<String> supportedCardTypes) {
        this.supportedCardTypes = supportedCardTypes;
    }

    public List<String> getSupportedCurrencies() {
        return supportedCurrencies;
    }

    public void setSupportedCurrencies(List<String> supportedCurrencies) {
        this.supportedCurrencies = supportedCurrencies;
    }

    public boolean isSupportsRefunds() {
        return supportsRefunds;
    }

    public void setSupportsRefunds(boolean supportsRefunds) {
        this.supportsRefunds = supportsRefunds;
    }

    public boolean isSupportsAuthorization() {
        return supportsAuthorization;
    }

    public void setSupportsAuthorization(boolean supportsAuthorization) {
        this.supportsAuthorization = supportsAuthorization;
    }

    public boolean isSupportsRecurring() {
        return supportsRecurring;
    }

    public void setSupportsRecurring(boolean supportsRecurring) {
        this.supportsRecurring = supportsRecurring;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}